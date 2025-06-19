package com.app.aprendequechua.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UserProgressHelper {

    fun debeResetearProgreso(lastAccessDate: String?, today: String): Boolean {
        return lastAccessDate != today
    }

    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}