package com.frag.eshophandling.data.model.submit_product

data class SubmittedProduct(
        val id: Int,
        var price: Float,
        var quantity: Int,
        val sku: String,
        var status:Int,
        var user_name:String,
        var date_modified: String
)