package com.example.camerademo

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.camerademo.activityresult.ActivityResultContractsForCustom
import com.example.camerademo.databinding.ActivityMainBinding
import com.example.camerademo.utils.FileUtil
import com.example.camerademo.utils.MediaUtil
import com.example.camerademo.utils.TimeUtil
import com.example.camerademo.utils.UriConstants
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions3.RxPermissions
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    var getImagePreviewResultBack: ActivityResultLauncher<Void>? = null
    var getImageResultBack: ActivityResultLauncher<Uri>? = null
    var getVideoResultBack : ActivityResultLauncher<Uri>? = null

    private lateinit var mBinding: ActivityMainBinding
    private val menu = mutableListOf(
        "系统相机-预览图", "系统相机-保存图片到本地", /*"往相册插入图片",*/"录制视频"
    )
    private lateinit var mAdapter: MainAdapter
    private var mPicUri : Uri? = null
    private var mVidUri : Uri? = null
    private lateinit var rxPermission: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        rxPermission = RxPermissions(this)

        mBinding.recyclerview.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerview.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(this)
                .colorResId(R.color.purple_200)
                .size(resources.getDimensionPixelSize(R.dimen.divider)).build()
        )
        mAdapter = MainAdapter(menu)
        mBinding.recyclerview.adapter = mAdapter
        mAdapter.setOnItemClickListener(object : MainAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (menu[position]) {
                    "系统相机-预览图" -> {
//                        Toast.makeText(this@MainActivity, "系统相机", Toast.LENGTH_LONG).show()
                        dispatchTakePictureIntentForPreview()
                    }
                    "系统相机-保存图片到本地" -> {
                        dispatchTakePictureIntent()
                    }
                    "录制视频"->{
                        dispatchTakeVideoIntent()
                    }
                    "往相册插入图片" -> {
                        val path =
                            "/storage/emulated/0/Android/data/com.example.camerademo/files/Pictures/IMG_20211111_163525.jpg"
                        MediaStore.Images.Media.insertImage(
                            contentResolver, path,
                            "title", "description"
                        )
                        sendBroadcast(
                            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE),
                            "file://${Uri.parse(path)}"
                        )
                    }
                }
            }

        })
        initData()
        register()
    }

    private fun initData() {
//        Log.i("MainActivity","str: ${FileUtil.getRealFilePathByUri(this,Uri.parse("content://com.example.camerademo.fileprovider/Pictures/Android/data/com.example.camerademo/files/Pictures/IMG_20211111_135504.jpg"))}")
        mPicUri = UriConstants.getPicturesUri(this)
        mVidUri = UriConstants.getTakeVideoUri(this)
        Log.i("MainActivity", "Uri: $mVidUri")
    }

    private fun register() {
        // 注册获取相机拍的照片缩略图
        getImagePreviewResultBack =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                val dialog = ImageDialog()
                dialog.setImage(it)
                dialog.show(supportFragmentManager, "0")
            }
        getImageResultBack =
            registerForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it) {
                    mPicUri?.let { uri ->
                        val dialog = ImageDialog()
                        dialog.setImage(uri)
                        dialog.show(supportFragmentManager, "1")
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            val absolutePath = FileUtil.getRealFilePathByUri(this, uri)
                            MediaUtil.mediaInsertImage(this@MainActivity,absolutePath)
                            /*MediaStore.Images.Media.insertImage(
                                contentResolver,
                                absolutePath,
                                "IMG_${TimeUtil.getCurrentTime_yyyyMMdd_HHmmss()}",
                                "description"
                            )
                            sendBroadcast(
                                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE),
                                "file://${Uri.parse(absolutePath)}"
                            )*/
                        }
                    }
                }
            }
        getVideoResultBack = registerForActivityResult(ActivityResultContractsForCustom.TakeVideo()){
            if (it == true) {
                mVidUri?.let { uri ->
                val dialog = VideoDialog()
                dialog.setVideoPath(FileUtil.getRealFilePathByUri(this, uri))
                dialog.show(supportFragmentManager, "video")
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        val absolutePath = FileUtil.getRealFilePathByUri(this, uri)
                        MediaUtil.mediaInsertVideo(this@MainActivity, absolutePath)
                    }
                }
            }
        }
            /*registerForActivityResult(ActivityResultContractsForCustom.TakeVideo()){
                if (it==true){

                }
            }*/
    }

    /**
     * 打开系统相机获取预览图
     */
    private fun dispatchTakePictureIntentForPreview() {
        // 通过Intent意图打开系统相机拍照获取预览图
        /*Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE)
            }
        }*/
        getImagePathUri()
        getImagePreviewResultBack?.launch(null)
    }

    /**
     * 打开系统相机拍照并保存
     */
    private fun dispatchTakePictureIntent() {
        rxPermission.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe(object : Observer<Boolean> {
                override fun onNext(t: Boolean) {
                    if (t) {// granted
                        getImagePathUri()
                        getImageResultBack?.launch(mPicUri)
                    } else { // Denied permissions
                        Snackbar.make(mBinding.root, "权限被拒绝，是否去设置？", Snackbar.LENGTH_LONG)
                            .setAction("设置") {
                                Intent().also {
                                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    it.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                                    it.data = Uri.fromParts("package",packageName,null)
                                    startActivity(it)
                                }

                            }.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.teal_200
                                )
                            ).setActionTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.teal_200
                                )
                            ).show()
                    }
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {}

                override fun onComplete() {}

            })

    }

    private fun dispatchTakeVideoIntent(){
        getVideoPathUri()
        getVideoResultBack?.launch(mVidUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            data?.extras?.get("data")?.let {
                val dialog = ImageDialog()
                dialog.setImage(it as Bitmap)
                dialog.show(supportFragmentManager, "0")
            }

        }
    }

    private fun getImagePathUri(){
        mPicUri = UriConstants.getPicturesUri(this)
    }
    private fun getVideoPathUri(){
        mVidUri = UriConstants.getTakeVideoUri(this)
    }
}