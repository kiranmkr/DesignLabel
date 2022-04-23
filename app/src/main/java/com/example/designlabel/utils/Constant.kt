package com.example.designlabel.utils

import com.example.designlabel.R

object Constant {

    var categoryName: String = "candy"
    var labelNumber: Int = 1

    @JvmStatic
    val categoryMap: HashMap<String, Int> =
        hashMapOf(
            "Soap" to 10,
            "New Year" to 10,
            "Shipping" to 10,
            "Gaming" to 10,
            "Cosmetic" to 10,
            "Baby Shower" to 10,
            "Candy" to 10,
            "Cars" to 10,
            "Holidays" to 10,
            "Product" to 10,
            "Laptop" to 10
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