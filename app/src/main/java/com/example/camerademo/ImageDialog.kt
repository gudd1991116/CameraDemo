package com.example.camerademo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.camerademo.databinding.DialogImageBinding

class ImageDialog : DialogFragment(){

    private lateinit var mBinding : DialogImageBinding
    private var mBitmap : Bitmap? = null
    private var mUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_image,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Glide.with(requireContext()).load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201609%2F26%2F20160926081306_GM2tv.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1639121377&t=1830c90629a1c7e6fb50f66b9c01b861").centerCrop().into(mBinding.image)
        context?.applicationContext?.let {
            mBitmap?.let { bit->
                Log.i("image", "bitmap is not null!")
                Glide.with(it).load(bit).into(mBinding.image)
            }?: mUri?.let {uri->
                Log.i("image", "uri is not null!")
                Glide.with(it).load(uri).into(mBinding.image)
            }
        }
        mBinding.close.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let{
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    fun setImage(uri : Uri){
        mUri = uri
    }

    fun setImage(bitmap: Bitmap?){
        mBitmap = bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBitmap?.recycle()
    }

}