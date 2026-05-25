package edu.voiciehb.scheduleteller.schedule

data class ScheduleResponse(
    val time_slots_full_time: Map<String, String>?,
    val time_slots_part_time: Map<String, String>?,
    val schedule: List<Lesson>
) {
    fun getTimeSlotsForMode(studyMode: String?): Map<String, String> {
        return if (studyMode?.equals("part-time", ignoreCase = true) == true) {
            time_slots_part_time ?: emptyMap()
        } else {
            time_slots_full_time ?: emptyMap()
        }
    }
}