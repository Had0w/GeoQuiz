package ru.klyuev

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var showAnswerButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        showAnswerButton = findViewById(R.id.show_answer_button)
        questionTextView = findViewById(R.id.question_textview)
        questionTextView.setOnClickListener { }

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        prevButton.setOnClickListener {
            if (quizViewModel.isCheater) {
                Toast.makeText(this, R.string.next_after_cheating, Toast.LENGTH_SHORT).show()
            } else {
                getPrevQuestion()
            }
        }

        nextButton.setOnClickListener {
            if (quizViewModel.isCheater) {
                Toast.makeText(this, R.string.next_after_cheating, Toast.LENGTH_SHORT).show()
            } else {
                getNextQuestion()
            }
        }

        questionTextView.setOnClickListener {
            getNextQuestion()
        }

        showAnswerButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions
                    .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent,
                    REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }


    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)

        trueButton.isEnabled = !quizViewModel.isAnswered()
        falseButton.isEnabled = !quizViewModel.isAnswered()
    }

    private fun getPrevQuestion() {
        quizViewModel.moveToPrev()
        updateQuestion()
    }

    private fun getNextQuestion() {
        quizViewModel.moveToNext();
        updateQuestion()
    }

    private fun checkAnswer(userAnswer: Boolean) {

        val currentRightQuestion = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> {
                if (userAnswer == currentRightQuestion) {
                    ++quizViewModel.rightAnswers
                }
                ++quizViewModel.countOfCheating
                R.string.judgment_toast
            }
            userAnswer == currentRightQuestion -> {
                ++quizViewModel.rightAnswers
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }

        quizViewModel.isCheater = false
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        quizViewModel.setAnswered()

        trueButton.isEnabled = false
        falseButton.isEnabled = false

        ++quizViewModel.answeredQuestion

        if (quizViewModel.isAllQuestionsAnswered()) {
            showResult()
        } else {
            getNextQuestion()
        }
    }

    private fun showResult() {
        val resultDialog = AlertDialog.Builder(this)

        resultDialog.setTitle(R.string.right_percent)
        resultDialog.setMessage("${quizViewModel.getRightAnswersPercent()}%")

        val cheatingDialog = AlertDialog.Builder(this)
        cheatingDialog.setTitle(R.string.cheating_times)
        cheatingDialog.setMessage("${quizViewModel.countOfCheating}")

        resultDialog.setPositiveButton("Ok") { dialog, id ->
            cheatingDialog.create().show()
            dialog.cancel()
        }

        resultDialog.create().show()

        cheatingDialog.setPositiveButton("Ok") { dialog2, id ->
            quizViewModel.resetParameters()
            getNextQuestion()
            dialog2.cancel()
        }
    }
}