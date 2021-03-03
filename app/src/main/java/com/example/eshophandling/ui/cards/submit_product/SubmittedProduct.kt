package com.example.eshophandling.ui.cards.submit_product

data class SubmittedProduct(
        val id: Int,
        var price: Int,
        var quantity: Int,
        val sku: String,
        var status:Int,
        var user_name:String,
        var date_moified: String
)