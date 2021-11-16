package com.example.camerademo.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.lang.Exception

object FileUtil {

    /**
     * 根据Uri获取文件名
     */
    fun getFileNameByUri(context: Context?, uri: Uri?): String {
        val documentFile = context?.let { uri?.let { it1 -> DocumentFile.fromSingleUri(it, it1) } }
        return "${documentFile?.name}"
    }

    /**
     * 根据Uri获取文件
     */
    fun getRealFilePathByUri(context: Context?, uri: Uri?): String {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        var path = ""
        uri?.let {
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, it)) {
                if (isExternalStorageDocument(it)) {
                    val docId = DocumentsContract.getDocumentId(it)
                    val split = docId.split(":")
                    val type = split[0]
                    if ("primary" == type.lowercase()) {
                        path = "${Environment.getExternalStorageDirectory()}/${split[1]}"
                    }
                }

                // DownloadsProvider
                else if (isDownloadsDocument(it)) {
                    val id = DocumentsContract.getDocumentId(it)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), id.toLong()
                    )
                    path =
                        context?.let { it1 -> getDataColumn(it1, contentUri, null, emptyArray()) }
                            ?: ""
                }
                //MediaProvider
                else if (isMediaDocument(it)) {
                    val docId = DocumentsContract.getDocumentId(it)
                    val split = docId.split(":")
                    val type = split[0]
                    val contentUri: Uri? = when (type) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(type)
                    path = context?.let { it1 ->
                        contentUri?.let { it2 ->
                            getDataColumn(
                                it1,
                                it2, selection, selectionArgs
                            )
                        }
                    } ?: ""
                }
            }
            // MediaStore (and general)
            else if (ContentResolver.SCHEME_CONTENT == it.scheme?.lowercase()) {
                // Return the remote address
                path = if (isGooglePhotosUri(it)) {
                    it.lastPathSegment ?: ""
                } else {
                    context?.let { it1 -> getDataColumn(it1, uri, null, emptyArray()) } ?: ""
                }
            }
            // File
            else if (ContentResolver.SCHEME_FILE == it.scheme?.lowercase()) {
                path = it.path ?: ""
            }

            if (path.isEmpty()) {
                val imgName = getFileNameByUri(context, it)
                var storageDir: File? = Environment.getExternalStorageDirectory()
                var file = File(storageDir, imgName)
                if (!file.exists()){
                    storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    file = File(storageDir, imgName)
                }
                if (!file.exists()){
                    storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                    file = File(storageDir, imgName)
                }
                if (!file.exists()){
                    storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                    file = File(storageDir, imgName)
                }
                path = file.absolutePath
            }
        }
        return path
    }


    /**
     **
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.ImageColumns.DATA
        var path: String? = null
        try {
            cursor = context.contentResolver.query(
                uri, arrayOf(column), selection, selectionArgs,
                null
            )
            cursor?.moveToFirst()?.let {
                if (it) {
                    val index = cursor.getColumnIndex(column)
                    path = cursor.getString(index)
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            cursor?.close();
        }
        return path
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}