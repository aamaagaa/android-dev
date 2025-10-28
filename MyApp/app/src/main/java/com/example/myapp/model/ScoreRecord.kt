package com.example.myapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "score_records",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScoreRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerId: Long,
    val score: Int,
    val difficultyLevel: Int,
    val gameDuration: Int,
    val date: Long = System.currentTimeMillis()
)
