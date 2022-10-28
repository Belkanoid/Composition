package com.belkanoid.composition.domain.repository

import com.belkanoid.composition.domain.entety.GameSettings
import com.belkanoid.composition.domain.entety.Level
import com.belkanoid.composition.domain.entety.Question

interface GameRepository {
    fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question

    fun getGameSettings(level: Level): GameSettings
}