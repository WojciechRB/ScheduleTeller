package edu.voiciehb.scheduleteller.schedule

data class Lesson(
    val day: String,
    val study_mode: String?,
    val slot: Int,
    val time: String,
    val subject: String,
    val type: String,
    val mode: String,
    val room: String?,
    val group: String,
    val dates: List<String>
)