package com.example.designlabel.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.designlabel.R
import com.example.designlabel.adapter.SVGLayersAdapter
import com.example.designlabel.datamodel.ImageView
import com.example.designlabel.datamodel.Root
import com.example.designlabel.datamodel.TextView
import com.example.designlabel.utils.Utils
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class EditingScreen : AppCompatActivity(), SVGLayersAdapter.SvgLayersClick {

    private val workerThread: ExecutorService = Executors.newCachedThreadPool()
    private val workerHandler = Handler(Looper.getMainLooper())

    private var categoryName: String = "candy"
    private var labelNumber: Int = 3

    var imageViewArray: ArrayList<ImageView> = ArrayList()
    var textViewJson: ArrayList<TextView> = ArrayList()
    var svgLayerPathList: ArrayList<String> = ArrayList()
    var rootLayout: RelativeLayout? = null
    var screenRatioFactor: Double = 1.0
    var screenWidth: Double = 720.0

    var imageViewSVG: android.widget.ImageView? = null
    var currentLayer: android.widget.ImageView? = null
    private var svgSeekAlpha: SeekBar? = null

    private var svgRecyclerView: RecyclerView? = null

    private var svgColorPickerImage: AppCompatImageView? = null
    private var svgColorPrimaryImage: AppCompatImageView? = null
    private var svgColorBlackImage: AppCompatImageView? = null
    private var svgLayersAdapter: SVGLayersAdapter? = null
    private var layerSelection: ConstraintLayout? = null

    private var mColor = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing_screen)

        workerHandler.post {
            initID()
        }

    }

    private fun initID() {

        rootLayout = findViewById(R.id.root_layout)
        imageViewSVG = findViewById(R.id.imageView52)
        svgSeekAlpha = findViewById(R.id.seekBarSvg)
        svgColorPickerImage = findViewById(R.id.imageView14)
        svgColorPrimaryImage = findViewById(R.id.imageView11)
        svgColorBlackImage = findViewById(R.id.imageView13)

        svgRecyclerView = findViewById(R.id.recyclerView2)
        svgRecyclerView?.setHasFixedSize(true)

        layerSelection = findViewById(R.id.layer)
        layerSelection?.isSelected = true

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

                    Log.d("myFactor", "$screenRatioFactor")

                    if (imageViewArray.size > 0) {
                        addImage(imageViewArray)
                    }

                    if (textViewJson.size > 0) {
                        addText(textViewJson)
                    }

                } else {
                    Log.d("myError", "wrong json")
                }

            }
        })

        clickHandler()

    }

    private fun clickHandler() {

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

        svgColorPickerImage?.setOnClickListener {

            ColorPickerDialog
                .Builder(this@EditingScreen) // Pass Activity Instance
                .setColorShape(ColorShape.SQAURE) // Or ColorShape.CIRCLE
                .setDefaultColor(mColor) // Pass Default Color
                .setColorListener { color, _ ->
                    mColor = color
                    Log.d("myColor", "$mColor")
                    if (currentLayer != null) {
                        currentLayer?.setColorFilter(mColor)
                    } else {
                        Utils.showToast(this, "Plz select path")
                    }
                }
                .setDismissListener {
                    Log.d("ColorPickerDialog", "Handle dismiss event")
                }
                .show()
        }

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
                        "file:///android_asset/category/$categoryName/assets/$labelNumber/$index.png"
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
                        "file:///android_asset/category/$categoryName/assets/$labelNumber/0.png"
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
                val parms = RelativeLayout.LayoutParams(width.toInt(), height.toInt())
                newTextView?.layoutParams = parms

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

                val resources: Resources = resources
                val resourceId: Int = resources.getIdentifier(
                    textView.androidFontFamily.toString().replace("@font/", ""),
                    "font",
                    packageName
                )

                val typeface = ResourcesCompat.getFont(this@EditingScreen, resourceId)
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

            rootLayout?.addView(newTextView)
        }

    }

    private fun loadJSONFromAsset(): String? {

        val json: String? = try {
            val `is`: InputStream =
                this.assets.open("category/$categoryName/json/$labelNumber.json")
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
}