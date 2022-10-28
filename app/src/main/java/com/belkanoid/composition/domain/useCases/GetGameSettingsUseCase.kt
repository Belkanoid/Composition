package com.belkanoid.composition.domain.useCases

import com.belkanoid.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {

    operator fun invoke(maxSum : Int) {
        repository.generateQuestion(maxSum, COUNT_OF_OPTIONS)
    }


    companion object {
        const val COUNT_OF_OPTIONS = 6
    }
}