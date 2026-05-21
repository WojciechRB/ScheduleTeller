package edu.voiciehb.scheduleteller.main

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule // 👈 Pamiętaj o tym imporcie
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import edu.voiciehb.scheduleteller.schedule.ScheduleResponse
import java.io.IOException

class ScheduleRepository(private val context: Context) {

    // Poprawiona konfiguracja ObjectMappera obsługująca Kotlina oraz Java 8 Date/Time (MonthDay)
    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule()) // 👈 Rejestracja modułu dat
    }

    fun loadScheduleFromAssets(): ScheduleResponse? {
        return try {
            val inputStream = context.assets.open("schedule.json")
            objectMapper.readValue(inputStream, ScheduleResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}