package otus.homework.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.net.SocketTimeoutException

class CatsViewModel(
    private val catsService: CatsService,
    private val meowService: MeowService
) : ViewModel() {

    private val _state = MutableLiveData<Result>()
    val state: LiveData<Result> = _state

    private val exceptionHandler = CoroutineExceptionHandler() { _, _ ->
        CrashMonitor.trackWarning()
    }

    fun onInitComplete() {
        viewModelScope.launch(exceptionHandler) {
            try {
                val fact = async {
                    catsService.getCatFact()
                }
                val imageUrl = async {
                    meowService.getMeow().file
                }
                _state.value = Result.Success(UiState(fact.await(), imageUrl.await()))
            } catch (e: Exception) {
                when(e) {
                    is CancellationException -> throw e
                    is SocketTimeoutException -> _state.value = Result.Error("Не удалось получить ответ от сервера")
                    else -> {
                        CrashMonitor.trackWarning()
                        _state.value = Result.Error(e.message ?: "")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}