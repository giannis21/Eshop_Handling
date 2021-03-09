package com.example.eshophandling.ui.cards.submit_products

data class Data1(
        val id: Int,
        var price: Float,
        var quantity: String,
        var quantity_minus: String?="1",
        val sku: String,
        var status:Int
)