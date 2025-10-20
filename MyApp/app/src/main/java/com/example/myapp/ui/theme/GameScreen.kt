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

@Composable
fun GameScreen(onBackToMenu: () -> Unit, gameSettings: com.example.myapp.model.GameSettings) {
    val gameState = remember { BugGameState() }
    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

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
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!gameState.isGameRunning) {
                    Button(
                        onClick = { gameState.startGame(gameSettings) },
                        modifier = Modifier.height(36.dp)
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