package com.example.googlehealthconnect

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convertStringToDate(s: String): Long{
    // Задаем формат даты
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return try {
        // Преобразуем строку в LocalDate
        val localDate = LocalDate.parse(s, formatter)

        // Преобразуем LocalDate в количество секунд с начала эпохи Unix
        localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
    } catch (e: Exception) {
        0L
    }
}