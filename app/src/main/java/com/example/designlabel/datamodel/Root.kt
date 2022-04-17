package com.example.designlabel.datamodel

import com.fasterxml.jackson.annotation.JsonProperty

class Root {
    @JsonProperty("AbsoluteLayout")
    var absoluteLayout: AbsoluteLayout? = null
}