package com.example.zoomautojoin

import android.content.Context
import org.json.JSONArray

/**
 * Very small persistence layer. Stores every scheduled class as a JSON array
 * in SharedPreferences so it survives app restarts (actual alarm re-arming
 * after a phone reboot is handled separately by BootReceiver).
 */
object ScheduleStore {

    private const val PREFS = "zoom_auto_join_prefs"
    private const val KEY_CLASSES = "classes"

    fun getAll(context: Context): MutableList<ClassSchedule> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_CLASSES, "[]") ?: "[]"
        val arr = JSONArray(raw)
        val list = mutableListOf<ClassSchedule>()
        for (i in 0 until arr.length()) {
            list.add(ClassSchedule.fromJson(arr.getJSONObject(i)))
        }
        return list
    }

    fun saveAll(context: Context, list: List<ClassSchedule>) {
        val arr = JSONArray()
        list.forEach { arr.put(it.toJson()) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CLASSES, arr.toString())
            .apply()
    }

    fun add(context: Context, item: ClassSchedule) {
        val list = getAll(context)
        list.add(item)
        saveAll(context, list)
    }

    fun remove(context: Context, id: Int) {
        val list = getAll(context).filterNot { it.id == id }
        saveAll(context, list)
    }

    fun nextId(context: Context): Int {
        val list = getAll(context)
        return (list.maxOfOrNull { it.id } ?: 0) + 1
    }
}
