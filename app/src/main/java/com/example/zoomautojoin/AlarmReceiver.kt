package com.example.zoomautojoin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: "Class"
        val zoomLink = intent.getStringExtra("zoomLink") ?: return
        val repeatWeekly = intent.getBooleanExtra("repeatWeekly", false)

        // Briefly wake the CPU so we reliably get through the next few steps.
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ZoomAutoJoin:alarm")
        wakeLock.acquire(15_000L)

        val joinIntent = Intent(context, JoinActivity::class.java).apply {
            putExtra("title", title)
            putExtra("zoomLink", zoomLink)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, id, joinIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "zoom_auto_join_channel"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Class reminders", NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("$title is starting")
            .setContentText("Tap to join now")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(true)
            .build()

        nm.notify(id, notification)

        // Also try to launch directly - works if the app/screen is already active.
        try {
            context.startActivity(joinIntent)
        } catch (_: Exception) {
            // Ignored - the full-screen notification above is the reliable fallback.
        }

        if (repeatWeekly) {
            val list = ScheduleStore.getAll(context)
            val item = list.find { it.id == id }
            if (item != null) {
                item.timeInMillis += 7L * 24 * 60 * 60 * 1000
                ScheduleStore.saveAll(context, list)
                AlarmScheduler.schedule(context, item)
            }
        } else {
            ScheduleStore.remove(context, id)
        }

        wakeLock.release()
    }
}
