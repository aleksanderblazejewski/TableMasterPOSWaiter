package com.example.tablemasterwaiter.model

data class TableGroup(
    val Id: Int,
    val Name: String,
    val AssignedWaiterIds: List<Int>,
    val AssignedTableIds: List<Int>
)