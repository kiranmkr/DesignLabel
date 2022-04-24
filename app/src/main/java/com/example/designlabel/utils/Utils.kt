package com.example.designlabel.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.designlabel.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    @Suppress("DEPRECATION")
    @JvmField
    val BASE_LOCAL_PATH =
        "${Environment.getExternalStorageDirectory().absolutePath}/Download/DesignLabel/"

    val Base_External_Save = "${Environment.DIRECTORY_DOWNLOADS}/DesignLabel"

    @JvmStatic
    fun getRootPath(context: Context, internalDrir: Boolean): String {

        val root = if (internalDrir) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.getExternalFilesDir("DesignLabel")?.absolutePath + "/"
            } else {
                BASE_LOCAL_PATH
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Base_External_Save
            } else {
                BASE_LOCAL_PATH
            }
        }

        val dirDest = File(root)

        if (!dirDest.exists()) {
            dirDest.mkdirs()
        }

        return root
    }

    fun getShareDirectory(context: Context): String {
        return context.externalCacheDir?.absolutePath + "/"
    }

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

    //**************************This method hide keyboard*******************************************//
    fun hideKeyboardFromView(view: View) {
        val imm =
            (App.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //*********************************Method for shear pdf***************************************//
    fun sharePdfFile(
        view: View?,
        context: Context
    ) {

        view?.let {

            //File Name as a current time Name
            val mFileName = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            val file = context.externalCacheDir?.absolutePath
            var fileUri: Uri? = null

            if (file != null) {

                val path = File(file)

                if (!path.exists()) {
                    path.mkdirs()
                }

                val newFile = File(path, "PDF\n$mFileName.pdf")
                val document = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(it.width, it.height, 1).create()
                val page = document.startPage(pageInfo)
                it.draw(page.canvas)

                // finish the page
                document.finishPage(page)
                try {

                    document.writeTo(FileOutputStream(newFile))

                    if (newFile.exists()) {

                        fileUri = if (Build.VERSION.SDK_INT >= 24) {
                            FileProvider.getUriForFile(
                                context,
                                Constant.fileProvider,
                                newFile
                            )
                        } else {
                            Uri.fromFile(newFile)
                        }
                        val intentShareFile = Intent(Intent.ACTION_SEND)
                        intentShareFile.type = "application/pdf"
                        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
                        context.startActivity(
                            Intent.createChooser(
                                intentShareFile,
                                context.resources.getString(R.string.share_file)
                            )
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                document.close()
            } else {
                Utils.showToast(context, "${R.string.something_went_wrong}")
            }

        }
    }

    fun fileChecker(path: String?): Boolean {
        return if (path != null) {
            val file = File(path)
            file.exists()
        } else {
            false
        }
    }
}