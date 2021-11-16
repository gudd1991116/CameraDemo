package com.example.camerademo

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.camerademo.databinding.DialogVideoBinding
import java.io.File

class VideoDialog : DialogFragment() {

    private lateinit var mBinding : DialogVideoBinding
    private var mFileUri : Uri? = null
    private var mFilePath : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.dialog_video,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.close.setOnClickListener { dismiss() }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        mFilePath?.let {
//            val strU = "/storage/emulated/0/Android/data/com.example.camerademo/files/Movies/VID_20211115_101123.3gp"
            val playUri = Uri.parse(it)
            mBinding.videoView.setVideoURI(playUri)
            mBinding.videoView.setMediaController(MediaController(requireContext()))
            mBinding.videoView.also {vv->
                vv.setOnPreparedListener {
                    vv.start()
                }
                vv.setOnCompletionListener {
                    vv.stopPlayback()
                }
                vv.setOnErrorListener { mp, what, extra ->
                    vv.stopPlayback()
                    true
                }
            }
        }
    }

    fun setVideoPath(filePath : String){
        mFilePath = filePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mBinding.videoView.isPlaying)
            mBinding.videoView.stopPlayback()
    }

}