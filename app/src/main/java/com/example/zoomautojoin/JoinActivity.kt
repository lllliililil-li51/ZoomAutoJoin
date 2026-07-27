package com.example.zoomautojoin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.zoomautojoin.databinding.ActivityJoinBinding

class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: "Class"
        val zoomLink = intent.getStringExtra("zoomLink") ?: ""

        binding.className.text = title
        binding.status.text = "Joining automatically in 3..."

        binding.joinNowButton.setOnClickListener { launchZoom(zoomLink) }
        binding.cancelButton.setOnClickListener { finish() }

        // Countdown gives the user a moment to cancel, then auto-joins.
        object : CountDownTimer(3000, 1000) {
            override fun onTick(msUntilFinished: Long) {
                val secondsLeft = (msUntilFinished / 1000) + 1
                binding.status.text = "Joining automatically in $secondsLeft..."
            }

            override fun onFinish() {
                launchZoom(zoomLink)
            }
        }.start()
    }

    private fun launchZoom(zoomLink: String) {
        val deepLink = ZoomLinkUtils.toAutoJoinDeepLink(zoomLink)
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)))
        } catch (e: Exception) {
            // Zoom app not installed / link couldn't be handled - fall back to the browser link
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(zoomLink)))
            } catch (_: Exception) { }
        }
        finish()
    }
}
