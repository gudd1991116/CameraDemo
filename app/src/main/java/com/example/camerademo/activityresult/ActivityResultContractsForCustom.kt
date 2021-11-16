package com.example.camerademo.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

object ActivityResultContractsForCustom {

    class TakeVideo : ActivityResultContract<Uri, Boolean?>() {
        @CallSuper
        override fun createIntent(context: Context, input: Uri): Intent {
            return Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, input)
        }

        override fun getSynchronousResult(
            context: Context,
            input: Uri
        ): SynchronousResult<Boolean?>? {
            return null
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean? {
            return resultCode == Activity.RESULT_OK
        }
    }
}