package com.example.designlabel.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.designlabel.R
import com.example.designlabel.adapter.*
import com.example.designlabel.customCallBack.AddNewTextCallBack
import com.example.designlabel.customCallBack.FontAdapterCallBack
import com.example.designlabel.customCallBack.StickerClick
import com.example.designlabel.customDialog.AddNewText
import com.example.designlabel.customDialog.SheetDialog
import com.example.designlabel.customDialog.SheetPrintDialog
import com.example.designlabel.customDialog.UpdateNewText
import com.example.designlabel.customSticker.CustomImageView
import com.example.designlabel.datamodel.ImageView
import com.example.designlabel.datamodel.Root
import com.example.designlabel.datamodel.TextView
import com.example.designlabel.other.MoveViewTouchListener
import com.example.designlabel.permissionWorking.OnActivityResultListener
import com.example.designlabel.permissionWorking.OnPermissionDeniedListener
import com.example.designlabel.permissionWorking.OnPermissionGrantedListener
import com.example.designlabel.permissionWorking.OnPermissionPermanentlyDeniedListener
import com.example.designlabel.utils.Constant
import com.example.designlabel.utils.Utils
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import org.json.JSONObject
import java.io.*
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class EditingScreen : AppCompatActivity(), SVGLayersAdapter.SvgLayersClick,
    MoveViewTouchListener.EditTextCallBacks, FontAdapterCallBack, AddNewTextCallBack, StickerClick,
    CustomImageView.CustomImageCallBack, SVGColorAdapter.SvgColorClick,
    SheetPrintDialog.SheetPrintDialogCallBack, OnActivityResultListener,
    OnPermissionDeniedListener, OnPermissionPermanentlyDeniedListener, OnPermissionGrantedListener {

    private val workerThread: ExecutorService = Executors.newCachedThreadPool()
    private val workerHandler = Handler(Looper.getMainLooper())

    private var imageViewArray: ArrayList<ImageView> = ArrayList()
    private var textViewJson: ArrayList<TextView> = ArrayList()
    private var svgLayerPathList: ArrayList<String> = ArrayList()
    private var rootLayout: RelativeLayout? = null
    private var screenRatioFactor: Double = 1.0
    private var screenWidth: Double = 720.0

    private var imageViewSVG: android.widget.ImageView? = null
    private var currentLayer: android.widget.ImageView? = null
    private var svgSeekAlpha: SeekBar? = null

    private var svgRecyclerView: RecyclerView? = null
    private var mLastClickTime: Long = 0

    private var svgColorPickerImage: AppCompatImageView? = null
    private var textColorPickerImage: AppCompatImageView? = null
    private var svgColorPrimaryImage: AppCompatImageView? = null
    private var svgColorBlackImage: AppCompatImageView? = null
    private var svgLayersAdapter: SVGLayersAdapter? = null
    private var svgColorAdapter: SVGColorAdapter? = null
    private var layerSelection: ConstraintLayout? = null
    private var svgColor: RecyclerView? = null


    private var mColor = 0

    private var textRoot: View? = null
    private var layerRoot: View? = null

    var currentText: android.widget.TextView? = null
    private var tvSeekAlpha: SeekBar? = null
    private var tvSizeSeekBar: SeekBar? = null

    private var textBold: android.widget.ImageView? = null
    private var textItalic: android.widget.ImageView? = null
    private var textCapital: android.widget.ImageView? = null
    private var textSmall: android.widget.ImageView? = null
    private var textUnderline: android.widget.ImageView? = null

    private var reTextFont: RecyclerView? = null

    private var reFontAdapter: FontAdapter? = null

    //Boolean var
    private var boldState = false
    private var italicState = false
    private var underlineState = false
    var layerStatusColor = false

    //ArrayList var
    private var fileNames: Array<String?>? = null

    private var btnDeleteText: android.widget.ImageView? = null
    private var btnChangeText: android.widget.TextView? = null

    private var updateNewText: UpdateNewText? = null
    private var addNewText: AddNewText? = null

    private var svgLayers: ConstraintLayout? = null
    private var svgAddText: ConstraintLayout? = null
    private var svgAddShape: ConstraintLayout? = null
    private var svgAddSticker: ConstraintLayout? = null
    private var svgAddImage: ConstraintLayout? = null

    private var textFont: ConstraintLayout? = null
    private var textSize: ConstraintLayout? = null
    private var textColor: ConstraintLayout? = null
    private var textOpacity: ConstraintLayout? = null
    private var textStyle: ConstraintLayout? = null


    private var svgContainerIcon: ArrayList<Any?> = ArrayList()
    private var textContainerIcon: ArrayList<Any?> = ArrayList()

    private var svgDownBtn: android.widget.ImageView? = null
    private var textDownBtn: android.widget.ImageView? = null

    private var svgRootContainer: ConstraintLayout? = null
    private var textRootContainer: ConstraintLayout? = null

    private var svgLayersContainer: View? = null
    private var svgAddStickerContainer: View? = null
    private var svgAddImageContainer: View? = null

    private var reTextColor: RecyclerView? = null
    private var textFontContainer: View? = null
    private var textSizeContainer: View? = null
    private var textOpacityContainer: View? = null
    private var textStyleContainer: View? = null

    private var reShape: RecyclerView? = null
    private var reSticker: RecyclerView? = null
    private var shapeAdapter: ShapeAdapter? = null
    private var stickerAdapter: StickerAdapter? = null
    private var importText: android.widget.TextView? = null

    var newCustomSticker: CustomImageView? = null
    private var stickerRoot: View? = null
    private var stickerDelete: android.widget.ImageView? = null
    private var stickerFlip: android.widget.ImageView? = null
    private var stickerCheckmark: android.widget.ImageView? = null
    private var stickerOpacitySeekBar: SeekBar? = null

    private var toolBack: ConstraintLayout? = null
    private var toolSave: ConstraintLayout? = null

    private var exportDialog: SheetPrintDialog? = null
    var sheetDialog: SheetDialog? = null

    var fileUri: Uri? = null
    private var importSticker: ConstraintLayout? = null

    var onActivityResultListener: OnActivityResultListener = this
    var onPermissionDeniedListener: OnPermissionDeniedListener = this
    var onPermissionPermanentlyDeniedListener: OnPermissionPermanentlyDeniedListener = this
    var onPermissionGrantedListener: OnPermissionGrantedListener = this

    private var cameraBtn: android.widget.ImageView? = null
    private var galleryBtn: android.widget.ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing_screen)

        workerHandler.post {
            initID()
        }

        exportDialog = SheetPrintDialog(this@EditingScreen, this)
        sheetDialog = SheetDialog(this@EditingScreen)
        sheetDialog?.setDialog()

    }



    private fun initID() {

        toolBack = findViewById(R.id.toolBack)
        toolSave = findViewById(R.id.toolSave)

        rootLayout = findViewById(R.id.root_layout)
        imageViewSVG = findViewById(R.id.imageView52)
        svgSeekAlpha = findViewById(R.id.seekBarSvg)
        svgColorPickerImage = findViewById(R.id.imageView14)
        textColorPickerImage = findViewById(R.id.imageView25)
        svgColorPrimaryImage = findViewById(R.id.imageView11)
        svgColorBlackImage = findViewById(R.id.imageView13)

        svgRecyclerView = findViewById(R.id.recyclerView2)
        svgRecyclerView?.setHasFixedSize(true)

        layerSelection = findViewById(R.id.layer)
        layerSelection?.isSelected = true

        textRoot = findViewById(R.id.text_root)
        layerRoot = findViewById(R.id.layer_root)

        tvSeekAlpha = findViewById(R.id.textOpacitySeekBar)
        tvSizeSeekBar = findViewById(R.id.textSizeSeekBar)

        textBold = findViewById(R.id.imageView20)
        textItalic = findViewById(R.id.imageView21)
        textCapital = findViewById(R.id.imageView22)
        textSmall = findViewById(R.id.imageView23)
        textUnderline = findViewById(R.id.imageView24)

        reTextFont = findViewById(R.id.reTextFont)
        reTextFont?.setHasFixedSize(true)

        btnDeleteText = findViewById(R.id.imageView12)
        btnChangeText = findViewById(R.id.textView7)

        svgLayers = findViewById(R.id.svgLayers)
        svgAddText = findViewById(R.id.svgAddText)
        svgAddShape = findViewById(R.id.svgAddShape)
        svgAddSticker = findViewById(R.id.svgAddSticker)
        svgAddImage = findViewById(R.id.svgAddImage)

        textFont = findViewById(R.id.textFont)
        textSize = findViewById(R.id.textSize)
        textColor = findViewById(R.id.textColor)
        textOpacity = findViewById(R.id.textOpacity)
        textStyle = findViewById(R.id.textStyle)

        svgContainerIcon.add(R.id.svgLayers)
        svgContainerIcon.add(R.id.svgAddShape)
        svgContainerIcon.add(R.id.svgAddSticker)
        svgContainerIcon.add(R.id.svgAddImage)

        textContainerIcon.add(R.id.textFont)
        textContainerIcon.add(R.id.textSize)
        textContainerIcon.add(R.id.textColor)
        textContainerIcon.add(R.id.textOpacity)
        textContainerIcon.add(R.id.textStyle)

        svgDownBtn = findViewById(R.id.imageView10)
        textDownBtn = findViewById(R.id.imageView19)

        svgRootContainer = findViewById(R.id.constraintLayout5)
        textRootContainer = findViewById(R.id.textRootContainer)
        svgLayersContainer = findViewById(R.id.svgLayersContainer)
        svgAddStickerContainer = findViewById(R.id.svgAddStickerContainer)
        svgAddImageContainer = findViewById(R.id.svgAddImageContainer)

        reTextColor = findViewById(R.id.reTextColor)
        reTextColor?.setHasFixedSize(true)
        textFontContainer = findViewById(R.id.textFontContainer)
        textSizeContainer = findViewById(R.id.textSizeContainer)
        textOpacityContainer = findViewById(R.id.textOpacityContainer)
        textStyleContainer = findViewById(R.id.textStyleContainer)

        importText = findViewById(R.id.textView299)

        importSticker = findViewById(R.id.constraintLayout2)
        reShape = findViewById(R.id.reShape)
        reShape?.setHasFixedSize(true)

        reSticker = findViewById(R.id.reSticker)
        reSticker?.setHasFixedSize(true)

        stickerRoot = findViewById(R.id.sticker_root)
        stickerDelete = findViewById(R.id.imageView333)
        stickerFlip = findViewById(R.id.imageView35)
        stickerCheckmark = findViewById(R.id.imageView343)
        stickerOpacitySeekBar = findViewById(R.id.stickerOpacitySeekBar)

        svgColor = findViewById(R.id.svgColor)
        svgColor?.setHasFixedSize(true)

        cameraBtn = findViewById(R.id.imageView17)
        galleryBtn = findViewById(R.id.imageView16)

        Log.d("myTemplate", "${Constant.categoryName} -- ${Constant.labelNumber}")

        val viewTreeObserver = rootLayout?.viewTreeObserver
        viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onGlobalLayout() {
                rootLayout?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                if (rootLayout != null) {
                    screenWidth = rootLayout!!.width.toDouble()
                }

                if (loadJSONFromAsset() != null) {

                    val obj = JSONObject(loadJSONFromAsset()!!)
                    val om = ObjectMapper()
                    val root: Root = om.readValue(obj.toString(), Root::class.java)

                    if (root.absoluteLayout != null) {
                        screenRatioFactor =
                            screenWidth / root.absoluteLayout!!.androidLayoutWidth!!.replace(
                                "dp",
                                ""
                            ).toDouble()

                        if (root.absoluteLayout!!.imageView != null) {
                            root.absoluteLayout!!.imageView!!.forEachIndexed { index, imageView ->
                                imageViewArray.add(index, imageView)
                            }
                        }
                        if (root.absoluteLayout!!.textView != null) {
                            root.absoluteLayout?.textView?.forEachIndexed { index, textview ->
                                textViewJson.add(index, textview)
                            }
                        }
                    }

                    if (imageViewArray.size > 0) {
                        addImage(imageViewArray)
                    }

                    if (textViewJson.size > 0) {
                        addText(textViewJson)
                    }

                } else {

                    Log.e("myError", "wrong json")
                }

            }
        })

        clickHandler()

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        showBackDialog()
    }

    fun finishEditing() {
        finish()
    }

    private fun showBackDialog() {

        AlertDialog.Builder(this@EditingScreen)
            .setMessage("Are you sure you want to exit Editing Screen?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                Utils.clearGarbageCollection()
                finish()
            }
            .setNegativeButton("No", null)
            .show()

    }

    private fun updateSVGColorAdapter() {
        svgColorAdapter = SVGColorAdapter(this)
        svgColor?.adapter = svgColorAdapter
        reTextColor?.adapter = svgColorAdapter
    }

    private fun updateStickerAdapter() {
        stickerAdapter = StickerAdapter(this)
        reSticker?.adapter = stickerAdapter
    }

    private fun updateShapeAdapter() {
        shapeAdapter = ShapeAdapter(this)
        reShape?.adapter = shapeAdapter
    }

    private fun clickHandler() {

        toolBack?.setOnClickListener {
            showBackDialog()
        }

        toolSave?.setOnClickListener {

            if (newCustomSticker != null) {
                newCustomSticker?.disableAllOthers()
            }

            if (currentText != null) {
                currentText!!.setBackgroundColor(Color.TRANSPARENT)
            }

            exportDialog?.show()
        }

        rootLayout?.setOnClickListener {

            if (textRoot?.visibility == View.VISIBLE) {
                textRoot?.visibility = View.GONE
            }

            if (layerRoot?.visibility == View.GONE) {
                layerRoot?.visibility = View.VISIBLE
            }

            if (currentText != null) {
                currentText!!.setBackgroundColor(Color.TRANSPARENT)
            }

            if (stickerRoot?.visibility == View.VISIBLE) {
                stickerRoot?.visibility = View.GONE
            }

            if (newCustomSticker != null) {
                newCustomSticker?.disableAllOthers()
            }

        }

        //code svg Seek Alpha
        svgSeekAlpha?.max = 10
        svgSeekAlpha?.progress = 10

        svgSeekAlpha?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                if (fromUser) {

                    Log.d("mySeekVal", "$progress")

                    currentLayer?.let {

                        if (progress == 10) {
                            it.alpha = 1.0f
                        } else {
                            it.alpha = "0.$progress".toFloat()
                        }
                    }

                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

        mColor = R.color.colorAccent

        val colorPickering = ColorPickerDialog
            .Builder(this@EditingScreen) // Pass Activity Instance
            .setColorShape(ColorShape.SQAURE) // Or ColorShape.CIRCLE
            .setDefaultColor(mColor) // Pass Default Color
            .setColorListener { color, _ ->
                mColor = color
                Log.d("myColor", "$mColor")

                if (textRoot?.visibility == View.VISIBLE) {

                    if (currentText != null) {
                        currentText!!.setTextColor(mColor)
                    }

                } else {
                    if (currentLayer != null) {
                        currentLayer?.setColorFilter(mColor)
                    } else {
                        Utils.showToast(this, "Plz select path")
                    }
                }

            }
            .setDismissListener {
                Log.d("ColorPickerDialog", "Handle dismiss event")
            }

        svgColorPickerImage?.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()

            colorPickering.show()
        })

        textColorPickerImage?.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()

            colorPickering.show()
        })

        svgColorPrimaryImage?.setOnClickListener {
            if (currentLayer != null) {
                currentLayer?.setColorFilter(Color.parseColor("#3D5FFA"))
            } else {
                Utils.showToast(this, "Plz select path")
            }
        }

        svgColorBlackImage?.setOnClickListener {
            if (currentLayer != null) {
                currentLayer?.setColorFilter(Color.parseColor("#000000"))
            } else {
                Utils.showToast(this, "Plz select path")
            }
        }

        try {

            fileNames = assets.list("font")

            if (fileNames != null) {
                reFontAdapter = FontAdapter(fileNames, this)
                reTextFont?.adapter = reFontAdapter
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        //SeekAlpha Code
        tvSeekAlpha?.max = 10
        tvSeekAlpha?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (currentText != null) {
                        if (progress == 10) {
                            currentText?.alpha = 1.0f
                        } else {
                            currentText?.alpha = "0.$progress".toFloat()
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        tvSizeSeekBar?.max = 300
        tvSizeSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (currentText != null) {
                        if (progress > 10) {
                            changeFontSize(progress, currentText!!)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        textBold?.setOnClickListener(textStyleListener)
        textItalic?.setOnClickListener(textStyleListener)
        textCapital?.setOnClickListener(textStyleListener)
        textSmall?.setOnClickListener(textStyleListener)
        textUnderline?.setOnClickListener(textStyleListener)

        updateNewText = UpdateNewText(this@EditingScreen, this)
        addNewText = AddNewText(this@EditingScreen, this)

        btnDeleteText?.setOnClickListener {
            if (currentText != null) {
                rootLayout!!.removeView(currentText)
                currentText = null
                textRoot?.visibility = View.GONE
                layerRoot?.visibility = View.VISIBLE
            }

        }

        btnChangeText?.setOnClickListener {
            if (currentText != null) {
                updateNewText?.show(currentText!!.text.toString())
            }
        }

        svgLayers?.setOnClickListener(svgClickListener)
        svgAddText?.setOnClickListener(svgClickListener)
        svgAddShape?.setOnClickListener(svgClickListener)
        svgAddSticker?.setOnClickListener(svgClickListener)
        svgAddImage?.setOnClickListener(svgClickListener)

        textFont?.setOnClickListener(textMenuClickListener)
        textSize?.setOnClickListener(textMenuClickListener)
        textColor?.setOnClickListener(textMenuClickListener)
        textOpacity?.setOnClickListener(textMenuClickListener)
        textStyle?.setOnClickListener(textMenuClickListener)

        textDownBtn?.setOnClickListener {
            textRoot?.visibility = View.GONE
            alphaManagerForAll(this, textContainerIcon)
            if (layerRoot?.visibility == View.GONE) {
                layerRoot?.visibility = View.VISIBLE
            }
        }

        svgDownBtn?.setOnClickListener {
            svgDownBtn?.visibility = View.GONE
            svgRootContainer?.visibility = View.GONE
            alphaManagerForAll(this, svgContainerIcon)
        }

        //SeekAlpha Code
        stickerOpacitySeekBar?.max = 10
        stickerOpacitySeekBar?.progress = 10
        stickerOpacitySeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    Log.e("mySeek", "$progress")
                    if (newCustomSticker != null) {
                        if (progress == 10) {
                            newCustomSticker?.imageView?.alpha = 1.0f
                        } else {
                            newCustomSticker?.imageView?.alpha = "0.$progress".toFloat()
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        updateStickerAdapter()

        updateShapeAdapter()

        updateSVGColorAdapter()

        stickerRoot?.setOnClickListener(emptyClickListener)
        svgLayersContainer?.setOnClickListener(emptyClickListener)

        stickerDelete?.setOnClickListener {
            newCustomSticker?.deleteObject()
            stickerRoot?.visibility = View.GONE
        }

        stickerCheckmark?.setOnClickListener {
            newCustomSticker?.disableAllOthers()
            stickerRoot?.visibility = View.GONE
        }

        stickerFlip?.setOnClickListener {
            newCustomSticker?.flipRoot()
        }

        importSticker?.setOnClickListener {
            importImageSticker()
        }

        cameraBtn?.setOnClickListener {
            Dexter.withContext(this@EditingScreen)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        pickCameraImage()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Utils.showToast(this@EditingScreen, "Perssmion is Denied")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        }

        galleryBtn?.setOnClickListener {
            importImageSticker()
        }

    }

    @Suppress("DEPRECATION")
    fun pickCameraImage() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(pictureIntent, Constant.REQUEST_CAPTURE_IMAGE)
    }

    private fun importImageSticker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pickGalleryImageNewApi()
        } else {
            pickGalleryImage()
        }
    }

    private fun pickGalleryImageNewApi() {
        checkPermissions()
    }

    //This permission Check only Android Q and Above
    @SuppressLint("InlinedApi")
    fun checkPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isDenied = 2
            permissions.entries.forEach {
                if (it.value == false) {
                    val showRationale1: Boolean = shouldShowRequestPermissionRationale(it.key)
                    isDenied = if (!showRationale1) {
                        0
                    } else {
                        1
                    }
                }
                Log.e("DEBUG", "${it.key} = ${it.value}")
            }


            //ShowPrompt(context,permissions);
            when (isDenied) {
                1 -> onPermissionDeniedListener.onPermissionDenied()
                0 -> onPermissionPermanentlyDeniedListener.onPermissionPermanentlyDenied()
                else -> onPermissionGrantedListener.onPermissionGranted()
            }

        }

    @Suppress("DEPRECATION")
    fun pickGalleryImage() {
        try {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            if (intent.resolveActivity(packageManager) != null) {
                this.startActivityForResult(intent, Constant.REQUEST_GELLERY_IMAGE)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            this.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                Constant.REQUEST_GELLERY_IMAGE
            )
        }
    }

    private val emptyClickListener = View.OnClickListener {
        Log.d("myEmptyClick", "Click")
    }

    private fun alphaManagerForAll(activity: Activity, views: ArrayList<Any?>) {
        for (i in views.indices) {
            activity.findViewById<View>((views[i] as Int?)!!).alpha = "0.40".toFloat()
        }
    }

    private val svgClickListener = View.OnClickListener { v: View ->

        alphaManager(this, svgContainerIcon, v.id)

        if (svgDownBtn?.visibility == View.GONE) {
            svgDownBtn?.visibility = View.VISIBLE
        }

        if (svgRootContainer?.visibility == View.GONE) {
            svgRootContainer?.visibility = View.VISIBLE
        }

        when (v.id) {

            R.id.svgLayers -> {
                showLayerContainer()
            }

            R.id.svgAddText -> {
                addNewText?.show()
            }

            R.id.svgAddShape -> {
                showStickerContainer(1)
            }

            R.id.svgAddSticker -> {
                showStickerContainer(2)
            }

            R.id.svgAddImage -> {
                showImageContainer()
            }

            else -> {
                Utils.showToast(this, "No thing click")
            }
        }
    }

    private val textMenuClickListener = View.OnClickListener { v: View ->

        alphaManager(this, textContainerIcon, v.id)

        if (textDownBtn?.visibility == View.GONE) {
            textDownBtn?.visibility = View.VISIBLE
        }

        if (textRootContainer?.visibility == View.GONE) {
            textRootContainer?.visibility = View.VISIBLE
        }

        when (v.id) {

            R.id.textFont -> {
                showTextFontContainer()
            }

            R.id.textSize -> {
                showTextSizeContainer()
            }

            R.id.textColor -> {
                showTextColorContainer()
            }

            R.id.textOpacity -> {
                showTextOpacityContainer()
            }

            R.id.textStyle -> {
                showTextStyleContainer()
            }
            else -> {
                Utils.showToast(this, "No thing click")
            }

        }

    }

    private fun showTextStyleContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
                textColorPickerImage?.visibility = View.GONE
            }
        }
    }

    private fun showTextOpacityContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
                textColorPickerImage?.visibility = View.GONE
            }
        }
    }

    private fun showTextColorContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
                textColorPickerImage?.visibility = View.VISIBLE
            }
        }
    }

    private fun showTextFontContainer() {

        textFontContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
                textColorPickerImage?.visibility = View.GONE
            }
        }
    }

    private fun showTextSizeContainer() {

        textFontContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textSizeContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        textOpacityContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        textStyleContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        reTextColor?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
                textColorPickerImage?.visibility = View.GONE
            }
        }
    }

    private fun showImageContainer() {
        svgLayersContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        svgAddStickerContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        svgAddImageContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }
    }

    private fun showLayerContainer() {

        alphaManager(this, svgContainerIcon, R.id.svgLayers)

        if (svgDownBtn?.visibility == View.GONE) {
            svgDownBtn?.visibility = View.VISIBLE
        }

        if (svgRootContainer?.visibility == View.GONE) {
            svgRootContainer?.visibility = View.VISIBLE
        }

        svgLayersContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        svgAddStickerContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        svgAddImageContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        if (currentText != null) {
            currentText!!.setBackgroundColor(Color.TRANSPARENT)
        }


    }

    private fun showStickerContainer(values: Int) {

        Log.d("myShape", "$values")

        if (values == 1) {
            if (reShape?.visibility == View.GONE) {
                importText?.setText(R.string.import_shape)
                reShape?.visibility = View.VISIBLE
                reSticker?.visibility = View.GONE
            }
        } else {
            if (reSticker?.visibility == View.GONE) {
                importText?.setText(R.string.import_sticker)
                reSticker?.visibility = View.VISIBLE
                reShape?.visibility = View.GONE
            }
        }

        svgLayersContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

        svgAddStickerContainer?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        svgAddImageContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }

    }

    private fun alphaManager(activity: Activity, views: ArrayList<Any?>, view_id: Int) {
        for (i in views.indices) {
            if (views[i] == view_id) {
                activity.findViewById<View>((views[i] as Int?)!!).alpha = "1.0".toFloat()
            } else {
                activity.findViewById<View>((views[i] as Int?)!!).alpha = "0.40".toFloat()
            }
        }
    }

    private
    val textStyleListener = View.OnClickListener { v: View ->
        currentText?.let {
            when (v.id) {

                R.id.imageView20 -> if (boldState) {
                    if (italicState) {
                        textBold!!.isSelected = false
                        boldState = false
                        currentText?.let {
                            setItalicStyle(true)
                        }

                    } else {
                        textBold!!.isSelected = false
                        boldState = false
                        currentText?.let {
                            setBoldStyle(false)
                        }
                    }

                } else {

                    if (italicState) {
                        textBold!!.isSelected = true
                        boldState = true
                        currentText?.let {
                            setBoldItalic()
                        }
                    } else {
                        textBold!!.isSelected = true
                        boldState = true
                        currentText?.let {
                            setBoldStyle(true)
                        }
                    }
                }

                R.id.imageView21 -> if (italicState) {
                    if (boldState) {
                        textItalic!!.isSelected = false
                        currentText?.let {
                            setBoldStyle(true)
                        }
                        italicState = false
                    } else {
                        textItalic!!.isSelected = false
                        currentText?.let {
                            setItalicStyle(false)
                        }
                        italicState = false
                    }
                } else {

                    if (boldState) {
                        textItalic!!.isSelected = true
                        currentText?.let {
                            setBoldItalic()
                        }
                        italicState = true
                    } else {
                        textItalic!!.isSelected = true
                        currentText?.let {
                            setItalicStyle(true)
                        }
                        italicState = true
                    }
                }
                R.id.imageView22 -> {
                    setUppercase()
                    textSmall!!.isSelected = false
                    textCapital!!.isSelected = true
                }
                R.id.imageView23 -> {
                    setLowerCase()
                    textSmall!!.isSelected = true
                    textCapital!!.isSelected = false
                }
                R.id.imageView24 -> if (underlineState) {
                    setUnderLine()
                    textUnderline!!.isSelected = false
                    underlineState = false
                } else {
                    setUnderLine()
                    textUnderline!!.isSelected = true
                    underlineState = true
                }
                else -> {
                    Utils.showToast(this, "default")
                }
            }
        }
    }

    private fun setBoldStyle(isBold: Boolean) {
        val oldTypeface =
            if (currentText?.typeface != null) currentText?.typeface else if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        currentText?.typeface =
            Typeface.create(oldTypeface, if (isBold) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun setItalicStyle(isBold: Boolean) {
        val oldTypeface =
            if (currentText?.typeface != null) currentText?.typeface else if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        currentText?.typeface =
            Typeface.create(oldTypeface, if (isBold) Typeface.ITALIC else Typeface.NORMAL)
    }

    private fun setBoldItalic() {
        val typeface = Typeface.create(currentText?.typeface, Typeface.BOLD_ITALIC)
        (currentText)?.typeface = typeface
    }

    private fun setUppercase() {
        currentText?.let {
            it.text = it.text.toString().uppercase(Locale.ROOT)
        }
    }

    private fun setLowerCase() {
        currentText?.let {
            it.text = it.text.toString().lowercase(Locale.ROOT)
        }
    }

    private fun setUnderLine() {

        try {

            currentText?.let {
                if (it.paintFlags == Paint.UNDERLINE_TEXT_FLAG) {
                    it.paintFlags = it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                } else {
                    it.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                }
            }

        } catch (exception: IllegalStateException) {
            exception.printStackTrace()
        }
    }

    private fun changeFontSize(fontSize: Int, currentEditText: android.widget.TextView) {

        val oldW: Int = currentEditText.width
        val oldH: Int = currentEditText.height

        Log.e("changeFontSize", "OLD= $oldW, $oldH, ${currentEditText.x}, ${currentEditText.y}")

        currentEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.toFloat())

        val newW: Float = currentEditText.width.toFloat()
        val factor = newW / oldW

        val cmDist: Float = (currentEditText.cameraDistance * factor)

        if (cmDist > 0) {

            try {
                currentEditText.cameraDistance = cmDist
            } catch (ex: IllegalArgumentException) {
            }
        }

    }

    private var newImageView: android.widget.ImageView? = null

    private fun addImage(array: ArrayList<ImageView>) {

        svgLayerPathList.clear()

        array.forEachIndexed { index, imageView ->

            newImageView = android.widget.ImageView(this@EditingScreen)

            var path: String? = null

            if (imageView.appSrcCompat != null) {

                try {
                    path =
                        "file:///android_asset/category/${Constant.categoryName}/assets/${Constant.labelNumber}/$index.png"
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

                path?.let {
                    Glide.with(this@EditingScreen)
                        .load(it)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(newImageView!!)
                }

            }

            if (imageView.androidBackground != null) {

                try {
                    path =
                        "file:///android_asset/category/${Constant.categoryName}/assets/${Constant.labelNumber}/0.png"
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

                path?.let {

                    Glide.with(this@EditingScreen)
                        .load(it)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(newImageView!!)

                    Glide.with(this@EditingScreen)
                        .load(it)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewSVG!!)

                    currentLayer = newImageView
                }

            }

            svgLayerPathList.add(path!!)

            if (imageView.androidLayoutWidth != null && imageView.androidLayoutHeight != null) {

                val width = (imageView.androidLayoutWidth.toString()
                    .replace("dp", "")).toInt() * screenRatioFactor
                val height = (imageView.androidLayoutHeight.toString()
                    .replace("dp", "")).toInt() * screenRatioFactor
                val layoutParams = RelativeLayout.LayoutParams(width.toInt(), height.toInt())
                newImageView?.layoutParams = layoutParams
            }

            if (imageView.androidLayoutX != null) {
                newImageView?.x = (imageView.androidLayoutX!!.replace("dp", "").toDouble()
                        * screenRatioFactor).toFloat()
            }

            if (imageView.androidLayoutY != null) {
                newImageView?.y = (imageView.androidLayoutY!!.replace("dp", "").toDouble()
                        * screenRatioFactor).toFloat()
            }

            if (imageView.androidRotation != null) {
                newImageView?.rotation = imageView.androidRotation!!.toFloat()
            }

            if (imageView.androidAlpha != null) {
                newImageView?.alpha = imageView.androidAlpha!!.toFloat()
            }

            rootLayout?.addView(newImageView)

            if (index + 1 == array.size) {

                @SuppressLint("NotifyDataSetChanged")
                if (svgLayerPathList.size > 0) {
                    Log.d("mySize", "${svgLayerPathList.size}")
                    svgLayersAdapter = SVGLayersAdapter(svgLayerPathList, this)
                    svgRecyclerView?.adapter = svgLayersAdapter
                    svgLayersAdapter?.notifyDataSetChanged()

                }
            }

        }

    }

    private var newTextView: android.widget.TextView? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun addText(artist: ArrayList<TextView>) {

        artist.forEachIndexed { _, textView ->

            newTextView = android.widget.TextView(this@EditingScreen)

            if (!textView.androidLayoutWidth.equals("wrap_content") && !textView.androidLayoutHeight.equals(
                    "wrap_content"
                )
            ) {

                val width =
                    (textView.androidLayoutWidth.toString()
                        .replace("dp", "")).toInt() * screenRatioFactor
                val height =
                    (textView.androidLayoutHeight.toString()
                        .replace("dp", "")).toInt() * screenRatioFactor
                val params = RelativeLayout.LayoutParams(width.toInt(), height.toInt())
                newTextView?.layoutParams = params

            }

            if (textView.androidText != null) {
                newTextView?.text = textView.androidText
            }

            if (textView.androidTextColor != null) {
                newTextView?.setTextColor(Color.parseColor(textView.androidTextColor))
            }

            if (textView.androidLayoutX != null) {
                newTextView?.x = (textView.androidLayoutX!!.replace("dp", "").toDouble()
                        * screenRatioFactor).toFloat()
            }

            if (textView.androidLayoutY != null) {
                newTextView?.y = (textView.androidLayoutY!!.replace("dp", "").toDouble()
                        * screenRatioFactor).toFloat()
            }

            if (textView.androidTextSize != null) {
                newTextView?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    ((textView.androidTextSize?.replace(
                        "sp",
                        ""
                    ))?.toFloat()?.times(screenRatioFactor))!!.toFloat()
                )
            }

            if (textView.androidBackground != null) {
                newTextView?.setBackgroundColor(Color.parseColor(textView.androidBackground))
            }

            if (textView.androidLetterSpacing != null) {
                newTextView?.letterSpacing = textView.androidLetterSpacing!!.toFloat()
            }

            if (textView.androidRotation != null) {
                newTextView?.rotation = textView.androidRotation!!.toFloat()
            }

            if (textView.androidTextAlignment != null) {

                when {
                    textView.androidTextAlignment.equals("center", ignoreCase = true) -> {
                        newTextView?.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    }
                    textView.androidTextAlignment.equals("textEnd", ignoreCase = true) -> {
                        newTextView?.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                    }
                    textView.androidTextAlignment.equals("textStart", ignoreCase = true) -> {
                        newTextView?.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    }
                }
            }

            if (textView.androidAlpha != null) {
                newTextView?.alpha = textView.androidAlpha!!.toFloat()
            }

            if (textView.androidFontFamily != null) {

                val typeface = Typeface.createFromAsset(assets, "font/${textView.androidFontFamily.toString().replace("@font/", "")}.ttf")
                if (typeface != null) {
                    newTextView?.typeface = typeface
                }

                if (textView.androidTextStyle != null) {
                    when {
                        textView.androidTextStyle.equals("bold") -> {
                            newTextView?.setTypeface(typeface, Typeface.BOLD)
                        }
                        textView.androidTextStyle.equals("italic") -> {
                            newTextView?.setTypeface(typeface, Typeface.ITALIC)
                        }
                        textView.androidTextStyle.equals("normal") -> {
                            newTextView?.setTypeface(typeface, Typeface.NORMAL)
                        }
                    }
                }

            }

            val moveViewTouchListener = MoveViewTouchListener(this, newTextView)

            newTextView?.setOnTouchListener(moveViewTouchListener)
            moveViewTouchListener.callBacks = this
            rootLayout?.addView(newTextView)

        }
    }

    private fun loadJSONFromAsset(): String? {

        val json: String? = try {
            val `is`: InputStream =
                this.assets.open("category/${Constant.categoryName}/json/${Constant.labelNumber}.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json

    }

    override fun setOnLayersClickListener(position: Int) {

        currentLayer = rootLayout?.get(position) as android.widget.ImageView

        if (svgLayerPathList.size > 0) {

            Glide.with(this@EditingScreen)
                .load(svgLayerPathList[position])
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewSVG!!)
        }

        if (currentLayer?.alpha == 1.0f) {
            svgSeekAlpha?.progress = 10
        } else {
            svgSeekAlpha?.progress = currentLayer?.alpha.toString().replace("0.", "").toInt()
        }
    }

    override fun showTextControls() {

    }

    override fun setCurrentText(view: View) {
        callingText(view)
    }

    private fun callingText(view: View) {

        if (textRoot?.visibility == View.GONE) {
            textRoot?.visibility = View.VISIBLE
        }

        if (layerRoot?.visibility == View.VISIBLE) {
            layerRoot?.visibility = View.GONE
        }

        if (currentText != null) {

            currentText!!.setBackgroundColor(Color.TRANSPARENT)
            currentText = view as android.widget.TextView
            currentText!!.background = ContextCompat.getDrawable(this, R.drawable.my_border)


        } else {

            currentText = view as android.widget.TextView
            currentText!!.background = ContextCompat.getDrawable(this, R.drawable.my_border)

        }

        if (currentText!!.alpha.toString().replace("0.", "") == "1.0") {
            tvSeekAlpha!!.progress = 10
        } else {
            tvSeekAlpha!!.progress =
                currentText!!.alpha.toString().replace("0.", "").toFloat().roundToInt().toString()
                    .toInt()
        }

        if (currentText?.textSize?.toInt()!! <= 300) {
            tvSizeSeekBar?.progress = (currentText?.textSize?.toInt()!!)
        }

        when (currentText!!.typeface.style) {

            Typeface.BOLD_ITALIC -> {
                textBold!!.isSelected = true
                textItalic!!.isSelected = true
                boldState = true
                italicState = true
            }

            Typeface.BOLD -> {
                textBold!!.isSelected = true
                textItalic!!.isSelected = false
                boldState = true
                italicState = false
            }

            Typeface.ITALIC -> {
                textBold!!.isSelected = false
                textItalic!!.isSelected = true
                boldState = false
                italicState = true
            }

            else -> {
                textBold!!.isSelected = false
                textItalic!!.isSelected = false
                boldState = false
                italicState = false
            }
        }

        if (currentText!!.text.toString().matches(Regex(".*[a-z].*"))
            && !currentText!!.text.toString().matches(Regex(".*[A-Z].*"))
        ) {
            textCapital!!.isSelected = false
            textSmall!!.isSelected = true
        } else if (currentText!!.text.toString().matches(Regex(".*[A-Z].*"))
            && !currentText!!.text.toString().matches(Regex(".*[a-z].*"))
        ) {
            textCapital!!.isSelected = true
            textSmall!!.isSelected = false
        } else {
            textSmall!!.isSelected = false
            textCapital!!.isSelected = false
        }

        if (currentText!!.paintFlags == Paint.UNDERLINE_TEXT_FLAG) {
            underlineState = true
            textUnderline!!.isSelected = true
        } else {
            underlineState = false
            textUnderline!!.isSelected = false
        }

    }

    override fun setFont(fontName: String) {
        Log.d("myFont",fontName)
        currentText?.let {
            val tf = Typeface.createFromAsset(assets, "font/$fontName")
            it.typeface = tf
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun addText(string: String?) {

        if (string != null) {

            val newText = android.widget.TextView(this)

            newText.id = View.generateViewId()
            newText.text = string
            newText.isCursorVisible = false
            newText.setTextColor(Color.BLACK)

            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            newText.layoutParams = params

            val textSizePx = Utils.dpToPx(30f, this)

            newText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx.toFloat())

            newText.y = 100f
            newText.x = 150f

            rootLayout!!.addView(newText)

            if (rootLayout?.childCount != 0) {

                for (i in 0 until rootLayout!!.childCount) {

                    if (rootLayout!!.getChildAt(i) is android.widget.TextView) {

                        val textView = rootLayout!!.getChildAt(i) as android.widget.TextView

                        val viewTreeObserver = textView.viewTreeObserver

                        if (viewTreeObserver.isAlive) {

                            viewTreeObserver.addOnGlobalLayoutListener(object :
                                ViewTreeObserver.OnGlobalLayoutListener {

                                override fun onGlobalLayout() {
                                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    /*  parentWidth = layoutMove!!.width
                                      parentHeight = layoutMove!!.height
                                      newTextColor = textView.currentTextColor*/

                                }
                            })
                        }

                        val moveViewTouchListener =
                            MoveViewTouchListener(
                                this,
                                rootLayout!!.getChildAt(i) as android.widget.TextView
                            )
                        rootLayout!!.getChildAt(i).setOnTouchListener(moveViewTouchListener)
                        moveViewTouchListener.callBacks = this
                    }
                }
            }

        } else {
            Log.d("myTextValue", "Text is null")
        }

    }

    override fun updateText(string: String) {
        currentText?.let {
            it.text = string
        }
    }

    override fun setOnStickerClickListener(position: Int, isShapeOrNot: Boolean) {

        newCustomSticker = CustomImageView(this)
        newCustomSticker?.updateCallBack(this@EditingScreen)

        if (isShapeOrNot) {

            val path = "category/shape/${position}.png"

            Log.d("myStickerIs", path)

            val newBit: Bitmap? = Utils.getBitmapFromAsset(this, path)

            newCustomSticker?.let {

                if (newBit != null) {
                    it.setBitMap(newBit)
                }

                rootLayout?.addView(it)
            }


        } else {

            val path = "category/sticker/${position}.webp"
            Log.d("myStickerIs", path)

            val newBit: Bitmap? = Utils.getBitmapFromAsset(this, path)

            newCustomSticker?.let {

                if (newBit != null) {
                    it.setBitMap(newBit)
                }

                rootLayout?.addView(it)
            }

        }

    }

    override fun stickerViewClickDown(currentView: CustomImageView) {

        newCustomSticker = currentView
        if (newCustomSticker != null) {
            newCustomSticker?.disableAllOthers()
        }
        showLayerContainer()

        stickerRoot?.let {
            if (it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }

        if (newCustomSticker?.imageView?.alpha.toString().replace("0.", "") == "1.0") {
            stickerOpacitySeekBar?.progress = 10
        } else {
            stickerOpacitySeekBar?.progress = newCustomSticker?.imageView?.alpha.toString()
                .replace("0.", "").toFloat().roundToInt().toString().toInt()
        }

    }

    override fun stickerViewDeleteClick() {

    }

    override fun stickerViewScrollViewEnable() {

    }

    override fun stickerViewScrollViewDisable() {

    }

    override fun setOnColorClickListener(position: Int) {
        if (currentText != null) {
            currentText!!.setTextColor(ContextCompat.getColor(this, Constant.colorArray[position]))
        }
    }

    override fun saveSingleLabel() {
        finalLabelDialog()
    }

    override fun saveLabelSheet() {
        saveSheetSizeDialog()
    }

    override fun sheetPrintDialogClose() {

    }

    private fun finalLabelDialog() {
        val saveImg: android.widget.ImageView
        val shareImage: android.widget.ImageView
        val backImage: android.widget.ImageView
        val finalLabel: android.widget.ImageView
        val savePDF: android.widget.ImageView
        val homeBtn: android.widget.ImageView
        val customDialog = Dialog(this, R.style.full_screen_dialog2)
        val view = View.inflate(this, R.layout.final_label_dialog, null)
        Objects.requireNonNull(customDialog.window)
            ?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        customDialog.window!!.statusBarColor =
            ContextCompat.getColor(this, R.color.colorPrimaryDark)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
        customDialog.setContentView(view)
        customDialog.show()
        finalLabel = view.findViewById(R.id.final_label)
        saveImg = view.findViewById(R.id.gallery_iv)
        savePDF = view.findViewById(R.id.imageView11)
        shareImage = view.findViewById(R.id.imageView43)
        backImage = view.findViewById(R.id.back_btn)
        homeBtn = view.findViewById(R.id.home_btn)

        rootLayout?.let {

            Glide.with(this)
                .load(it.drawToBitmap(Bitmap.Config.ARGB_8888))
                .centerInside()
                .into(finalLabel)
        }

        saveImg.setOnClickListener {

            rootLayout?.let {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    Dexter.withContext(this@EditingScreen)
                        .withPermissions(
                            Manifest.permission.ACCESS_MEDIA_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    workerThread.execute {

                                        val saveFilePath: String? = saveMediaToStorage(
                                            it.drawToBitmap(Bitmap.Config.ARGB_8888)
                                        )

                                        if (saveFilePath != null && File(saveFilePath).exists()) {

                                            workerHandler.post {
                                                Utils.showToast(this@EditingScreen, "$saveFilePath")
                                                Log.e("myFileFos", "Saved file and not null")
                                            }

                                        } else {
                                            workerHandler.post {
                                                Utils.showToast(
                                                    this@EditingScreen,
                                                    resources.getString(R.string.something_went_wrong)
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

                    Dexter.withContext(this@EditingScreen)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                                workerThread.execute {

                                    val saveFilePath: String? =
                                        saveMediaToStorage(
                                            it.drawToBitmap(Bitmap.Config.ARGB_8888)
                                        )

                                    if (saveFilePath != null && File(saveFilePath).exists()) {

                                        workerHandler.post {
                                            Utils.showToast(this@EditingScreen, "$saveFilePath")
                                            Log.e("myFileFos", "Saved file and not null")
                                        }

                                    } else {
                                        workerHandler.post {
                                            Utils.showToast(
                                                this@EditingScreen,
                                                resources.getString(R.string.something_went_wrong)
                                            )
                                        }
                                    }

                                }

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                Utils.showToast(this@EditingScreen, "Perssmion is Denied")
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

        savePDF.setOnClickListener {

            rootLayout?.let {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    Dexter.withContext(this@EditingScreen)
                        .withPermissions(
                            Manifest.permission.ACCESS_MEDIA_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    workerThread.execute {

                                        val filePath = savePdfFileToScopeStorage(it)

                                        if (filePath != null && File(filePath).exists()) {

                                            workerHandler.post {
                                                Utils.showToast(this@EditingScreen, "$filePath")
                                                Log.e("myFileFos", "Saved file and not null")
                                            }

                                        } else {
                                            workerHandler.post {
                                                Utils.showToast(
                                                    this@EditingScreen,
                                                    resources.getString(R.string.something_went_wrong)
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

                    Dexter.withContext(this@EditingScreen)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                                workerThread.execute {

                                    val filePath = savePdfFileToScopeStorage(it)

                                    if (filePath != null && File(filePath).exists()) {

                                        workerHandler.post {
                                            Utils.showToast(this@EditingScreen, "$filePath")
                                            Log.e("myFileFos", "Saved file and not null")
                                        }

                                    } else {
                                        workerHandler.post {
                                            Utils.showToast(
                                                this@EditingScreen,
                                                resources.getString(R.string.something_went_wrong)
                                            )
                                        }
                                    }
                                }

                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                Utils.showToast(this@EditingScreen, "Perssmion is Denied")
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

        shareImage.setOnClickListener {
            shareSinglePdfFile()
        }

        backImage.setOnClickListener { customDialog.dismiss() }

        homeBtn.setOnClickListener {
            finish()
        }

    }

    var sta = true

    private fun saveSheetSizeDialog() {

        val height: EditText
        val width: EditText
        val printBtn: android.widget.TextView
        val reset: android.widget.TextView
        val closeDialog: android.widget.ImageView
        val ratioVal = 1.0f
        val view = View.inflate(this, R.layout.save_pdf_dialog, null)
        val dialog = Dialog(this@EditingScreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
        dialog.setContentView(view)
        dialog.show()
        height = view.findViewById(R.id.height)
        width = view.findViewById(R.id.width)
        printBtn = view.findViewById(R.id.print_btn)
        reset = view.findViewById(R.id.textView262)
        closeDialog = view.findViewById(R.id.imageView39)

        width.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                sta = true
                Log.e("calling", "${v.id}")
            }
            false
        }

        height.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                sta = false
                Log.e("calling", "${v.id}")
            }
            false
        }

        closeDialog.setOnClickListener {
            dialog.dismiss()
        }

        width.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Log.e("calling", "beforeTextChanged");
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //Log.e("calling", "onTextChanged");
            }

            override fun afterTextChanged(s: Editable) {
                //Log.e("calling", "afterTextChanged");
                if (sta) {

                    if (width.text.toString().trim { it <= ' ' } != "") {

                        if (width.text.toString().trim { it <= ' ' } == ".") {

                            val w = ("0" + width.text.toString().trim { it <= ' ' }).toFloat()

                            if (w in 0.1f..8.0f) {
                                sta = true
                                if (w / ratioVal <= 11.0f) {
                                    height.setText((w / ratioVal).toString())
                                } else {
                                    height.setText("")
                                }
                                height.setBackgroundColor(Color.parseColor("#D3D3D3"))
                                height.isEnabled = false

                                //quantity.setText(String.valueOf(calculateSheetSize(Float.valueOf(width.getText().toString().trim()), Float.valueOf(height.getText().toString().trim()))));
                            } else {
                                Log.e("calling", "No Change")
                                height.setText("")
                                //quantity.setText("");
                            }

                        } else {
                            val w = ("0" + width.text.toString().trim { it <= ' ' }).toFloat()
                            if (w in 0.1f..8.0f) {
                                sta = true
                                if (w / ratioVal <= 11.0f) {
                                    height.setText((w / ratioVal).toString())
                                } else {
                                    height.setText("")
                                }
                                height.setBackgroundColor(Color.parseColor("#D3D3D3"))
                                height.isEnabled = false

                                //quantity.setText(String.valueOf(calculateSheetSize(Float.valueOf(width.getText().toString().trim()), Float.valueOf(height.getText().toString().trim()))));
                            } else {
                                Log.e("calling", "No Change")
                                height.setText("")
                                //quantity.setText("");
                            }
                        }
                    } else {
                        height.setText("")
                        //height.setFocusable(true);
                        //quantity.setText("");
                    }
                }
            }
        })

        height.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Log.e("calling", "beforeTextChanged");
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //Log.e("calling", "onTextChanged");
            }

            override fun afterTextChanged(s: Editable) {
                //Log.e("calling", "afterTextChanged");
                if (!sta) {

                    if (height.text.toString() != "") {

                        if (height.text.toString().trim { it <= ' ' } == ".") {

                            val h = ("0" + height.text.toString().trim { it <= ' ' }).toFloat()

                            if (h in 0.1f..11f) {

                                sta = false

                                if (ratioVal * h <= 8.0f) {
                                    width.setText((ratioVal * h).toString())
                                } else {
                                    width.setText("")
                                }
                                width.isEnabled = false
                                width.setBackgroundColor(Color.parseColor("#D3D3D3"))

                                //quantity.setText(String.valueOf(calculateSheetSize(Float.valueOf(width.getText().toString().trim()), h)));
                            } else {
                                Log.e("calling", "No Change")
                                width.setText("")
                                //quantity.setText("");
                            }
                        } else {
                            val h = height.text.toString().trim { it <= ' ' }.toFloat()
                            if (h in 0.1f..11f) {
                                sta = false
                                if (ratioVal * h <= 8.0f) {
                                    width.setText((ratioVal * h).toString())
                                } else {
                                    width.setText("")
                                }
                                width.isEnabled = false
                                width.setBackgroundColor(Color.parseColor("#D3D3D3"))

                                //quantity.setText(String.valueOf(calculateSheetSize(Float.valueOf(width.getText().toString().trim()), h)));
                            } else {
                                Log.e("calling", "No Change")
                                width.setText("")
                                //quantity.setText("");
                            }
                        }
                    } else {
                        width.setText("")
                        //width.setFocusable(true);
                        //quantity.setText("");
                    }
                }
            }
        })

        printBtn.setOnClickListener { v ->

            if (height.text.toString().trim { it <= ' ' } != "" && width.text.toString()
                    .trim { it <= ' ' } != ""
                && height.text.toString().trim { it <= ' ' }
                    .toFloat() <= 11f && width.text.toString().trim { it <= ' ' }
                    .toFloat() <= 8.0f) {

                dialog.dismiss()

                Utils.hideKeyboardFromView(v)

                Handler(Looper.getMainLooper()).postDelayed({
                    rootLayout?.let {
                        sheetDialog?.showDialog(
                            width.text.toString().trim { it <= ' ' } + "x" + height.text.toString()
                                .trim { it <= ' ' },
                            rootLayout!!.drawToBitmap(Bitmap.Config.ARGB_8888)
                        )
                    }

                }, 500)

            } else {
                Utils.showToast(this@EditingScreen, "Please enter right label Size")
            }
        }

        reset.setOnClickListener { v ->
            sta = true
            width.isEnabled = true
            width.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.pdf_bg,
                null
            )
            width.setText("")
            height.isEnabled = true
            height.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.pdf_bg,
                null
            )
            height.setText("")

            Utils.hideKeyboardFromView(v)
        }

    }

    //*********************************Method for shear pdf***************************************//
    private fun shareSinglePdfFile() {

        if (currentText != null) {
            currentText!!.setBackgroundColor(Color.TRANSPARENT)
        }

        //File Name as a current time Name
        val mFileName = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val file = externalCacheDir?.absolutePath

        if (file != null) {

            val path = File(file)

            if (!path.exists()) {
                path.mkdirs()
            }

            val newFile = File(path, "PDF\n$mFileName.pdf")
            val document = PdfDocument()
            val pageInfo =
                PdfDocument.PageInfo.Builder(rootLayout!!.width, rootLayout!!.height, 1).create()
            val page = document.startPage(pageInfo)
            val content: View? = rootLayout
            content!!.draw(page.canvas)
            rootLayout!!.draw(page.canvas)

            // finish the page
            document.finishPage(page)
            try {

                document.writeTo(FileOutputStream(newFile))

                if (newFile.exists()) {

                    fileUri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(
                            this@EditingScreen,
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
                    startActivity(
                        Intent.createChooser(
                            intentShareFile,
                            resources.getString(R.string.share_file)
                        )
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            document.close()
        } else {
            Utils.showToast(
                this,
                resources.getString(R.string.something_went_wrong)
            )
        }

    }

    @Suppress("DEPRECATION")
    fun saveMediaToStorage(bitmap: Bitmap): String? {

        var filePath: String? = null

        //Generating a file name
        val filename = "img_${System.currentTimeMillis()}.png"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            applicationContext?.contentResolver?.also { resolver ->

                val dirDest = File(Utils.getRootPath(this, false), "img")

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "$dirDest")
                }

                //MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                //Inserting the contentValues to contentResolver and getting the Uri
                // val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                val imageUri: Uri? = resolver.insert(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    contentValues
                )

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }

            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val dirDest = File(Utils.getRootPath(this, false), "img")
            if (!dirDest.exists()) {
                dirDest.mkdirs()
            }
            val image = File(dirDest, filename)

            Log.e("myFilePath", "$image")

            filePath = image.toString()

            fos = FileOutputStream(image)

        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
            it.close()

            Log.e("myFileFos", "Saved to Photos Main")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                filePath = saveImageInternalDirectory(bitmap, filename)
            }
        }

        return filePath
    }

    //This method only call Android 10 and above
    private fun saveImageInternalDirectory(bitmap: Bitmap, filename: String): String? {

        var filePath: String? = null

        //Output stream
        var fos: OutputStream? = null

        val dirDest = File(Utils.getRootPath(this, true), "img")
        if (!dirDest.exists()) {
            dirDest.mkdirs()
        }
        val image = File(dirDest, filename)

        try {
            fos = FileOutputStream(image)
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        }

        @Suppress("SENSELESS_COMPARISON")
        if (fos != null) {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            Log.e("myFileFos", "Saved to Photos Android 10 and above")

            filePath = image.toString()

        } else {
            Log.e("myFOS", "FOS is null")
        }

        return filePath
    }

    fun savePdfFileToScopeStorage(view: View): String? {

        var filePath: String? = null

        //Generating a file name
        val filename = "pdf_${System.currentTimeMillis()}.pdf"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            applicationContext?.contentResolver?.also { resolver ->

                val dirDest = File(Utils.getRootPath(this, false), "pdf")

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "$dirDest")
                }

                //MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                        contentValues
                    )

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }

            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val dirDest = File(Utils.getRootPath(this, false), "pdf")
            if (!dirDest.exists()) {
                dirDest.mkdirs()
            }
            val image = File(dirDest, filename)

            Log.e("myFilePath", "$image")

            filePath = image.toString()

            fos = FileOutputStream(image)

        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
            val page = document.startPage(pageInfo)

            view.draw(page.canvas)
            view.draw(page.canvas)
            // finish the page
            document.finishPage(page)

            document.writeTo(it)

            //This method save the Thumbnail pdf
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                filePath = savePdfThumbnailInternal(view, filename)
            }

        }

        return filePath

    }

    private fun savePdfThumbnailInternal(view: View, filename: String): String? {

        var filePath: String? = null

        //Output stream
        var fos: OutputStream? = null

        val dirDest = File(Utils.getRootPath(this, true), "pdf")
        if (!dirDest.exists()) {
            dirDest.mkdirs()
        }

        try {
            val file = File(dirDest, filename)

            Log.e("myFilePath", "$file")

            filePath = file.toString()

            try {
                fos = FileOutputStream(file)
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            }

        } catch (ex: java.lang.NullPointerException) {
            ex.printStackTrace()
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
            val page = document.startPage(pageInfo)

            view.draw(page.canvas)
            view.draw(page.canvas)
            // finish the page
            document.finishPage(page)

            document.writeTo(it)

        }

        return filePath

    }

    override fun onActivityResult(result: ActivityResult, currentRequestCode: Int) {

        if (currentRequestCode == Constant.REQUEST_GELLERY_IMAGE) {

            //hideFullScreenLayout()

            @Suppress("UNNECESSARY_SAFE_CALL")
            result?.data?.data?.also { uri ->

                try {

                    resizeImage(uri)

                    Log.e("myFilePicker", "$currentPhotoPath")

                    if (currentPhotoPath != null) {

                        if (File(currentPhotoPath!!).exists()) {
                            applyLocalSticker(currentPhotoPath!!)
                        } else {
                            Log.e("filePath", "path not found")
                            Utils.showToast(this, "Image is not Find")
                        }

                    } else {
                        Log.e("filePath", "path not found")
                        Utils.showToast(this, "Image is not Find")
                    }

                } catch (ex: java.lang.Exception) {
                    Log.e("myActivityResult", "${ex.message}")
                    Utils.showToast(this, getString(R.string.something_went_wrong))
                }

            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun applyLocalSticker(localPathStickerFile: String) {
        // hideFullScreenLayout()

        Utils.clearGarbageCollection()

        if (Utils.fileChecker(localPathStickerFile)) {

            newCustomSticker = CustomImageView(this)
            newCustomSticker?.updateCallBack(this@EditingScreen)

            newCustomSticker?.let {

                it.setImagePath(localPathStickerFile)

                rootLayout?.addView(it)
            }


        } else {
            Utils.showToast(this, getString(R.string.filter_not_found))
        }
    }

    private fun resizeImage(uri: Uri) {
        alterDocument(uri)
    }

    fun alterDocument(uri: Uri) {
        val image: Bitmap? = getBitmapFromContentResolver(uri)
        if (image != null) {
            saveImage(image)
        } else {
            Utils.showToast(this, "${R.string.something_went_wrong}")
        }
    }

    fun getBitmapFromContentResolver(shareUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val parcelFileDescriptor: ParcelFileDescriptor =
                contentResolver.openFileDescriptor(shareUri, "r")!!
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        } catch (ex: OutOfMemoryError) {
            ex.printStackTrace()
        }
        return bitmap
    }

    fun saveImage(image: Bitmap) {
        //var bitmap = image
        try {

            val file = createImageFile()
            Log.e("myFile", "$file")
            val outputStream = BufferedOutputStream(FileOutputStream(file))

            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            workerHandler.postDelayed({
                image.recycle()
                Utils.clearGarbageCollection()
            }, 1000)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = System.currentTimeMillis().toString()
        val storageDir = File("$externalCacheDir")
        return File.createTempFile(
            "PNG_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {

            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == Constant.REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {

                try {

                    if (data != null && data.extras != null) {

                        val imageBitmap = data.extras!!["data"] as Bitmap?
                        imageBitmap?.let { addImage(it) }

                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }

            if (requestCode == Constant.REQUEST_GELLERY_IMAGE && resultCode == Activity.RESULT_OK && null != data) {

                // hideFullScreenLayout()

                try {

                    val selectPath = data.data
                    val filePath = getRealPathFromURI(selectPath!!)

                    filePath?.let {
                        if (File(it).exists()) {

                            applyLocalSticker(it)
                        } else {
                            Log.e("filePath", "path not found")
                        }
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }

        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }

    }

    @Suppress("DEPRECATION")
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val filePath: String?
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            filePath = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(idx)
            cursor.close()
        }
        return filePath
    }

    private fun addImage(bitmap: Bitmap?) {

        try {

            newCustomSticker = CustomImageView(this)
            newCustomSticker?.updateCallBack(this@EditingScreen)

            newCustomSticker?.let {

                if (bitmap != null) {
                    it.setBitMap(bitmap)
                }

                rootLayout?.addView(it)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPermissionDenied() {
        Log.d("permissionWorking", "permission is Denied")
    }

    override fun onPermissionPermanentlyDenied() {
        showPrompt()
        Log.d("permissionWorking", "OnPermissionPermanentlyDenied Go to Setting")
    }

    override fun onPermissionGranted() {

        openGalleryNew()
        Log.d("permissionWorking", "Permission is Granted")

    }

    private fun showPrompt() {
        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this@EditingScreen)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage("Allow this app to access Photos and videos?")
        alertBuilder.setPositiveButton(
            getString(R.string.yes)
        ) { _, _ -> //hasPermissions(context,permissions);
            showSettings()
        }
        val alert = alertBuilder.create()
        alert.show()
    }

    private fun showSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun openGalleryNew() {

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        try {
            val intent =
                Intent(Intent.ACTION_PICK, collection).apply {
                    type = "image/*"
                }
            intent.resolveActivity(packageManager)?.also {
                initRequestCode(intent)
            }
        } catch (e: ActivityNotFoundException) {
            Utils.showToast(this, "${e.message}")
        }
    }

    var currentRequestCode = 0
    var currentPhotoPath: String? = null

    private fun initRequestCode(takePictureIntent: Intent) {
        currentRequestCode = Constant.REQUEST_GELLERY_IMAGE
        startActivityForResult.launch(takePictureIntent)
    }

    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                onActivityResultListener.onActivityResult(result, currentRequestCode)
            }
        }
}