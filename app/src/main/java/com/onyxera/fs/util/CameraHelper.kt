package com.onyxera.fs.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

/**
 * Solnod Medya Yönetimi Modülü.
 * Sistem güvenli, yerel (offline) dosya ve galeri kaydı sağlar.
 */
class CameraHelper(private val context: Context) {

    // Native Intent için güvenli FileProvider URI'si üretir.
    fun createTempPhotoUri(): Uri? {
        return try {
            val secureFolder = File(context.filesDir, "fs_secure_images")
            if (!secureFolder.exists()) {
                secureFolder.mkdirs()
            }
            // Kayıtların benzersiz olması için timestamp kullanıldı.
            val photoFile = File(secureFolder, "FS_SOLNOD_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Fotoğrafı doğrudan telefonun genel Galerisine kaydeder
    fun savePhotoToPublicGallery(sourceUri: Uri) {
        try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "FS_SOLNOD_${System.currentTimeMillis()}.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/FS_Materials")
                }
            }

            val publicUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (publicUri != null) {
                resolver.openOutputStream(publicUri)?.use { outputStream ->
                    resolver.openInputStream(sourceUri)?.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}