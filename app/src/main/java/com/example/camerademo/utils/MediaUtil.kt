package com.example.camerademo.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import java.io.File

object MediaUtil {

    fun mediaInsertImage(con: Context, filePath: String) {
        con.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            getImageContentValue(convertPathToFile(filePath))
        )

        Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE").also {
            it.data = Uri.fromFile(convertPathToFile(filePath))
            con.sendBroadcast(it)
        }
    }

    fun mediaInsertVideo(con: Context, filePath: String) {
        con.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, getVideoContentValue(
                convertPathToFile(filePath)
            )
        )
    }

    private fun convertPathToFile(path: String?): File? {
        return path?.let {
            File(path)
        }
    }

    private fun getImageContentValue(file: File?): ContentValues? {
        return file?.let {
            val con = contentValuesOf(Pair("mime_type", "image/jpeg"))
            con.putAll(getMediaContentValueBase(it))
            con
        }
    }

    private fun getVideoContentValue(file: File?): ContentValues? {
        return file?.let {
            val con = contentValuesOf(Pair("mime_type", "video/mp4"))
            con.put(MediaStore.Video.Media.DURATION, getVideoDuration(file))
            con.putAll(getMediaContentValueBase(it))
            con
        }
    }

    private fun getMediaContentValueBase(file: File): ContentValues {
        val ct = System.currentTimeMillis()
        return contentValuesOf(
            Pair(MediaStore.Video.Media.TITLE, file.name),
            Pair(MediaStore.Video.Media.DISPLAY_NAME, file.name),
            Pair(MediaStore.Video.Media.DATE_TAKEN, ct),
            Pair(MediaStore.Video.Media.DATE_MODIFIED, ct),
            Pair(MediaStore.Video.Media.DATE_ADDED, ct),
            Pair(MediaStore.Video.Media.DATA, file.absolutePath),
            Pair(MediaStore.Video.Media.SIZE, file.length())
        )
    }

    private fun getVideoDuration(file: File?) : Long{
        return file?.let { _file->
            if (!_file.exists()) {
                0
            } else{
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(_file.absolutePath)
                val dur = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                dur?.toLong()
            }
        }?:0
    }
}