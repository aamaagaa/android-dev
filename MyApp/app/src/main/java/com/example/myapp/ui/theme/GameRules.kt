package com.example.myapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapp.R
import androidx.compose.ui.unit.dp

@Composable
fun GameRules(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Правила игры",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onBack) {
                Text("Назад")
            }
        }

        AndroidView(
            factory = { context ->
                android.webkit.WebView(context).apply {
                    loadUrl("file:///android_res/raw/game_rules.html")
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        )
    }
}