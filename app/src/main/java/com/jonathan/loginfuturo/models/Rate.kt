package com.jonathan.loginfuturo.models

import java.util.*

data class Rate(val text : String = "",
                val rate : Float = 0.0f,
                val createAt : Date = Date(),
                val profileImageUrl : String = "")