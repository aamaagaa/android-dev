package com.example.myapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String = "",
    val gender: String = "",
    val course: String = "",
    val difficultyLevel: Int = 1,
    val birthDate: Long = 0L,
    val zodiacSign: String = "",
    val createdAt: Long = System.currentTimeMillis()
)