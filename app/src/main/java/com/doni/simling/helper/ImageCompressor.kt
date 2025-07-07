package com.doni.simling.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {

    fun compressImage(context: Context, uri: Uri, maxSizeKB: Int = 200): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                Log.e("ImageCompressor", "Gagal decode bitmap dari Uri")
                return null
            }

            // Dapatkan ukuran file asli (dalam bytes)
            val originalFile = File(uri.path!!)
            val originalSizeKB = originalFile.length() / 1024
            Log.d("ImageCompressor", "Ukuran asli: $originalSizeKB KB")

            val outputDir = context.cacheDir
            val outputFile = File.createTempFile("compressed_", ".jpg", outputDir)

            var quality = 90
            var outputStream: FileOutputStream

            do {
                outputStream = FileOutputStream(outputFile)
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()

                val compressedSizeKB = outputFile.length() / 1024
                Log.d("ImageCompressor", "Quality: $quality%, Ukuran: $compressedSizeKB KB")

                quality -= 10
            } while (outputFile.length() > maxSizeKB * 1024 && quality >= 10)

            val finalSizeKB = outputFile.length() / 1024
            Log.d("ImageCompressor", "Final: $finalSizeKB KB (${calculateReduction(originalSizeKB, finalSizeKB)}%)")

            outputFile
        } catch (e: Exception) {
            Log.e("ImageCompressor", "Error: ${e.message}")
            null
        }
    }

    private fun calculateReduction(original: Long, compressed: Long): String {
        return "%.2f".format(100 - (compressed.toDouble() / original.toDouble() * 100))
    }
}