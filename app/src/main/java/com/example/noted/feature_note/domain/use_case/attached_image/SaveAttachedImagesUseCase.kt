package com.example.noted.feature_note.domain.use_case.attached_image

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SaveAttachedImagesUseCase(
    private val bitmap: Bitmap,
    private val applicationContext: Context
) {

    operator fun invoke(): Single<String> {
        val cw = ContextWrapper(applicationContext)
        val directory: File = cw.getDir("notesImages", Context.MODE_PRIVATE)

        val now = System.currentTimeMillis().toString()
        val mypath = File(directory, "image${now}.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return Single.just(mypath.absolutePath)
    }
}