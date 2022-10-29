package com.belkanoid.composition.domain.useCases

import com.belkanoid.composition.domain.entety.GameSettings
import com.belkanoid.composition.domain.entety.Level
import com.belkanoid.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {


    operator fun invoke(level: Level) : GameSettings = repository.getGameSettings(level)




}