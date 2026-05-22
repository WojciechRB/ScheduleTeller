package edu.voiciehb.scheduleteller.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

// Pomocnicza klasa reprezentująca pełny dzień w widoku
data class DaySchedule(
    val dayName: String,       // np. "Poniedziałek", "Wtorek"
    val dayCode: String,       // Pełna nazwa z JSON-a, np. "Monday", "Tuesday"
    val lessons: List<Lesson>  // Lista lekcji w tym dniu
)

class ScheduleManager(private val scheduleResponse: ScheduleResponse) {

    private val currentYear = 2026

    // Mapa łącząca pełne angielskie nazwy z JSON z polskimi nagłówkami w UI
    private val dayNamesMap = mapOf(
        "Monday" to "Poniedziałek",
        "Tuesday" to "Wtorek",
        "Wednesday" to "Środa",
        "Thursday" to "Czwartek",
        "Friday" to "Piątek",
        "Saturday" to "Sobota",
        "Sunday" to "Niedziela"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getScheduleForWeek(targetDate: LocalDate): List<DaySchedule> {
        val startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        // Formaty dat, które mogą pojawić się w Twoim pliku JSON
        val shortFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val longFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val weeklyLessons = scheduleResponse.schedule.filter { lesson ->
            lesson.dates.any { rawDateString ->
                try {
                    val dateString = rawDateString.trim() // Usuwamy przypadkowe spacje

                    val parsedDate = if (dateString.contains("-")) {
                        // Jeśli w JSON jest np. "2026-05-22"
                        LocalDate.parse(dateString, longFormatter)
                    } else {
                        // Jeśli w JSON jest krótki format np. "22.05", budujemy pełny string
                        val fullDateString = if (dateString.endsWith(".")) "$dateString$currentYear" else "$dateString.$currentYear"
                        LocalDate.parse(fullDateString, shortFormatter)
                    }

                    !parsedDate.isBefore(startOfWeek) && !parsedDate.isAfter(endOfWeek)
                } catch (e: Exception) {
                    println("DEBUG_ERROR: Nie udało się sparsować daty: [$rawDateString]")
                    false
                }
            }
        }

        // Mapowanie na pełne nazwy dni, aby dopasować je do wartości z pola 'day' w JSON
        return listOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        ).map { englishDay ->
            val dayLessons = weeklyLessons
                .filter { it.day.equals(englishDay, ignoreCase = true) }
                .sortedBy { it.slot }

            DaySchedule(
                dayName = dayNamesMap[englishDay] ?: englishDay,
                dayCode = englishDay,
                lessons = dayLessons
            )
        }
    }
}