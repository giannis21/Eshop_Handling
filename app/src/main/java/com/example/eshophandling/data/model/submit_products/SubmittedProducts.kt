package com.example.eshophandling.data.model.submit_products

import com.google.gson.annotations.SerializedName


data class SubmittedProducts(
        @SerializedName("products")
        val `data`: MutableList<Data1>,
        val username: String,
        val date: String,
        val phonemodel: String
)

