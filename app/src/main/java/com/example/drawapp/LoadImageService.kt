package com.example.drawapp

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class LoadImageService: Service() {

    private val binder = LocalBinder()

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LoadImageService = this@LoadImageService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        GlobalScope.launch {
            val bitmap = intent.getParcelableExtra<Bitmap>("KEY")!!
            saveImage(bitmap)
        }
        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    fun saveImage(bitmap: Bitmap, folderName: String = "MyDrawApp") {
        GlobalScope.launch {
            if (Build.VERSION.SDK_INT >= 29) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
                values.put(MediaStore.Images.Media.IS_PENDING, true)
                // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    saveImageToStream(bitmap, contentResolver.openOutputStream(uri))
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    contentResolver.update(uri, values, null, null)
                }
            } else {
                val directory = File(Environment.getExternalStorageDirectory().toString() + "/" + folderName)
                // getExternalStorageDirectory is deprecated in API 29
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val fileName = System.currentTimeMillis().toString() + ".png"
                val file = File(directory, fileName)
                saveImageToStream(bitmap, FileOutputStream(file))
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }

    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}