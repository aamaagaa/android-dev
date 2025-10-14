package com.example.myapp.model

data class Player(
    val fullName: String = "",
    val gender: String = "",
    val course: String = "",
    val difficultyLevel: Int = 1,
    val birthDate: Long = 0L,
    val zodiacSign: String = ""
)