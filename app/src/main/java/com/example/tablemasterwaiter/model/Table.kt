package com.example.tablemasterwaiter.model

data class Table(
    val Id: Int,
    val Bounds: String,
    val PositionData: PositionData,
    val Rotation: Float,
    val SizeData: SizeData
)

data class PositionData(
    val X: Int,
    val Y: Int
)

data class SizeData(
    val Width: Int,
    val Height: Int
)
