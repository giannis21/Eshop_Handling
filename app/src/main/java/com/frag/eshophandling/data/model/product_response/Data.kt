package com.frag.eshophandling.data.model.product_response

import java.math.RoundingMode
import java.text.DecimalFormat

data class Data(
    val id: Int,
    val image: String,
    var price: String,
    var quantity: String,
    var quantity_minus: String? = "1",
    val sku: String,
    var status: Int
) {
    fun getCorrectPrice(): String {

        return if (price.contains(".")) {
            val commaIndex = price.indexOf(".")
            val length = price.length
            if (length - commaIndex > 3) {
                price.take(commaIndex + 2)
            } else
                price
        } else {
            price
        }
    }
}