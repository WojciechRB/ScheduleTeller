package edu.voiciehb.scheduleteller.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class DaySchedule(
    val dayName: String,
    val dayCode: String,
    val lessons: List<Lesson>
)

class ScheduleManager(private val scheduleResponse: ScheduleResponse) {

    private val currentYear = 2026

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

        val shortFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val longFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val weeklyLessons = scheduleResponse.schedule.filter { lesson ->
            lesson.dates.any { rawDateString ->
                try {
                    val dateString = rawDateString.trim()

                    val parsedDate = if (dateString.contains("-")) {
                        LocalDate.parse(dateString, longFormatter)
                    } else {
                        val fullDateString =
                            if (dateString.endsWith(".")) "$dateString$currentYear" else "$dateString.$currentYear"
                        LocalDate.parse(fullDateString, shortFormatter)
                    }

                    !parsedDate.isBefore(startOfWeek) && !parsedDate.isAfter(endOfWeek)
                } catch (e: Exception) {
                    println("DEBUG_ERROR: Nie udało się sparsować daty: [$rawDateString]")
                    false
                }
            }
        }

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