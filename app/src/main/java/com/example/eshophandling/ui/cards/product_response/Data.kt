package com.example.eshophandling.ui.cards.product_response

data class Data(
        val id: Int,
        val image: String,
        var price: String,
        var quantity: String,
        var quantity_minus: String?="1",
        val sku: String,
        var status:Int
  )