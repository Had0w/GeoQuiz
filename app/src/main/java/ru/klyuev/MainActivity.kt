package ru.klyuev

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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

    private val TAG = "MainActivity"

    private val questionsBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var currentIndex = 0
    private var answeredQuestion = 0;
    private var rightAnswers = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        // added view model
        ViewModelProvider(this)[QuizViewModel::class.java]

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

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = questionsBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)

        trueButton.isEnabled = !questionsBank[currentIndex].answered
        falseButton.isEnabled = !questionsBank[currentIndex].answered
    }

    private fun getPrevQuestion() {
        currentIndex = if (currentIndex - 1 < 0) {
            questionsBank.size -1
        } else {
            currentIndex - 1
        }
        updateQuestion()
    }

    private fun getNextQuestion() {
        currentIndex = (currentIndex + 1) % questionsBank.size
        updateQuestion()
    }

    private fun checkAnswer(userAnswer: Boolean) {

        val currentRightQuestion = questionsBank[currentIndex].answer

        val messageResId = if (currentRightQuestion == userAnswer) {
            ++rightAnswers
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        questionsBank[currentIndex].answered = true

        trueButton.isEnabled = false
        falseButton.isEnabled = false

        ++answeredQuestion

        if (answeredQuestion == questionsBank.size) {
            showResult()
        } else {
            getNextQuestion()
        }
    }

    private fun showResult() {
        var dialog = AlertDialog.Builder(this)
        dialog.setMessage(R.string.right_percent)

        dialog.setTitle(R.string.right_percent)
        dialog.setMessage("${rightAnswers * 100 / questionsBank.size} %")
        dialog.setPositiveButton("Ok") {dialog, id ->
            questionsBank.forEach { q -> q.answered = false }
            currentIndex = 0
            answeredQuestion = 0
            rightAnswers = 0
            getNextQuestion()
            dialog.cancel()
        }
        dialog.create().show()
    }
}