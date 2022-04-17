package com.example.designlabel.datamodel

import com.fasterxml.jackson.annotation.JsonProperty

class TextView {
    @JsonProperty("_android:id")
    var androidId: String? = null

    @JsonProperty("_android:layout_width")
    var androidLayoutWidth: String? = null

    @JsonProperty("_android:layout_height")
    var androidLayoutHeight: String? = null

    @JsonProperty("_android:layout_x")
    var androidLayoutX: String? = null

    @JsonProperty("_android:layout_y")
    var androidLayoutY: String? = null

    @JsonProperty("_android:background")
    var androidBackground: String? = null

    @JsonProperty("_android:letterSpacing")
    var androidLetterSpacing: String? = null

    @JsonProperty("_android:rotation")
    var androidRotation: String? = null

    @JsonProperty("_android:text")
    var androidText: String? = null

    @JsonProperty("_android:textColor")
    var androidTextColor: String? = null

    @JsonProperty("_android:textSize")
    var androidTextSize: String? = null

    @JsonProperty("_android:textAlignment")
    var androidTextAlignment: String? = null

    @JsonProperty("_android:alpha")
    var androidAlpha: String? = null

    @JsonProperty("_android:fontFamily")
    var androidFontFamily: String? = null

    @JsonProperty("_android:textStyle")
    var androidTextStyle: String? = null
}