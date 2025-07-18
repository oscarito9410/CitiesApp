package com.oscarp.citiesapp.domain.extensions

import kotlin.math.pow

fun Double.format(digits: Int): String =
    buildString {
        append(kotlin.math.round(this@format * 10.0.pow(digits)) / 10.0.pow(digits))
    }
