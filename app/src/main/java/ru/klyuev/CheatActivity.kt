package ru.klyuev

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val EXTRA_ANSWER_IS_TRUE = "ru.klyuev.android.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "ru.klyuev.android.geoquiz.answer_shown"

class CheatActivity : AppCompatActivity() {

    private var answerIsTrue = false
    private lateinit var showAnswerButton: Button
    private lateinit var answerTextView: TextView

    private val cheatViewModel: CheatViewModel by lazy {
        ViewModelProvider(this)[CheatViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        showAnswerButton = findViewById(R.id.show_answer_button)
        answerTextView = findViewById(R.id.answer_textview)

        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue ->
                    R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)

            cheatViewModel.isCheater = true

            setAnswerShownResult(cheatViewModel.isCheater)
        }

        setAnswerShownResult(cheatViewModel.isCheater)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply { putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown) }
        setResult(Activity.RESULT_OK, data)
    }
}