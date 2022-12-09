package ru.klyuev

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_textview)
        questionTextView.setOnClickListener {  }

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        prevButton.setOnClickListener {
            getPrevQuestion()
        }

        nextButton.setOnClickListener {
            getNextQuestion()
        }

        questionTextView.setOnClickListener {
            getNextQuestion()
        }

        updateQuestion()
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

        val messageResId = if (currentRightQuestion == userAnswer) {
            ++quizViewModel.rightAnswers
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

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
        var dialog = AlertDialog.Builder(this)
        dialog.setMessage(R.string.right_percent)

        dialog.setTitle(R.string.right_percent)
        dialog.setMessage("${quizViewModel.getRightAnswersPercent()} %")
        dialog.setPositiveButton("Ok") {dialog, id ->
            quizViewModel.resetParameters()
            getNextQuestion()
            dialog.cancel()
        }
        dialog.create().show()
    }
}