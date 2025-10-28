package com.example.myapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.repository.GameRepository
import com.example.myapp.database.AppDatabase
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.myapp.model.Player
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable

@Composable
fun PlayerSelectionScreen(
    onPlayerSelected: (Player) -> Unit,
    onNewPlayer: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { GameRepository(database) }
    val players by repository.getAllPlayers().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Выбор игрока",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onBack) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNewPlayer,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Новый игрок", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (players.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет зарегистрированных игроков", fontSize = 16.sp)
            }
        } else {
            Text("Выберите игрока:", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(players) { player ->
                    PlayerItem(
                        player = player,
                        onPlayerSelected = onPlayerSelected
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerItem(
    player: Player,
    onPlayerSelected: (Player) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPlayerSelected(player) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Курс: ${player.course}, Уровень: ${player.difficultyLevel}",
                    fontSize = 14.sp
                )
                if (player.zodiacSign.isNotEmpty()) {
                    Text(
                        text = "Знак: ${player.zodiacSign}",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
