package com.example.myapp.database

import androidx.room.*
import com.example.myapp.model.ScoreRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreRecordDao {
    @Query("SELECT * FROM score_records ORDER BY score DESC LIMIT 50")
    fun getTopScores(): Flow<List<ScoreRecord>>

    @Query("SELECT * FROM score_records WHERE playerId = :playerId ORDER BY score DESC")
    fun getPlayerScores(playerId: Long): Flow<List<ScoreRecord>>

    @Insert
    suspend fun insertScoreRecord(scoreRecord: ScoreRecord): Long

    @Query("DELETE FROM score_records WHERE playerId = :playerId")
    suspend fun deletePlayerScores(playerId: Long)
}
