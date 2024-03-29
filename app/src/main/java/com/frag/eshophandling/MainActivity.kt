package com.frag.eshophandling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.frag.alertlocation_kotlin.utils.Preferences
import com.frag.alertlocation_kotlin.utils.Preferences.token
import com.frag.eshophandling.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.banner_layout.view.*


class MainActivity : AppCompatActivity() {

    companion object{
        var errorListener: ((Int) ->Unit) ?=null
        var hideKeyboardListener: ((Boolean) ->Unit)? = null
        var screenInches:Double=0.0
    }

    private var mainActivityAlive:Boolean=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main2)
        Preferences.sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        val dm = resources.displayMetrics

        val density = dm.density * 160.toDouble()
        val x = Math.pow(dm.widthPixels / density, 2.0)
        val y = Math.pow(dm.heightPixels / density, 2.0)
        screenInches = Math.sqrt(x + y)

        setStatusBarColor()
        scannerIcon.setOnClickListener {
                tvTitle.text = "Scanner"
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_scannerFragment)
        }
        btHome.setOnClickListener {
            tvTitle.text = "Κάρτα προϊόντων"
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_cardsFragment)
        }
        settingsIcon.setOnClickListener {
            tvTitle.text = "Ρυθμίσεις"
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_settingsFragment)
        }
        hideKeyboardListener = {
            if(it){
                footer.visibility=View.GONE
                scannerIcon.visibility=View.GONE
                settingsIcon.visibility=View.GONE
                btHome.visibility=View.GONE
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    footer.visibility = View.VISIBLE
                    scannerIcon.visibility = View.VISIBLE
                    settingsIcon.visibility = View.VISIBLE
                    btHome.visibility = View.VISIBLE
                }, 100)

            }

        }
        errorListener = { error ->

            when (error) {
                401,403 -> {
                    if(mainActivityAlive)
                    {
                        showBanner("Ουπς, υπάρχει κάποιο πρόβλημα!")
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 4000)
                        token=""
                    }

                }
                500 -> {
                    showBanner("server error!")
                }
                else -> {}
            }
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivityAlive=false
    }
    private fun setStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.logo_color)
    }

    fun showBanner(value: String, success: Boolean = false) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

        runOnUiThread {
            frameLayout_main?.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
                cLayout.redBannerTxtV.text = value

                if(!success){
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.LightRed)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)

                }else{
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.success_color)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_check_24)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cLayout.removeView(view)
                }, 4000)
            }
        }
    }
}