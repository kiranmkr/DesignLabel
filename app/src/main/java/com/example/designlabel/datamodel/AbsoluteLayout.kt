package com.example.designlabel.datamodel

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.ArrayList

class AbsoluteLayout {
    @JsonProperty("ImageView")
    var imageView: ArrayList<ImageView>? = null

    @JsonProperty("TextView")
    var textView: ArrayList<TextView>? = null

    @JsonProperty("_xmlns:android")
    var xmlnsAndroid: String? = null

    @JsonProperty("_xmlns:app")
    var xmlnsApp: String? = null

    @JsonProperty("_android:layout_width")
    var androidLayoutWidth: String? = null

    @JsonProperty("_android:layout_height")
    var androidLayoutHeight: String? = null

    @JsonProperty("_android:background")
    var androidBackground: String? = null
}