package com.example.camerademo.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import java.io.File

object UriConstants {

    /**
     * 获取拍照保存的图片目录
     */
    fun getPicturesUri(context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValue = contentValuesOf(
                Pair(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    "IMG_${TimeUtil.getCurrentTime_yyyyMMdd_HHmmss()}.jpg"
                ),
                Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            )
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )!!
        } else {
            val file = File(
                Environment.getExternalStorageDirectory(),
                "/IMG_${TimeUtil.getCurrentTime_yyyyMMdd_HHmmss()}.jpg"
            )
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
                val f = File(it, "/IMG_${TimeUtil.getCurrentTime_yyyyMMdd_HHmmss()}.jpg")
                Log.i("MainActivity", "FilePath: ${f.absolutePath}")
                FileProvider.getUriForFile(context, authorities, f)
            } ?: FileProvider.getUriForFile(context, authorities, file)
        }
    }

    /**
     * 获取拍视频后的视频Uri
     */
    fun getTakeVideoUri(context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValue = contentValuesOf(
                Pair(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    "VID_${TimeUtil.getCurrentTime_yyyyMMdd_HHmmss()}.mp4"
                ),
                Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            )
            context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValue
            )!!
        } else {
            val file = File(
                Environment.getExternalStorageState(),
                "/Movies/VID_${TimeUtil.getCurrentTime_yyyyMMdd_HHmmss()}.mp4"
            )
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.let {
                val f = File(it, "/VID_${TimeUtil.getCurrentTime_yyyyMMdd_HHmmss()}.mp4")
                FileProvider.getUriForFile(context, authorities, f)
            } ?: FileProvider.getUriForFile(context, authorities, file)
        }
    }

}