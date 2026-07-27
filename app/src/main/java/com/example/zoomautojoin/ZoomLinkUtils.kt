package com.example.zoomautojoin

import android.net.Uri

object ZoomLinkUtils {

    /**
     * Accepts a normal link like:
     *   https://zoom.us/j/1234567890?pwd=abcDEF123
     * or
     *   https://us02web.zoom.us/j/1234567890?pwd=abcDEF123
     *
     * and converts it into Zoom's own deep-link scheme, which tells the
     * Zoom app to join immediately without showing the "Join Meeting" button:
     *   zoommtg://zoom.us/join?action=join&confno=1234567890&pwd=abcDEF123
     *
     * If the link doesn't match the expected pattern (e.g. it's already a
     * zoommtg:// link, or a personal room URL Zoom formats differently),
     * the original link is returned unchanged and Android will just hand it
     * to whichever app/browser can open it - Zoom will still open, it may
     * just need one tap on "Join".
     */
    fun toAutoJoinDeepLink(rawLink: String): String {
        val trimmed = rawLink.trim()
        if (trimmed.startsWith("zoommtg://")) return trimmed

        return try {
            val uri = Uri.parse(trimmed)
            // Path looks like /j/1234567890
            val segments = uri.pathSegments
            val meetingId = segments.lastOrNull { it.all(Char::isDigit) }
            val pwd = uri.getQueryParameter("pwd")

            if (meetingId != null) {
                val builder = StringBuilder("zoommtg://zoom.us/join?action=join&confno=$meetingId")
                if (!pwd.isNullOrEmpty()) builder.append("&pwd=$pwd")
                builder.toString()
            } else {
                trimmed
            }
        } catch (e: Exception) {
            trimmed
        }
    }
}
