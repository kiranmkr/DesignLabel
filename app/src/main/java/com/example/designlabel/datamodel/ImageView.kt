package com.example.designlabel.datamodel

import com.fasterxml.jackson.annotation.JsonProperty

class ImageView {
    @JsonProperty("_android:id")
    var androidId: String? = null

    @JsonProperty("_android:layout_width")
    var androidLayoutWidth: String? = null

    @JsonProperty("_android:layout_height")
    var androidLayoutHeight: String? = null

    @JsonProperty("_app:srcCompat")
    var appSrcCompat: String? = null

    @JsonProperty("_android:layout_x")
    var androidLayoutX: String? = null

    @JsonProperty("_android:layout_y")
    var androidLayoutY: String? = null

    @JsonProperty("_android:rotation")
    var androidRotation: String? = null

    @JsonProperty("_android:background")
    var androidBackground: String? = null

    @JsonProperty("_android:alpha")
    var androidAlpha: String? = null
}