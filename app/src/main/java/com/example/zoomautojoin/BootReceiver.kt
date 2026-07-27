package com.example.zoomautojoin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val now = System.currentTimeMillis()
            ScheduleStore.getAll(context)
                .filter { it.timeInMillis > now }
                .forEach { AlarmScheduler.schedule(context, it) }
        }
    }
}
