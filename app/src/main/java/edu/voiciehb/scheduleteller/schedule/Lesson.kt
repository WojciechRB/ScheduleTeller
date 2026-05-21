package edu.voiciehb.scheduleteller.schedule

data class Lesson(
    val day: String,
    val slot: Int,
    val time: String,      // Dodano: np. "9:45-11:15"
    val subject: String,
    val type: String,      // Dodano: np. "laboratory"
    val mode: String,      // Dodano: np. "teams" lub "on-site"
    val room: String?,     // Dodano: sala (String? ponieważ może być null w przypadku Teams)
    val group: String,     // Dodano: np. "Cosmetology II"
    val dates: List<String>
)