package com.example.eshophandling.ui.cards.product_response

data class Data(
        val id: Int,
        val image: String,
        var price: Int,
        var quantity: Int,
        val sku: String,
        var status:Int
  )