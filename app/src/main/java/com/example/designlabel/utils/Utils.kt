package com.example.designlabel.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.HashMap

object Utils {

    fun showToast(c: Context, message: String) {
        try {
            if (!(c as Activity).isFinishing) {
                c.runOnUiThread { //show your Toast here..
                    Toast.makeText(c.applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scaleDown(
        realImage: Bitmap?, maxImageSize: Float,
        filter: Boolean
    ): Bitmap? {

        if (realImage != null) {

            Log.e("myBitmapVal", "${realImage.width} -- ${realImage.height}")

            return if (realImage.width > maxImageSize || realImage.height > maxImageSize) {

                val ratio =
                    Math.min(maxImageSize / realImage.width, maxImageSize / realImage.height)
                val width = Math.round(ratio * realImage.width)
                val height = Math.round(ratio * realImage.height)

                return Bitmap.createScaledBitmap(
                    realImage, width,
                    height, filter
                )
            } else {
                realImage
            }
        } else {
            return null
        }

    }

    fun getBitmapOrg(path: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return bitmap
    }

    fun getBitmapFromAsset(context: Context, path: String): Bitmap? = try {
        context.assets.open(path).use { BitmapFactory.decodeStream(it) }
    } catch (ex: IOException) {
        ex.printStackTrace()
        null
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun clearGarbageCollection() {
        try {
            System.gc()
            Runtime.getRuntime().gc()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    @JvmStatic
    var listOfCategory = arrayOf(
        "candy",
        "car",
        "cosmetic",
        "drinks",
        "hazard",
        "kids",
        "laptop",
        "ramzan",
        "shipping",
    )

    @JvmStatic
    val categoryMap: HashMap<String, Int> =
        hashMapOf(
            "candy" to 10,
            "car" to 10,
            "cosmetic" to 10,
            "drinks" to 10,
            "hazard" to 10,
            "kids" to 10,
            "laptop" to 10,
            "ramzan" to 10,
            "shipping" to 10,
            "shape" to 10
        )
}