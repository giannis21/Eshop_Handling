package com.frag.eshophandling.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.frag.eshophandling.R


class Loading_dialog(var context: Context) {

    lateinit var alertDialog1: AlertDialog

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    val layoutInflaterAndroid = LayoutInflater.from(context)
    val view2: View = layoutInflaterAndroid.inflate(R.layout.loading_dialog, null, false)


    fun displayLoadingDialog() {
//        if (view2.parent != null) {
//            (view2.parent as ViewGroup).removeView(view2)
//        }
//
//        builder.setView(view2)
//        alertDialog1 = builder.create()
//        alertDialog1.show()

    }

    fun hideLoadingDialog() {
      // alertDialog1.dismiss()
    }
}