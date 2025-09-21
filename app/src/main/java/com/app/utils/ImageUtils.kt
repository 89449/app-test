package com.app.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

object ImageUtils {

    /**
     * Converts a size in bytes to a human-readable string (e.g., 1.2 MB).
     */
    fun formatSize(sizeInBytes: Long): String {
        if (sizeInBytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(sizeInBytes.toDouble()) / log10(1024.0)).toInt()
        return String.format(Locale.getDefault(), "%.1f %s", sizeInBytes / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
    }

    /**
     * Formats a Unix timestamp to a readable date string.
     */
    fun formatDate(timestampInSeconds: Long): String {
        val date = Date(timestampInSeconds * 1000) // Convert seconds to milliseconds
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    /**
     * Formats a timestamp to show time elapsed since it was added.
     */
    fun formatTimeAgo(timestampInSeconds: Long): String {
        val now = System.currentTimeMillis()
        val timeInSeconds = now / 1000 - timestampInSeconds
        return when {
            timeInSeconds < 60 -> "${timeInSeconds}s ago"
            timeInSeconds < 3600 -> "${timeInSeconds / 60}m ago"
            timeInSeconds < 86400 -> "${timeInSeconds / 3600}h ago"
            else -> "${timeInSeconds / 86400}d ago"
        }
    }
}