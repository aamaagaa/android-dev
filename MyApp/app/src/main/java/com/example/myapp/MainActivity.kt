package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.ui.theme.*
import com.example.myapp.model.Author
import com.example.myapp.model.GameSettings
import com.example.myapp.model.Player
import com.example.myapp.repository.GameRepository
import com.example.myapp.database.AppDatabase
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayerRegistrationApp()
        }
    }
}

@Composable
fun PlayerRegistrationApp() {
    var currentScreen by remember { mutableStateOf("player_selection") }
    var gameSettings by remember { mutableStateOf(GameSettings()) }
    var currentPlayer by remember { mutableStateOf<Player?>(null) }

    // ЕДИНЫЙ источник данных для формы регистрации
    var registrationPlayer by remember {
        mutableStateOf(Player())
    }

    val authors = listOf(
        Author("Михальчич Елизавета", android.R.drawable.ic_menu_gallery, "ИП-216")
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                "player_selection" -> PlayerSelectionScreen(
                    onPlayerSelected = { player ->
                        currentPlayer = player
                        currentScreen = "menu"
                    },
                    onNewPlayer = {
                        registrationPlayer = Player() // Сбрасываем форму
                        currentScreen = "registration"
                    },
                    onBack = {
                        currentScreen = "menu"
                    }
                )
                "registration" -> RegistrationScreen(
                    player = registrationPlayer,
                    onPlayerUpdate = { updatedPlayer ->
                        registrationPlayer = updatedPlayer // Обновляем ЕДИНЫЙ источник
                        println("DEBUG: Registration form updated: ${updatedPlayer.fullName}")
                    },
                    onContinueToMenu = {
                        // Переходим к сохранению с АКТУАЛЬНЫМИ данными
                        currentScreen = "saving_player"
                    }
                )
                "saving_player" -> SavingPlayerScreen(
                    player = registrationPlayer, // Используем актуальные данные формы
                    onPlayerSaved = { savedPlayer ->
                        currentPlayer = savedPlayer
                        currentScreen = "menu"
                    },
                    onBack = { currentScreen = "registration" }
                )
                "menu" -> MainMenu(
                    player = currentPlayer,
                    onStartGame = { currentScreen = "game" },
                    onShowRules = { currentScreen = "rules" },
                    onShowAuthors = { currentScreen = "authors" },
                    onShowSettings = { currentScreen = "settings" },
                    onShowRecords = { currentScreen = "records" },
                    onChangePlayer = { currentScreen = "player_selection" }
                )
                "game" -> GameScreen(
                    player = currentPlayer,
                    onBackToMenu = { currentScreen = "menu" },
                    gameSettings = gameSettings
                )
                "rules" -> GameRules(
                    onBack = { currentScreen = "menu" }
                )
                "authors" -> AuthorsList(
                    authors = authors,
                    onBack = { currentScreen = "menu" }
                )
                "settings" -> GameSettings(
                    settings = gameSettings,
                    onSettingsUpdate = { gameSettings = it },
                    onBack = { currentScreen = "menu" }
                )
                "records" -> RecordsScreen(
                    onBack = { currentScreen = "menu" }
                )
            }
        }
    }
}

@Composable
fun SavingPlayerScreen(
    player: Player,
    onPlayerSaved: (Player) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { GameRepository(database) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(player) {
        coroutineScope.launch {
            try {
                // Сохраняем игрока и получаем его ID
                val playerId = repository.insertPlayer(player)
                println("DEBUG: Player saved with ID: $playerId") // Отладочная информация

                // Создаем копию игрока с правильным ID
                val savedPlayer = player.copy(id = playerId)
                println("DEBUG: Saved player: $savedPlayer") // Отладочная информация

                onPlayerSaved(savedPlayer)
            } catch (e: Exception) {
                println("DEBUG: Error saving player: ${e.message}") // Отладочная информация
                errorMessage = "Ошибка сохранения: ${e.message}"
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Сохранение игрока...", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Имя: ${player.fullName}", fontSize = 14.sp, color = Color.Gray)
        } else if (errorMessage != null) {
            Text("Ошибка!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "Неизвестная ошибка", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Назад")
            }
        }
    }
}

@Composable
fun RegistrationScreen(
    player: Player,
    onPlayerUpdate: (Player) -> Unit,
    onContinueToMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        RegistrationForm(
            player = player,
            onPlayerUpdate = onPlayerUpdate, // Прямая передача наружу
            showContinueButton = true,
            onContinue = {
                println("DEBUG: Final player data before save: $player")
                onContinueToMenu()
            }
        )
    }
}

@Composable
fun MainMenu(
    player: Player?,
    onStartGame: () -> Unit,
    onShowRules: () -> Unit,
    onShowAuthors: () -> Unit,
    onShowSettings: () -> Unit,
    onShowRecords: () -> Unit,
    onChangePlayer: () -> Unit
) {
    LaunchedEffect(player) {
        println("DEBUG: Current player in menu: $player")
        println("DEBUG: Player name: ${player?.fullName}")
        println("DEBUG: Player ID: ${player?.id}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Информация о текущем игроке
        if (player != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Текущий игрок:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = player.fullName.ifEmpty { "Без имени" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (player.zodiacSign.isNotEmpty()) {
                        ZodiacSignImage(
                            zodiacSign = player.zodiacSign,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(vertical = 4.dp)
                        )
                        Text(
                            text = player.zodiacSign,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            // Показываем сообщение если игрок не выбран
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Игрок не выбран!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Нажмите 'Сменить игрока'",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Text(
            text = "Игра \"Жуки\"",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Начать игру", fontSize = 18.sp)
        }

        Button(
            onClick = onShowRecords,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Рекорды", fontSize = 18.sp)
        }

        Button(
            onClick = onShowRules,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Правила", fontSize = 18.sp)
        }

        Button(
            onClick = onShowAuthors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Авторы", fontSize = 18.sp)
        }

        Button(
            onClick = onShowSettings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Настройки", fontSize = 18.sp)
        }

        OutlinedButton(
            onClick = onChangePlayer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Сменить игрока", fontSize = 16.sp)
        }
    }
}

@Composable
fun DebugPlayerInfo() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { GameRepository(database) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Проверим есть ли игроки в базе
            val players = repository.getAllPlayers().first()
            println("DEBUG: Total players in DB: ${players.size}")
            players.forEach { player ->
                println("DEBUG: Player in DB - ID: ${player.id}, Name: '${player.fullName}'")
            }

            // Создадим тестового игрока если база пуста
            if (players.isEmpty()) {
                println("DEBUG: Creating test player...")
                val testPlayer = Player(
                    fullName = "Тестовый Игрок",
                    gender = "Мужской",
                    course = "1 курс",
                    difficultyLevel = 5,
                    birthDate = System.currentTimeMillis(),
                    zodiacSign = "Овен"
                )
                val playerId = repository.insertPlayer(testPlayer)
                println("DEBUG: Test player created with ID: $playerId")
            }
        }
    }
}
