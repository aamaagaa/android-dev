package com.example.myapp.model

data class GameState(
    val score: Int = 0,
    val isPlaying: Boolean = false,
    val timeLeft: Int = 0,
    val bugs: List<Bug> = emptyList(),
    val missedClicks: Int = 0
)

data class Bug(
    val id: Int,
    val x: Float,
    val y: Float,
    val speed: Float,
    val type: BugType,
    val direction: Float
)

enum class BugType {
     BEETLE, BUTTERFLY
}