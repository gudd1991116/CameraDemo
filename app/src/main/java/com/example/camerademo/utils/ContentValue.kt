package com.example.camerademo.utils

import android.content.Context
import androidx.core.content.contentValuesOf
import java.io.File

object ContentValue {

    fun getImageContentValue(context: Context, contentFile : File){
        contentValuesOf(
            Pair("title",contentFile.name)
        )
    }

}