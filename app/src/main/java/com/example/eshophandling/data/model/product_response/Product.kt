package com.example.eshophandling.data.model.product_response

data class Product(
        val `data`: Data,
        val error: List<Any>,
        val success: Int
)