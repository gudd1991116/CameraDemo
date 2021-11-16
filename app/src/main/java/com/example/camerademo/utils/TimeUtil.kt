package com.example.camerademo.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TimeUtil {

    /**
     * 时间戳转换成yyyyMMdd_HHmmss样式
     */
    fun getCurrentTime_yyyyMMdd_HHmmss() : String{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        }else{
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
            val date = Date()
            timestamp.format(date)
        }
    }

}