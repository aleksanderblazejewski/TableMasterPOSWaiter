package com.example.tablemasterwaiter.model

data class Order(
val Id: Int? = null,
val IsPaid: Boolean,
val Items: List<OrderItem>,
val TableId: Int
)
