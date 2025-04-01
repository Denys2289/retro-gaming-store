package com.example.retrogamingstore.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    /**
     * Зберігає зображення у внутрішнє сховище додатку
     */
    fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            // Отримуємо Bitmap із Uri
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

            // Створюємо директорію для зображень, якщо вона не існує
            val imagesDir = File(context.filesDir, "product_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            // Створюємо унікальне ім'я файлу
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "PRODUCT_${timeStamp}.jpg"
            val imageFile = File(imagesDir, imageFileName)

            // Зберігаємо Bitmap у файл із стисненням
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }

            // Повертаємо шлях до збереженого файлу
            imageFile.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Видаляє зображення з внутрішнього сховища
     */
    fun deleteImageFromInternalStorage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadImageFromInternalStorage(context: Context, imagePath: String): Bitmap? {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}