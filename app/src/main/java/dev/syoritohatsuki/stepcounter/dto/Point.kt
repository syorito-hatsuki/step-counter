package dev.syoritohatsuki.stepcounter.dto

import kotlin.math.sqrt

data class Point(val x: Float, val y: Float, val z: Float) {
    val magnitude = sqrt(x * x + y * y + z * z)
}