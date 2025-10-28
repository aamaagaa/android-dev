package com.example.myapp.database

import androidx.room.*
import com.example.myapp.model.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY fullName")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: Long): Player?

    @Insert
    suspend fun insertPlayer(player: Player): Long

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)
}