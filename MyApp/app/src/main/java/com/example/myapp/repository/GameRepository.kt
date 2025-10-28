package com.example.myapp.repository

import com.example.myapp.database.AppDatabase
import com.example.myapp.model.Player
import com.example.myapp.model.ScoreRecord
import kotlinx.coroutines.flow.Flow

class GameRepository(private val database: AppDatabase) {

    // Player operations
    fun getAllPlayers(): Flow<List<Player>> = database.playerDao().getAllPlayers()

    suspend fun getPlayerById(playerId: Long): Player? = database.playerDao().getPlayerById(playerId)

    suspend fun insertPlayer(player: Player): Long = database.playerDao().insertPlayer(player)

    suspend fun updatePlayer(player: Player) = database.playerDao().updatePlayer(player)

    suspend fun deletePlayer(player: Player) = database.playerDao().deletePlayer(player)

    // Score operations
    fun getTopScores(): Flow<List<ScoreRecord>> = database.scoreRecordDao().getTopScores()

    fun getPlayerScores(playerId: Long): Flow<List<ScoreRecord>> = database.scoreRecordDao().getPlayerScores(playerId)

    suspend fun insertScoreRecord(scoreRecord: ScoreRecord): Long = database.scoreRecordDao().insertScoreRecord(scoreRecord)

    suspend fun deletePlayerScores(playerId: Long) = database.scoreRecordDao().deletePlayerScores(playerId)

    // Combined operations
    suspend fun saveGameResult(playerId: Long, score: Int, difficultyLevel: Int, gameDuration: Int) {
        val scoreRecord = ScoreRecord(
            playerId = playerId,
            score = score,
            difficultyLevel = difficultyLevel,
            gameDuration = gameDuration
        )
        insertScoreRecord(scoreRecord)
    }
}
