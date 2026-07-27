package com.example.zoomautojoin

import org.json.JSONObject

/**
 * One scheduled Zoom class.
 * id            - unique request code, also used as the AlarmManager request code
 * title         - e.g. "Math 101"
 * zoomLink      - the raw link the user pasted (https://zoom.us/j/xxxxx?pwd=yyyy)
 * timeInMillis  - next trigger time (epoch millis)
 * repeatWeekly  - if true, reschedules itself 7 days later after it fires
 */
data class ClassSchedule(
    val id: Int,
    val title: String,
    val zoomLink: String,
    var timeInMillis: Long,
    val repeatWeekly: Boolean
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("zoomLink", zoomLink)
        put("timeInMillis", timeInMillis)
        put("repeatWeekly", repeatWeekly)
    }

    companion object {
        fun fromJson(o: JSONObject): ClassSchedule = ClassSchedule(
            id = o.getInt("id"),
            title = o.getString("title"),
            zoomLink = o.getString("zoomLink"),
            timeInMillis = o.getLong("timeInMillis"),
            repeatWeekly = o.getBoolean("repeatWeekly")
        )
    }
}
