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
import java.util.*
import com.example.myapp.model.Player
import com.example.myapp.utils.ZodiacUtils
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapp.ui.theme.RegistrationForm
import com.example.myapp.ui.theme.GameRules
import com.example.myapp.ui.theme.GameSettings
import com.example.myapp.model.Author
import com.example.myapp.model.GameSettings
import com.example.myapp.ui.theme.AuthorsList
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
    var player by remember { mutableStateOf(Player()) }
    var gameSettings by remember { mutableStateOf(GameSettings()) }

    val authors = listOf(
        Author("Михальчич Елизавета", android.R.drawable.ic_menu_gallery, "ИП-216")
    )

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TabLayout(
                player = player,
                onPlayerUpdate = { player = it },
                gameSettings = gameSettings,
                onSettingsUpdate = { gameSettings = it },
                authors = authors
            )
        }
    }
}

@Composable
fun TabLayout(
    player: Player,
    onPlayerUpdate: (Player) -> Unit,
    gameSettings: GameSettings,
    onSettingsUpdate: (GameSettings) -> Unit,
    authors: List<Author>
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Регистрация", "Правила", "Авторы", "Настройки")

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(24.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> RegistrationForm(
                    player = player,
                    onPlayerUpdate = onPlayerUpdate
                )
                1 -> GameRules()
                2 -> AuthorsList(authors = authors)
                3 -> GameSettings(
                    settings = gameSettings,
                    onSettingsUpdate = onSettingsUpdate
                )
            }
        }
    }
}