package com.example.zoomautojoin

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zoomautojoin.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ScheduleAdapter
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestRuntimePermissionsIfNeeded()

        adapter = ScheduleAdapter(ScheduleStore.getAll(this).toMutableList()) { item ->
            AlarmScheduler.cancel(this, item.id)
            ScheduleStore.remove(this, item.id)
            refreshList()
            Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
        }
        binding.scheduleList.layoutManager = LinearLayoutManager(this)
        binding.scheduleList.adapter = adapter

        binding.pickDateButton.setOnClickListener { pickDate() }
        binding.pickTimeButton.setOnClickListener { pickTime() }

        binding.saveButton.setOnClickListener { saveClass() }

        binding.batteryButton.setOnClickListener { requestBatteryExemption() }
        binding.exactAlarmButton.setOnClickListener { requestExactAlarmPermission() }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        adapter.updateData(ScheduleStore.getAll(this).sortedBy { it.timeInMillis })
    }

    private fun pickDate() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                binding.pickDateButton.text = "Date: ${month + 1}/$day/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun pickTime() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                binding.pickTimeButton.text = String.format("Time: %02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun saveClass() {
        val title = binding.classTitleInput.text.toString().trim().ifEmpty { "Class" }
        val link = binding.zoomLinkInput.text.toString().trim()

        if (link.isEmpty()) {
            Toast.makeText(this, "Paste the Zoom link first", Toast.LENGTH_SHORT).show()
            return
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Pick a date/time in the future", Toast.LENGTH_SHORT).show()
            return
        }

        val repeatWeekly = binding.repeatWeeklyCheckbox.isChecked
        val id = ScheduleStore.nextId(this)
        val item = ClassSchedule(id, title, link, calendar.timeInMillis, repeatWeekly)

        ScheduleStore.add(this, item)
        AlarmScheduler.schedule(this, item)

        binding.classTitleInput.text?.clear()
        binding.zoomLinkInput.text?.clear()
        binding.repeatWeeklyCheckbox.isChecked = false
        refreshList()
        Toast.makeText(this, "Scheduled! It'll auto-join at that time.", Toast.LENGTH_LONG).show()
    }

    private fun requestRuntimePermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001
            )
        }
    }

    private fun requestBatteryExemption() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(this, "Open Settings > Battery > Unrestricted for this app manually", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!am.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Already granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Not needed on this Android version", Toast.LENGTH_SHORT).show()
        }
    }
}
