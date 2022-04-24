package com.example.designlabel.utils

import com.example.designlabel.R
import java.util.HashMap

object Constant {

    const val REQUEST_CAPTURE_IMAGE = 10101
    const val REQUEST_GELLERY_IMAGE = 20202

    @JvmStatic
    val fileProvider: String = "com.example.designlabel.fileprovider"

    var categoryName: String = "candy"
    var labelNumber: Int = 1

    var categoryPosition: Int = 0

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
        "shipment",
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
            "shipment" to 10,
            "shape" to 10
        )


    @JvmStatic
    var colorArray = arrayOf(
        R.color.black,
        R.color.white,
        R.color.red_100,
        R.color.red_300, R.color.red_500, R.color.red_700, R.color.blue_100,
        R.color.blue_300, R.color.blue_500, R.color.blue_700, R.color.green_100, R.color.green_300,
        R.color.green_500, R.color.green_700
    )

    var colorPro = arrayOf(
        R.color.colorPrimaryDark,
        R.color.lightGray
    )
}