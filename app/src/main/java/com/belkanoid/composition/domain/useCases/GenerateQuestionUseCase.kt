package com.belkanoid.composition.domain.useCases

import com.belkanoid.composition.domain.entety.GameSettings
import com.belkanoid.composition.domain.entety.Level
import com.belkanoid.composition.domain.entety.Question
import com.belkanoid.composition.domain.repository.GameRepository

class GenerateQuestionUseCase(private val repository : GameRepository) {

    operator fun invoke(maxSum : Int) : Question{
        return repository.generateQuestion(maxSum, COUNT_OF_OPTIONS)
    }

    companion object {
        const val COUNT_OF_OPTIONS = 6
    }
}