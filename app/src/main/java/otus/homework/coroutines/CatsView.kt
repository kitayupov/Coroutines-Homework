package otus.homework.coroutines

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso

class CatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ICatsView {

    var presenter : CatsPresenter? = null

    var viewModel : CatsViewModel? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        findViewById<Button>(R.id.button).setOnClickListener {
//            presenter?.onInitComplete()
            viewModel?.onInitComplete()
        }
    }

    override fun populate(state: Result) {
        when(state) {
            is Result.Success -> showState(state.uiState)
            is Result.Error -> showError(state.message)
        }
    }

    private fun showState(state: UiState) {
        findViewById<TextView>(R.id.fact_textView).text = state.fact.text
        Picasso.Builder(context).build()
            .load(state.imageUrl)
            .into(findViewById<ImageView>(R.id.meow_imageView))
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

interface ICatsView {

    fun populate(state: Result)
}