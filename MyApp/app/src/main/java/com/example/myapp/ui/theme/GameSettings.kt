package com.example.myapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.model.GameSettings

@Composable
fun GameSettings(
    settings: GameSettings,
    onSettingsUpdate: (GameSettings) -> Unit
) {
    var gameSpeed by remember { mutableFloatStateOf(settings.gameSpeed) }
    var maxCockroaches by remember { mutableIntStateOf(settings.maxCockroaches) }
    var bonusInterval by remember { mutableIntStateOf(settings.bonusInterval) }
    var roundDuration by remember { mutableIntStateOf(settings.roundDuration) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Настройки игры",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text("Скорость игры: ${"%.1f".format(gameSpeed)}x", fontWeight = FontWeight.Medium)
        Slider(
            value = gameSpeed,
            onValueChange = {
                gameSpeed = it
                onSettingsUpdate(settings.copy(gameSpeed = it))
            },
            valueRange = 0.5f..3.0f,
            steps = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Макс. тараканов: $maxCockroaches", fontWeight = FontWeight.Medium)
        Slider(
            value = maxCockroaches.toFloat(),
            onValueChange = {
                maxCockroaches = it.toInt()
                onSettingsUpdate(settings.copy(maxCockroaches = it.toInt()))
            },
            valueRange = 1f..20f,
            steps = 19,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Интервал бонусов: $bonusInterval сек", fontWeight = FontWeight.Medium)
        Slider(
            value = bonusInterval.toFloat(),
            onValueChange = {
                bonusInterval = it.toInt()
                onSettingsUpdate(settings.copy(bonusInterval = it.toInt()))
            },
            valueRange = 10f..60f,
            steps = 50,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Длительность раунда: $roundDuration сек", fontWeight = FontWeight.Medium)
        Slider(
            value = roundDuration.toFloat(),
            onValueChange = {
                roundDuration = it.toInt()
                onSettingsUpdate(settings.copy(roundDuration = it.toInt()))
            },
            valueRange = 30f..300f,
            steps = 27,
            modifier = Modifier.fillMaxWidth()
        )
    }
}