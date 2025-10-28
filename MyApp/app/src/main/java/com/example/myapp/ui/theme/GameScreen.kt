package com.example.myapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapp.model.BugGameState
import com.example.myapp.model.Insect
import com.example.myapp.model.InsectType
import kotlinx.coroutines.delay
import android.widget.ImageView
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.example.myapp.model.Player
import com.example.myapp.repository.GameRepository
import com.example.myapp.database.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    player: Player?,
    onBackToMenu: () -> Unit,
    gameSettings: com.example.myapp.model.GameSettings
) {
    val gameState = remember { BugGameState() }
    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }
    var showSaveDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { GameRepository(database) }
    val coroutineScope = rememberCoroutineScope()

    // Сохраняем результат когда игра заканчивается
    LaunchedEffect(gameState.isGameRunning) {
        if (!gameState.isGameRunning && gameState.score > 0 && player != null) {
            showSaveDialog = true
        }
    }

    LaunchedEffect(gameState.isGameRunning) {
        if (gameState.isGameRunning) {
            while (gameState.isGameRunning) {
                delay(50)
                gameState.updateInsects(screenWidth, screenHeight)
                gameState.addInsect(screenWidth, screenHeight)
            }
        }
    }

    LaunchedEffect(gameState.isGameRunning) {
        if (gameState.isGameRunning) {
            while (gameState.isGameRunning && gameState.timeLeft > 0) {
                delay(1000)
                gameState.updateTimer()
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Сохранение результата") },
            text = {
                Text("Ваш результат: ${gameState.score} очков\nСохранить в таблицу рекордов?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (player != null) {
                                repository.saveGameResult(
                                    playerId = player.id,
                                    score = gameState.score,
                                    difficultyLevel = player.difficultyLevel,
                                    gameDuration = gameSettings.roundDuration
                                )
                            }
                            showSaveDialog = false
                            onBackToMenu()
                        }
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                        onBackToMenu()
                    }
                ) {
                    Text("Не сохранять")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Очки: ${gameState.score}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Время: ${gameState.timeLeft}с",
                    fontSize = 14.sp
                )
                if (player != null) {
                    Text(
                        text = "Игрок: ${player.fullName}",
                        fontSize = 12.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!gameState.isGameRunning) {
                    Button(
                        onClick = {
                            if (player != null) {
                                gameState.startGame(gameSettings)
                            }
                        },
                        modifier = Modifier.height(36.dp),
                        enabled = player != null
                    ) {
                        Text("Старт", fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = { gameState.stopGame() },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Стоп", fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = onBackToMenu,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Меню", fontSize = 12.sp)
                }
            }
        }

        if (player == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Выберите игрока для начала игры!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        if (gameState.isGameRunning) {
                            gameState.missedClick()
                        }
                    }
            ) {
                val context = LocalContext.current
                val bitmap = remember {
                    try {
                        val inputStream = context.assets.open("raw/background.jpg")
                        BitmapFactory.decodeStream(inputStream)
                    } catch (e: Exception) {
                        null
                    }
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Фон травы",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    screenWidth = size.width
                    screenHeight = size.height
                }
                gameState.insects.forEach { insect ->
                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(LocalDensity.current) { insect.x.toDp() },
                                y = with(LocalDensity.current) { insect.y.toDp() }
                            )
                            .size(48.dp)
                            .clickable {
                                if (gameState.isGameRunning) {
                                    gameState.removeInsect(insect)
                                }
                            }
                    ) {
                        SvgImage(
                            svgFileName = if (insect.type == InsectType.BUG) "bug.png" else "butterfly.png",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun SvgImage(svgFileName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER

                try {
                    val inputStream = ctx.assets.open("raw/$svgFileName")
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    setImageBitmap(bitmap)
                    inputStream.close()
                } catch (e: Exception) {
                    setImageResource(android.R.drawable.ic_menu_report_image)
                }
            }
        },
        modifier = modifier
    )
}