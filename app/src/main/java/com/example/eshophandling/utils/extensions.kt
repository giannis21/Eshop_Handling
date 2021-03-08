package com.example.eshophandling.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(private var defaultInterval: Int = 1000, private val onSafeCLick: (View) -> Unit) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

@SuppressLint("SimpleDateFormat")
fun getDateInMilli(date: String): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

    return try {
        val mDate: Date = sdf.parse(date)
        mDate.time
    } catch (e: ParseException) {
        0
    }
}

 fun milliToDate(milliseconds: String): String{
     // Create a DateFormatter object for displaying date in specified format.

     // Create a DateFormatter object for displaying date in specified format.
     val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")

     // Create a calendar object that will convert the date and time value in milliseconds to date.

     // Create a calendar object that will convert the date and time value in milliseconds to date.
     val calendar = Calendar.getInstance()
     calendar.timeInMillis = milliseconds.toLong()
     return formatter.format(calendar.time)
 }

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}