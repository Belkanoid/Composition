package com.belkanoid.composition.domain.entety

data class Question(
    val sum : Int,
    val visibleNumber : Int,
    val option: List<Int>,
) {
    val rightAnswer = sum - visibleNumber
}