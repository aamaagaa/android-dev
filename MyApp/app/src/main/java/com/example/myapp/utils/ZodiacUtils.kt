package com.example.myapp.utils

import java.util.Calendar

object ZodiacUtils {

    fun getZodiacSign(day: Int, month: Int): String {
        return when (month) {
            0 -> if (day < 20) "Козерог" else "Водолей"
            1 -> if (day < 19) "Водолей" else "Рыбы"
            2 -> if (day < 21) "Рыбы" else "Овен"
            3 -> if (day < 20) "Овен" else "Телец"
            4 -> if (day < 21) "Телец" else "Близнецы"
            5 -> if (day < 21) "Близнецы" else "Рак"
            6 -> if (day < 23) "Рак" else "Лев"
            7 -> if (day < 23) "Лев" else "Дева"
            8 -> if (day < 23) "Дева" else "Весы"
            9 -> if (day < 23) "Весы" else "Скорпион"
            10 -> if (day < 22) "Скорпион" else "Стрелец"
            11 -> if (day < 22) "Стрелец" else "Козерог"
            else -> "Неизвестно"
        }
    }

    fun getZodiacIconResource(zodiacSign: String): Int {
        return when (zodiacSign) {
            "Овен" -> android.R.drawable.star_big_on
            "Телец" -> android.R.drawable.star_big_off
            "Близнецы" -> android.R.drawable.ic_lock_lock
            "Рак" -> android.R.drawable.ic_menu_camera
            "Лев" -> android.R.drawable.ic_menu_compass
            "Дева" -> android.R.drawable.ic_menu_help
            "Весы" -> android.R.drawable.ic_menu_info_details
            "Скорпион" -> android.R.drawable.ic_menu_manage
            "Стрелец" -> android.R.drawable.ic_menu_preferences
            "Козерог" -> android.R.drawable.ic_menu_save
            "Водолей" -> android.R.drawable.ic_menu_share
            "Рыбы" -> android.R.drawable.ic_menu_slideshow
            else -> android.R.drawable.ic_menu_gallery
        }
    }

    fun getCalendarDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return "$day.$month.$year"
    }
}