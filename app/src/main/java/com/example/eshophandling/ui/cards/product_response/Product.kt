package com.example.eshophandling.ui.cards.product_response

data class Product(
    val `data`: Data,
    val error: List<Any>,
    val success: Int
)