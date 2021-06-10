package com.frag.eshophandling.data.model.product_response

data class ProductDescription(
    val description: String,
    val language_id: String,
    val meta_description: String,
    val meta_keyword: String,
    val meta_title: String,
    val name: String,
    val tag: String
)