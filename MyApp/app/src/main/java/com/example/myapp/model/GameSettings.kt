package com.example.myapp.model

data class GameSettings(
    val gameSpeed: Float = 1.0f,
    val maxCockroaches: Int = 10,
    val bonusInterval: Int = 30,
    val roundDuration: Int = 120
)