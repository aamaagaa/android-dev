package com.example.myapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

data class Insect(
    val id: Int,
    val type: InsectType,
    var x: Float,
    var y: Float,
    var velocityX: Float = Random.nextFloat() * 4 - 2,
    var velocityY: Float = Random.nextFloat() * 4 - 2
)

enum class InsectType {
    BUG, BUTTERFLY
}

class BugGameState {
    var score by mutableStateOf(0)
    var insects by mutableStateOf(listOf<Insect>())
    var isGameRunning by mutableStateOf(false)
    var timeLeft by mutableStateOf(0)
    var gameSettings: GameSettings? = null
    
    fun addInsect(screenWidth: Float, screenHeight: Float) {
        val maxInsects = gameSettings?.maxCockroaches ?: 5
        if (insects.size < maxInsects) {
            val speed = gameSettings?.gameSpeed ?: 1.0f
            val newInsect = Insect(
                id = insects.size,
                type = if (Random.nextBoolean()) InsectType.BUG else InsectType.BUTTERFLY,
                x = Random.nextFloat() * (screenWidth - 48),
                y = Random.nextFloat() * (screenHeight - 48),
                velocityX = (Random.nextFloat() * 4 - 2) * speed,
                velocityY = (Random.nextFloat() * 4 - 2) * speed
            )
            insects = insects + newInsect
        }
    }
    
    fun removeInsect(insect: Insect) {
        insects = insects - insect
        score += 10
    }
    
    fun missedClick() {
        score -= 5
    }
    
    fun startGame(settings: GameSettings) {
        gameSettings = settings
        isGameRunning = true
        score = 0
        insects = emptyList()
        timeLeft = settings.roundDuration
    }
    
    fun updateTimer() {
        if (timeLeft > 0) {
            timeLeft--
        } else {
            stopGame()
        }
    }
    
    fun stopGame() {
        isGameRunning = false
    }
    
    fun updateInsects(screenWidth: Float, screenHeight: Float) {
        val speed = gameSettings?.gameSpeed ?: 1.0f
        insects = insects.map { insect ->
            var newX = insect.x + insect.velocityX * speed
            var newY = insect.y + insect.velocityY * speed
            var newVelX = insect.velocityX
            var newVelY = insect.velocityY
            
            if (newX <= 0 || newX >= screenWidth - 48) {
                newVelX = -newVelX
                newX = if (newX <= 0) 0f else screenWidth - 48
            }
            if (newY <= 0 || newY >= screenHeight - 48) {
                newVelY = -newVelY
                newY = if (newY <= 0) 0f else screenHeight - 48
            }
            
            insect.copy(x = newX, y = newY, velocityX = newVelX, velocityY = newVelY)
        }
    }
}