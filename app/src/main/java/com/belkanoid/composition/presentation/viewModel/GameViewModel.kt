package com.belkanoid.composition.presentation.viewModel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.belkanoid.composition.R
import com.belkanoid.composition.data.GameRepositoryImpl
import com.belkanoid.composition.domain.entety.GameResult
import com.belkanoid.composition.domain.entety.GameSettings
import com.belkanoid.composition.domain.entety.Level
import com.belkanoid.composition.domain.entety.Question
import com.belkanoid.composition.domain.useCases.GenerateQuestionUseCase
import com.belkanoid.composition.domain.useCases.GetGameSettingsUseCase

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepositoryImpl

    private lateinit var timer: CountDownTimer

    private val context = application
    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    /**
     * UseCases
     * */

    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private val getQuestionUseCase = GenerateQuestionUseCase(repository)

    /**
     * LiveData
     * */

    private val _gameSettings: MutableLiveData<GameSettings> = MutableLiveData()

    private val _gameQuestion: MutableLiveData<Question> = MutableLiveData()
    val gameQuestion: LiveData<Question>
        get() = _gameQuestion

    private val _gameResult : MutableLiveData<GameResult> = MutableLiveData()
    val gameResult : LiveData<GameResult>
        get() = _gameResult

    private val _formatTime : MutableLiveData<String> = MutableLiveData()
    val formatTime : LiveData<String>
        get() = _formatTime

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    /**
     * GameViewModel Logic
     * */

    fun startGame(level: Level) {
        getGameSettings(level)
        updateProgress()
        startTimer()
        getQuestion()
    }

    fun chooseAnswer(answer: Int) {
        checkAnswer(answer)
        updateProgress()
        getQuestion()
    }

    private fun checkAnswer(answer : Int) {
        val rightAnswer = _gameQuestion.value!!.rightAnswer
        if (answer == rightAnswer)
            countOfRightAnswers++

        countOfQuestions++
    }

    private fun getGameSettings(level: Level) {
        _gameSettings.value = getGameSettingsUseCase(level)
        _minPercent.value = _gameSettings.value!!.minPercentOfRightAnswers

    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            context.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            _gameSettings.value!!.minCountOfRightAnswers
        )
        _enoughPercent.value = countOfRightAnswers >= _gameSettings.value!!.minCountOfRightAnswers
        _enoughPercent.value = percent >= _gameSettings.value!!.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        return ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun getQuestion() {
        val maxSumValue = _gameSettings.value!!.maxSumValue
        _gameQuestion.value = getQuestionUseCase(maxSumValue)
    }

    private fun startTimer() {
        val countDown = _gameSettings.value!!.gameTimeInSeconds * SECOND_IN_MILLIS
        timer = object : CountDownTimer(countDown, SECOND_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                _formatTime.value = formatTime(millisUntilFinished)
            }
            override fun onFinish() {
                finishGame()
            }
        }.start()
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswers,
            countOfQuestions,
            _gameSettings.value!!
        )
    }

    private fun formatTime(millisUntilFinished : Long) : String {
        val seconds = millisUntilFinished / SECOND_IN_MILLIS
        val minutes = seconds / SECOND_IN_MILLIS
        val leftSeconds = seconds - (minutes * SECOND_IN_MILLIS)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }



    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    companion object {
        const val SECOND_IN_MILLIS = 1000L
    }

}