package com.example.myapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapp.model.Player
import com.example.myapp.utils.ZodiacUtils
import androidx.compose.ui.unit.sp
import java.util.*
import android.widget.ImageView
import android.graphics.BitmapFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationForm(
    player: Player,
    onPlayerUpdate: (Player) -> Unit,
    showContinueButton: Boolean = false,
    onContinue: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf(player.fullName) }
    var selectedGender by remember { mutableStateOf(player.gender) }
    var selectedCourse by remember { mutableStateOf(player.course) }
    var difficultyLevel by remember { mutableIntStateOf(player.difficultyLevel) }
    var selectedDate by remember { mutableLongStateOf(player.birthDate) }

    val calendar = Calendar.getInstance()
    if (selectedDate != 0L) {
        calendar.timeInMillis = selectedDate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Регистрация игрока",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                onPlayerUpdate(player.copy(fullName = it))
            },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Пол:", fontWeight = FontWeight.Medium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedGender == "Мужской",
                    onClick = {
                        selectedGender = "Мужской"
                        onPlayerUpdate(player.copy(gender = "Мужской"))
                    }
                )
                Text("Мужской", modifier = Modifier.padding(start = 4.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedGender == "Женский",
                    onClick = {
                        selectedGender = "Женский"
                        onPlayerUpdate(player.copy(gender = "Женский"))
                    }
                )
                Text("Женский", modifier = Modifier.padding(start = 4.dp))
            }
        }

        Text("Курс:", fontWeight = FontWeight.Medium)
        val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс", "6 курс")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCourse,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course) },
                        onClick = {
                            selectedCourse = course
                            onPlayerUpdate(player.copy(course = course))
                            expanded = false
                        }
                    )
                }
            }
        }

        Text("Уровень сложности: $difficultyLevel", fontWeight = FontWeight.Medium)
        Slider(
            value = difficultyLevel.toFloat(),
            onValueChange = {
                difficultyLevel = it.toInt()
                onPlayerUpdate(player.copy(difficultyLevel = it.toInt()))
            },
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Дата рождения:", fontWeight = FontWeight.Medium)
        Text(
            text = if (selectedDate != 0L) ZodiacUtils.getCalendarDate(calendar) else "Не выбрана",
            modifier = Modifier.padding(vertical = 8.dp)
        )

        AndroidView(
            factory = { context ->
                android.widget.CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val newCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }
                        selectedDate = newCalendar.timeInMillis

                        val zodiacSign = ZodiacUtils.getZodiacSign(dayOfMonth, month)
                        onPlayerUpdate(player.copy(
                            birthDate = selectedDate,
                            zodiacSign = zodiacSign
                        ))
                    }
                }
            },
            modifier = Modifier.height(300.dp)
        )

        if (player.zodiacSign.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ваш знак зодиака:",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ZodiacSignImage(
                    zodiacSign = player.zodiacSign,
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = player.zodiacSign,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (showContinueButton) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = fullName.isNotEmpty() && selectedDate != 0L
            ) {
                Text("Продолжить", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ZodiacSignImage(zodiacSign: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val bitmap = remember(zodiacSign) {
        try {
            val fileName = ZodiacUtils.getZodiacIconFileName(zodiacSign)
            val inputStream = context.assets.open("raw/$fileName")
            BitmapFactory.decodeStream(inputStream).also {
                inputStream.close()
            }
        } catch (e: Exception) {
            null
        }
    }

    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        },
        update = { imageView ->
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        },
        modifier = modifier
    )
}