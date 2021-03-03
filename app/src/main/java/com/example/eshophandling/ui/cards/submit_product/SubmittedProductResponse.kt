package com.example.eshophandling.ui.cards.submit_product

data class SubmittedProductResponse(
    val `data`: List<Any>,
    val error: List<String>,
    val success: Int
)