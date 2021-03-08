package com.example.eshophandling.ui.cards


interface ItemHandler{
        fun onSubmit(position: Int, id: Int, quantity: String, price: String, isEnabled1: Boolean)
        fun onDelete(id: Int)
        fun onRefresh(position: Int, id: Int, quantity: String, price: String, isEnabled1: Boolean)
        fun onRefreshQuantity(position: Int, id: Int, quantity:String )
    }