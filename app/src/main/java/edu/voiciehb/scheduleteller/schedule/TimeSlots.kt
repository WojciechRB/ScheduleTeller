package edu.voiciehb.scheduleteller.schedule

import com.fasterxml.jackson.annotation.JsonProperty

data class TimeSlots(
    @JsonProperty("full_time")
    val fullTime: Map<String, String>,

    val extramural: Map<String, String>
)