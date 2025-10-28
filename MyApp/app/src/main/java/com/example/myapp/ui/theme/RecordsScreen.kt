package com.example.myapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.repository.GameRepository
import com.example.myapp.database.AppDatabase
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.background

@Composable
fun RecordsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { GameRepository(database) }
    val topScores by repository.getTopScores().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    // Отладочная информация
    LaunchedEffect(topScores) {
        println("DEBUG: Top scores count: ${topScores.size}")
        topScores.forEachIndexed { index, score ->
            println("DEBUG: Score $index - PlayerID: ${score.playerId}, Score: ${score.score}")
        }
    }

    // State для хранения имен игроков
    val playerNames = remember { mutableStateMapOf<Long, String>() }

    // Загружаем имена игроков для каждого рекорда
    LaunchedEffect(topScores) {
        topScores.forEach { scoreRecord ->
            if (!playerNames.containsKey(scoreRecord.playerId)) {
                coroutineScope.launch {
                    val player = repository.getPlayerById(scoreRecord.playerId)
                    println("DEBUG: Loading player ${scoreRecord.playerId} -> ${player?.fullName ?: "NOT FOUND"}")
                    playerNames[scoreRecord.playerId] = player?.fullName ?: "Неизвестный игрок"
                }
            }
        }
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Рекорды",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Таблица рекордов",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(onClick = onBack) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Статистика
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = topScores.size.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Всего записей",
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = topScores.maxByOrNull { it.score }?.score?.toString() ?: "0",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Лучший результат",
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Список рекордов
        if (topScores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Нет рекордов",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Пока нет рекордов",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Сыграйте в игру чтобы установить первый рекорд!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(topScores.indexed()) { (index, scoreRecord) ->
                    ScoreRecordItem(
                        scoreRecord = scoreRecord,
                        rank = index + 1,
                        playerName = playerNames[scoreRecord.playerId] ?: "Загрузка..."
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreRecordItem(
    scoreRecord: com.example.myapp.model.ScoreRecord,
    rank: Int,
    playerName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (rank) {
                1 -> Color(0xFFFFD700).copy(alpha = 0.1f) // Золото
                2 -> Color(0xFFC0C0C0).copy(alpha = 0.1f) // Серебро
                3 -> Color(0xFFCD7F32).copy(alpha = 0.1f) // Бронза
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Место и информация об игроке
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Место
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = when (rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            },
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Text(
                        text = "#$rank",
                        fontWeight = FontWeight.Bold,
                        color = when (rank) {
                            1, 2, 3 -> Color.White
                            else -> MaterialTheme.colorScheme.primary
                        },
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Информация об игроке
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Игрок",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = playerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "Уровень сложности: ${scoreRecord.difficultyLevel}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Дата: ${formatDate(scoreRecord.date)}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Очки и длительность
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${scoreRecord.score} очков",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${scoreRecord.gameDuration} сек",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Вспомогательная функция для форматирования даты
private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
    return formatter.format(date)
}

// Вспомогательная функция для получения индекса в items
private fun <T> List<T>.indexed(): List<Pair<Int, T>> = this.mapIndexed { index, value -> index to value }
