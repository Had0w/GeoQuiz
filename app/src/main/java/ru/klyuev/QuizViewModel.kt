package ru.klyuev

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    var currentIndex = 0
    var answeredQuestion = 0
    var rightAnswers = 0
    var isCheater = false
    var countOfCheating = 0;

    val currentQuestionAnswer: Boolean
    get() = questionsBank[currentIndex].answer

    val currentQuestionText: Int
    get() = questionsBank[currentIndex].textResId

    fun isAnswered(): Boolean {
        return questionsBank[currentIndex].answered
    }

    fun setAnswered() {
        questionsBank[currentIndex].answered = true
    }

    fun isAllQuestionsAnswered(): Boolean {
        return answeredQuestion == questionsBank.size
    }

    fun getRightAnswersPercent(): Int {
        return rightAnswers * 100 / questionsBank.size
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionsBank.size
    }

    fun moveToPrev() {
        currentIndex = if (currentIndex - 1 < 0) {
            questionsBank.size -1
        } else {
            currentIndex - 1
        }
    }

    fun resetParameters() {
        questionsBank.forEach { q -> q.answered = false }
        currentIndex = 0
        answeredQuestion = 0
        rightAnswers = 0
        isCheater = false
        countOfCheating = 0
    }

    private val questionsBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
}