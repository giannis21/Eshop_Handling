package com.example.eshophandling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.eshophandling.ui.login.LoginActivity
import io.reactivex.internal.operators.maybe.MaybeDoAfterSuccess
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.banner_layout.view.*

class MainActivity : AppCompatActivity() {

    companion object{
        var errorListener: ((Int) ->Unit) ?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
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

        errorListener = { error ->
            when (error) {
                401 -> {
                    val intent = Intent (this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                500 -> {
                    showBanner("server error!")
                }
                else -> {}
            }
        }
    }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.logo_color)
    }

    fun showBanner(value: String,success: Boolean = false) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

        runOnUiThread {
            frameLayout_main?.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
                cLayout.redBannerTxtV.text = value

                if(!success){
                    cLayout.BannerConstraint.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
                    cLayout.imageView.setBackgroundResource(R.drawable.ic_baseline_close_24)
                }else{
                    cLayout.BannerConstraint.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
                    cLayout.imageView.setBackgroundResource(R.drawable.ic_baseline_check_24)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cLayout.removeView(view)
                }, 3000)
            }
        }
    }
}