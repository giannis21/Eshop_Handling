 package com.example.eshophandling.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.utils.Preferences.BaseUrl
import com.example.alertlocation_kotlin.utils.Preferences.lastLoginDate
import com.example.alertlocation_kotlin.utils.Preferences.token
import com.example.eshophandling.MainActivity
import com.example.eshophandling.R
import com.example.eshophandling.api.ApiClient
import com.example.eshophandling.api.ApiClientBasicAuth
import com.example.eshophandling.api.RemoteRepository
import com.example.eshophandling.api.NetworkConnectionIncterceptor
import com.example.eshophandling.utils.getDateInMilli
import com.example.eshophandling.utils.hideKeyboard
import com.example.tvshows.ui.nowplaying.ViewmodelFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.banner_layout.view.*
import java.util.*


 class LoginActivity : AppCompatActivity() {
     private lateinit var viewModelFactory: ViewmodelFactory
     private lateinit var viewModel: LoginViewModel
     private var fieldsFilled=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setStatusBarColor()
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        com.example.alertlocation_kotlin.utils.Preferences.sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        if(token?.isNotEmpty()!!){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            group.visibility=View.VISIBLE
        }

        val networkConnectionIncterceptor = this.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val apiClient = ApiClient(networkConnectionIncterceptor!!)
        val apiClientBasic = ApiClientBasicAuth(networkConnectionIncterceptor)
        val repository = RemoteRepository(apiClient,apiClientBasic)

        viewModelFactory = ViewmodelFactory(repository, this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)


        submit_btn.setOnClickListener {
            if(!fieldsFilled){
                showBanner("Πρέπει να συμπληρώσεις όλα τα πεδία!")
                return@setOnClickListener
            }

            window.decorView.rootView.hideKeyboard()
            viewModel.getCredentials(username_container.editText?.text.toString().trim(),password_container.editText?.text.toString().trim(),url_container.editText?.text.toString().trim())
        }

        addFocusListeners()

        url_container.editText?.addTextChangedListener(loginTextWatcher)
        password_container.editText?.addTextChangedListener(loginTextWatcher)
        username_container.editText?.addTextChangedListener(loginTextWatcher)

        observeViewModel()
    }

     private fun addFocusListeners() {
         username_container.editText?.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
             if (hasFocus) {
                 username_line?.setBackgroundResource(R.drawable.ic_generic_line_black)
             }else{
                 username_line?.setBackgroundResource(R.drawable.ic_generic_line_gray)
             }
         }

         password_container.editText?.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
             if (hasFocus) {
                 password_line?.setBackgroundResource(R.drawable.ic_generic_line_black)
             }else{
                 password_line?.setBackgroundResource(R.drawable.ic_generic_line_gray)
             }
         }

         url_container.editText?.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
             if (hasFocus) {
                 url_line?.setBackgroundResource(R.drawable.ic_generic_line_black)
             }else{
                 url_line?.setBackgroundResource(R.drawable.ic_generic_line_gray)
             }
         }
     }

     private fun observeViewModel() {
         viewModel.LoggedIn.observe(this, Observer {
             if(it){
                 lastLoginDate = Calendar.getInstance().timeInMillis.toString()
                 BaseUrl = viewModel.validUrl.value
                 startActivity(Intent(this, MainActivity::class.java))
             }
         })

         viewModel.loading.observe(this, Observer {
             if(it){
                 spin_kit.visibility=View.VISIBLE
                 submit_btn.text = ""
             }else{
                 submit_btn.text = "Login"
                 spin_kit.visibility=View.GONE
             }
         })

         viewModel.noInternetException.observe(this, Observer {
             if (it)
               showBanner("Δεν υπάρχει σύνδεση στο internet!")
         })

         viewModel.error?.observe(this, Observer {
             showBanner("Ουπς, κάτι πήγε λάθος!")
         })
     }

     private val loginTextWatcher: TextWatcher = object : TextWatcher {
         override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

         override fun afterTextChanged(s: Editable) {
             val url_container = url_container?.editText?.text.toString().trim()
             val password_container: String = password_container.editText?.getText().toString().trim()
             val username_container: String = username_container.editText?.getText().toString().trim()

             fieldsFilled = url_container.isNotEmpty() && password_container.isNotEmpty() && username_container.isNotEmpty()
          }
     }

     fun showBanner(value: String) {
         val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

         runOnUiThread {
             frameLayout?.let { cLayout ->
                 cLayout.addView(view, 0)
                 cLayout.bringToFront()

                 cLayout.redBannerTxtV.text = value
                 cLayout.BannerConstraint.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark);

                 cLayout.imageView.setBackgroundResource(R.drawable.ic_baseline_close_24)
                 Handler(Looper.getMainLooper()).postDelayed({
                     cLayout.removeView(view)
                 }, 3000)
             }
         }
     }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.logo_color)
    }
}