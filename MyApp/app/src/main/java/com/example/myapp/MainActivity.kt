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
import com.example.myapp.ui.theme.GameRules
import com.example.myapp.ui.theme.GameSettings
import com.example.myapp.ui.theme.GameScreen
import com.example.myapp.ui.theme.RegistrationForm
import com.example.myapp.model.Author
import com.example.myapp.model.GameSettings
import com.example.myapp.model.Player
import com.example.myapp.ui.theme.AuthorsList
import com.example.myapp.ui.theme.ZodiacSignImage
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
    var currentScreen by remember { mutableStateOf("registration") }
    var gameSettings by remember { mutableStateOf(GameSettings()) }
    var player by remember { mutableStateOf(Player()) }

    val authors = listOf(
        Author("Михальчич Елизавета", android.R.drawable.ic_menu_gallery, "ИП-216")
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                "registration" -> RegistrationScreen(
                    player = player,
                    onPlayerUpdate = { updatedPlayer ->
                        player = updatedPlayer
                    },
                    onContinueToMenu = {
                        currentScreen = "menu"
                    }
                )
                "menu" -> MainMenu(
                    player = player,
                    onStartGame = { currentScreen = "game" },
                    onShowRules = { currentScreen = "rules" },
                    onShowAuthors = { currentScreen = "authors" },
                    onShowSettings = { currentScreen = "settings" },
                    onShowRegistration = { currentScreen = "registration" }
                )
                "game" -> GameScreen(
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
            onPlayerUpdate = onPlayerUpdate,
            showContinueButton = true,
            onContinue = onContinueToMenu
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinueToMenu,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = player.fullName.isNotEmpty() && player.birthDate != 0L
        ) {
            Text("Продолжить в меню", fontSize = 16.sp)
        }
    }
}

@Composable
fun MainMenu(
    player: Player,
    onStartGame: () -> Unit,
    onShowRules: () -> Unit,
    onShowAuthors: () -> Unit,
    onShowSettings: () -> Unit,
    onShowRegistration: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


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
            onClick = onShowRegistration,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Редактировать профиль", fontSize = 16.sp)
        }
    }
}