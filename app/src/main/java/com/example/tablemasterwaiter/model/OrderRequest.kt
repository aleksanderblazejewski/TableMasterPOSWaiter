package com.example.tablemasterwaiter.model

data class OrderRequest(
    val tableId: Int,
    val items: List<OrderItem>
)