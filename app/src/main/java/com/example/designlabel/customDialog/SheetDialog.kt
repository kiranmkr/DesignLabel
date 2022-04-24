package com.example.designlabel.customDialog


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import com.example.designlabel.R
import com.example.designlabel.ui.EditingScreen
import com.example.designlabel.utils.Utils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt


@Suppress("UNNECESSARY_SAFE_CALL")
class SheetDialog(var activity: Activity) {

    lateinit var customDialog: Dialog

    var gridLayout: GridLayout? = null
    var width: Int = 0
    var height: Int = 0
    var labelWidth = 0.0
    var labelHight = 0.0
    var bitmap: Bitmap? = null

    /*var sw = 7.9f
    var sh = 11.0f*/
    var sw: Double = 8.27
    var sh: Double = 11.69
    var newW = 0
    var newH = 0
    var totalLabels = 0
    var labelSize: String? = "3x3"
    lateinit var btnback: ImageView
    lateinit var save_gallery_iv: ImageView
    lateinit var save_pdf_iv: ImageView
    lateinit var save_share_iv: ImageView
    lateinit var home_iv: ImageView

    val workerThread: ExecutorService = Executors.newCachedThreadPool()
    val workerHandler = Handler(Looper.getMainLooper())

    @SuppressLint("InflateParams")
    fun setDialog() {

        //*****************************Create a custom Dialog***************************************//
        customDialog = Dialog(activity, R.style.full_screen_dialog2)
        val view = View.inflate(activity, R.layout.sheet_dialog, null)
        Objects.requireNonNull(customDialog.window)
            ?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        customDialog.window!!.statusBarColor =
            ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
        customDialog.setContentView(view)

        gridLayout = customDialog.findViewById(R.id.gridViewItem)
        btnback = customDialog.findViewById(R.id.back_btn)
        save_gallery_iv = customDialog.findViewById(R.id.gallery_iv)
        save_pdf_iv = customDialog.findViewById(R.id.imageView11)
        save_share_iv = customDialog.findViewById(R.id.imageView43)
        home_iv = customDialog.findViewById(R.id.home_btn)

        btnback.setOnClickListener {
            customDialog.dismiss()
        }

        save_gallery_iv.setOnClickListener {

            gridLayout?.let {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    Dexter.withContext(activity)
                        .withPermissions(
                            Manifest.permission.ACCESS_MEDIA_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    workerThread.execute {

                                        val saveFilePath: String? =
                                            (activity as EditingScreen).saveMediaToStorage(
                                                it.drawToBitmap(Bitmap.Config.ARGB_8888)
                                            )

                                        if (saveFilePath != null && File(saveFilePath).exists()) {

                                            workerHandler.post {
                                                Utils.showToast(activity, "$saveFilePath")
                                                Log.e("myFileFos", "Saved file and not null")
                                            }

                                        } else {
                                            workerHandler.post {
                                                Utils.showToast(
                                                    activity,
                                                    "${activity.resources.getString(R.string.something_went_wrong)}"
                                                )
                                            }
                                        }

                                    }
                                }
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                list: List<PermissionRequest>,
                                permissionToken: PermissionToken
                            ) {
                                permissionToken.continuePermissionRequest()
                            }
                        }).check()

                } else {

                    Dexter.withContext(activity)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                                workerThread.execute {

                                    val saveFilePath: String? =
                                        (activity as EditingScreen).saveMediaToStorage(
                                            it.drawToBitmap(Bitmap.Config.ARGB_8888)
                                        )

                                    if (saveFilePath != null && File(saveFilePath).exists()) {

                                        workerHandler.post {
                                            Utils.showToast(activity, "$saveFilePath")
                                            Log.e("myFileFos", "Saved file and not null")
                                        }

                                    } else {
                                        workerHandler.post {
                                            Utils.showToast(
                                                activity,
                                                "${activity.resources.getString(R.string.something_went_wrong)}"
                                            )
                                        }
                                    }

                                }

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                Utils.showToast(activity, "Perssmion is Denied")
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permission: PermissionRequest?,
                                token: PermissionToken?
                            ) {
                                token?.continuePermissionRequest()
                            }
                        }).check()

                }

            }
        }

        save_pdf_iv.setOnClickListener {

            gridLayout?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    workerThread.execute {

                        val filePath =
                            (activity as EditingScreen).savePdfFileToScopeStorage(it)

                        if (filePath != null && File(filePath).exists()) {

                            workerHandler.post {
                                Utils.showToast(activity, "${filePath}")
                                Log.e("myFileFos", "Saved file and not null")
                            }

                        } else {
                            workerHandler.post {
                                Utils.showToast(activity, "${R.string.something_went_wrong}")
                            }
                        }
                    }

                } else {

                    Dexter.withContext(activity)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                                workerThread.execute {

                                    val filePath =
                                        (activity as EditingScreen).savePdfFileToScopeStorage(it)

                                    if (filePath != null && File(filePath).exists()) {

                                        workerHandler.post {
                                            Utils.showToast(activity, "${filePath}")
                                            Log.e("myFileFos", "Saved file and not null")
                                        }

                                    } else {
                                        workerHandler.post {
                                            Utils.showToast(
                                                activity,
                                                "${R.string.something_went_wrong}"
                                            )
                                        }
                                    }
                                }

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                Utils.showToast(activity, "Perssmion is Denied")
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permission: PermissionRequest?,
                                token: PermissionToken?
                            ) {
                                token?.continuePermissionRequest()
                            }
                        }).check()

                }

            }
        }

        save_share_iv.setOnClickListener {
            gridLayout?.let {
                Utils.sharePdfFile(it, activity)
            }
        }

        home_iv.setOnClickListener {
            customDialog.dismiss()
            ((activity as EditingScreen)).finishEditing()
        }

    }

    fun showDialog(label_size: String, labelBitmap: Bitmap) {

        customDialog.show()

        val vto = gridLayout!!.viewTreeObserver

        if (vto.isAlive) {

            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    gridLayout!!.viewTreeObserver
                        .removeOnGlobalLayoutListener(this)

                    gridLayout!!.removeAllViews()

                    width = gridLayout!!.measuredWidth
                    height = gridLayout!!.measuredHeight

                    labelSize = label_size

                    Log.d("myTag", "${width} -- ?${height}")

                    val firstIndex = labelSize!!.indexOf("x")
                    labelWidth = labelSize!!.substring(0, firstIndex).toDouble()
                    labelHight = labelSize!!.substring(firstIndex + 1).toDouble()

                    bitmap = labelBitmap
                    calculateSheetSize(labelWidth, labelHight)
                    callsetView(width, height)
                }
            })

        }


    }

    private fun callsetView(viewWidth: Int, viewHeight: Int) {

        ////user enterd label Quantity
        val total = totalLabels
        val column = newW.toFloat().roundToInt()
        val row = total / column

        Log.e("myTag", "${column} $row")

        Log.e("myTag", "after ${column} $row")

        gridLayout!!.columnCount = column
        gridLayout!!.rowCount = row + 1


        var i = 0
        var c = 0
        var r = 0

        while (i < total) {

            if (c == column) {
                c = 0
                r++
            }

            //Log.e("LayoutName", String.valueOf(bitmap));
            val imageView = ImageView(activity)
            imageView.setImageBitmap(bitmap)
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageView.setPadding(8, 8, 8, 8)
            val param = GridLayout.LayoutParams()
            param.width = (viewWidth / column.toFloat()).roundToInt()
            param.height = (viewHeight / newH.toFloat().roundToInt().toFloat()).roundToInt()
            //param.setMargins(2,0,2,0)
            param.setGravity(Gravity.CENTER)
            param.columnSpec = GridLayout.spec(c)
            param.rowSpec = GridLayout.spec(r)
            imageView.layoutParams = param
            gridLayout!!.addView(imageView)
            i++
            c++
        }
    }

    private fun calculateSheetSize(w: Double, h: Double) {
        newW = (sw / w).toInt()
        newH = (sh / h).toInt()
        totalLabels = newH.toFloat().roundToInt() * newW.toFloat().roundToInt()
        //Log.e("Calling", "" + newH + "/" + newW + "/" + totalLabels);
    }

}