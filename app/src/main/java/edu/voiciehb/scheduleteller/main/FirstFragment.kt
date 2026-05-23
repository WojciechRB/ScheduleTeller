package edu.voiciehb.scheduleteller.main

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import edu.voiciehb.scheduleteller.R
import edu.voiciehb.scheduleteller.databinding.FragmentFirstBinding
import edu.voiciehb.scheduleteller.schedule.DaySchedule
import edu.voiciehb.scheduleteller.schedule.ScheduleManager
import edu.voiciehb.scheduleteller.schedule.ScheduleResponse
import java.io.InputStreamReader
import java.time.LocalDate

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val timeSlotsMap = mapOf(
        1 to "8:00 - 9:30",
        2 to "9:45 - 11:15",
        3 to "11:30 - 13:00",
        4 to "13:15 - 14:45",
        5 to "15:00 - 16:30",
        6 to "16:45 - 18:15",
        7 to "18:30 - 20:00"
    )

    private val typeTranslationMap = mapOf(
        "laboratory" to "Laboratorium",
        "tutorial" to "Ćwiczenia",
        "lecture" to "Wykład"
    )

    private val modeTranslationMap = mapOf(
        "teams" to "Online (Teams)",
        "on-site" to "Stacjonarnie"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scheduleResponse = try {
            val assetManager = requireContext().assets
            val inputStream = assetManager.open("schedule.json")
            val reader = InputStreamReader(inputStream)
            Gson().fromJson(reader, ScheduleResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (scheduleResponse == null) return

        val manager = ScheduleManager(scheduleResponse)
        val todayDate = LocalDate.now()
        val weekSchedule = manager.getScheduleForWeek(todayDate)

        val tabLayout = binding.dayTabLayout
        tabLayout.removeAllTabs()

        weekSchedule.forEach { daySchedule ->
            val tab = tabLayout.newTab().apply {
                text = daySchedule.dayName
                tag = daySchedule
            }
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val daySchedule = tab?.tag as? DaySchedule
                if (daySchedule != null) {
                    displayDay(daySchedule)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val currentDayOfWeek = todayDate.dayOfWeek.value
        val defaultTabIndex = currentDayOfWeek - 1

        if (defaultTabIndex in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(defaultTabIndex)?.select()
            displayDay(weekSchedule[defaultTabIndex])
        } else {
            tabLayout.getTabAt(0)?.select()
            displayDay(weekSchedule[0])
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDay(daySchedule: DaySchedule) {
        val container = binding.scheduleContainer
        container.removeAllViews()

        val today = LocalDate.now().dayOfWeek
        val selectedDayOfWeek = try {
            java.time.DayOfWeek.valueOf(daySchedule.dayCode.uppercase(java.util.Locale.ROOT))
        } catch (e: Exception) {
            null
        }
        val isDayPast = selectedDayOfWeek != null && today.value > selectedDayOfWeek.value

        // -------------------------------------------------------------------------
        // OSTATECZNE ROZWIĄZANIE: Używamy TYLKO wbudowanych atrybutów Androida.
        // Te atrybuty istnieją w 100% projektów i nie wymagają Material Design.
        // -------------------------------------------------------------------------
        val textColorPrimary = getThemeColor(android.R.attr.textColorPrimary)
        val textColorSecondary = getThemeColor(android.R.attr.textColorSecondary)
        val cardBackgroundColor = getThemeColor(android.R.attr.colorBackground)
        val breakBackgroundColor = getThemeColor(android.R.attr.colorBackground)
        val breakStrokeColor = Color.GRAY
        val accentColor = getThemeColor(android.R.attr.colorAccent)
        // -------------------------------------------------------------------------

        // Nagłówek dnia
        val titleHeader = TextView(context).apply {
            text = if (isDayPast) {
                "Plan na: ${daySchedule.dayName} (Dzień już minął)"
            } else {
                "Plan na: ${daySchedule.dayName}"
            }
            textSize = 18f
            setTextColor(if (isDayPast) Color.parseColor("#888888") else textColorPrimary)
            setTypeface(null, Typeface.BOLD)
            setPadding(8, 0, 0, 24)
        }
        container.addView(titleHeader)

        val sortedLessons = daySchedule.lessons.sortedBy { it.slot }

        if (sortedLessons.isEmpty()) {
            val noLessonsTv = TextView(context).apply {
                text = "Brak zaplanowanych zajęć w tym dniu."
                textSize = 14f
                setTextColor(Color.GRAY)
                setPadding(8, 16, 0, 0)
            }
            container.addView(noLessonsTv)
        } else {
            for (i in sortedLessons.indices) {
                val lesson = sortedLessons[i]

                val lessonBlock = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(24, 24, 24, 24)

                    val blockParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 16)
                    }
                    layoutParams = blockParams

                    val blockDrawable = GradientDrawable().apply {
                        setColor(cardBackgroundColor)
                        cornerRadius = 24f
                    }
                    background = blockDrawable

                    if (isDayPast) alpha = 0.4f
                }

                val timeTv = TextView(context).apply {
                    text = timeSlotsMap[lesson.slot] ?: lesson.time
                    textSize = 12f
                    setTextColor(accentColor)
                    setTypeface(null, Typeface.BOLD)
                }
                lessonBlock.addView(timeTv)

                val subjectTv = TextView(context).apply {
                    text = lesson.subject
                    textSize = 16f
                    setTextColor(textColorPrimary)
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, 6, 0, 4)
                }
                lessonBlock.addView(subjectTv)

                val detailsTv = TextView(context).apply {
                    val translatedType = typeTranslationMap[lesson.type.lowercase()] ?: lesson.type
                    val translatedModeOrRoom =
                        if (lesson.room != null) "Sala: ${lesson.room}" else modeTranslationMap[lesson.mode.lowercase()]
                            ?: lesson.mode
                    text = "$translatedType | $translatedModeOrRoom"
                    textSize = 13f
                    setTextColor(textColorSecondary)
                }
                lessonBlock.addView(detailsTv)

                val groupTv = TextView(context).apply {
                    text = "Grupa: ${lesson.group}"
                    textSize = 12f
                    setTextColor(Color.GRAY)
                    setPadding(0, 6, 0, 0)
                }
                lessonBlock.addView(groupTv)

                container.addView(lessonBlock)

                if (i < sortedLessons.size - 1) {
                    val nextLesson = sortedLessons[i + 1]

                    val currentString = timeSlotsMap[lesson.slot] ?: lesson.time
                    val nextString = timeSlotsMap[nextLesson.slot] ?: nextLesson.time

                    val currentEnd = currentString.split("-").last().trim()
                    val nextStart = nextString.split("-").first().trim()

                    val breakMinutes = calculateBreakDuration(currentEnd, nextStart)

                    if (breakMinutes > 0) {
                        val breakBlock = LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER
                            setPadding(16, 16, 16, 16)

                            val breakParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(64, 0, 64, 16)
                            }
                            layoutParams = breakParams

                            background = GradientDrawable().apply {
                                setColor(breakBackgroundColor)
                                setStroke(2, breakStrokeColor)
                                cornerRadius = 16f
                            }

                            if (isDayPast) alpha = 0.4f
                        }

                        val breakText = if (breakMinutes >= 90) {
                            "🥪 Okienko: $currentEnd - $nextStart ($breakMinutes min)"
                        } else {
                            "☕ Przerwa: $currentEnd - $nextStart ($breakMinutes min)"
                        }

                        val breakTv = TextView(context).apply {
                            text = breakText
                            textSize = 12f
                            setTextColor(textColorSecondary)
                            setTypeface(null, Typeface.ITALIC)
                        }

                        breakBlock.addView(breakTv)
                        container.addView(breakBlock)
                    }
                }
            }
        }
    }

    private fun calculateBreakDuration(endTime: String, startTime: String): Int {
        return try {
            val (endH, endM) = endTime.split(":").map { it.toInt() }
            val (startH, startM) = startTime.split(":").map { it.toInt() }
            val endTotal = endH * 60 + endM
            val startTotal = startH * 60 + startM
            startTotal - endTotal
        } catch (e: Exception) {
            0
        }
    }

    private fun getThemeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}