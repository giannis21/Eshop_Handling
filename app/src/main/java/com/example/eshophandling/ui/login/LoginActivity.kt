 package com.example.eshophandling.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.utils.Preferences
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
import com.example.eshophandling.utils.milliToDate
import com.example.tvshows.ui.nowplaying.ViewmodelFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.banner_layout.view.*
import java.util.*


 class LoginActivity : AppCompatActivity() {
     private lateinit var viewModelFactory: ViewmodelFactory
     private lateinit var viewModel: LoginViewModel
     private var fieldsFilled=false
     private var isPasswordVisible=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setStatusBarColor()
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        Preferences.sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        val diff: Long = Calendar.getInstance().timeInMillis - getDateInMilli(lastLoginDate!!)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        println("days--- $days")


        if(token?.isNotEmpty()!! && days <= 20){
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
            viewModel.getCredentials(username_container.editText?.text.toString().trim(),password_container.editText?.text.toString().trim(),getAbsoluteUrl(url_container.editText?.text.toString().trim()))
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

         password_container.setEndIconOnClickListener {
             isPasswordVisible = !isPasswordVisible
             if (isPasswordVisible) {
                 password_container?.editText?.transformationMethod = null
                 password_container?.editText?.text?.let {
                     password_container?.editText?.setSelection(it.length)
                 }
             } else {
                 password_container?.editText?.transformationMethod = PasswordTransformationMethod()
                 password_container?.editText?.text?.let {
                     password_container?.editText?.setSelection(it.length)
                 }
             }
         }

         textViewHttp.setOnClickListener {
             if(textViewHttp.text.toString() == "https://"){
                 textViewHttp.text ="http://"
             }else{
                 textViewHttp.text ="https://"
             }
         }
     }
     fun getAbsoluteUrl(baseUrl: String) : String {

         var temp=baseUrl.removeSuffix("/")
         if(temp.contains("https")!!){
             temp=temp.removePrefix("https://")
         }else if(temp.contains("http")){
             temp=temp.removePrefix("http://")
         }
         temp=textViewHttp.text.toString()+temp+"/"

         BaseUrl = temp
         return temp +"api/rest_admin/oauth2/token/client_credentials"
     }
     private fun observeViewModel() {
         viewModel.LoggedIn.observe(this, Observer {
             if(it){
                 lastLoginDate = milliToDate(Calendar.getInstance().timeInMillis.toString())

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
         viewModel.unknownHostException.observe(this, Observer {
             if(it)
             showBanner("Ουπς, κάτι πήγε λάθος με το url!")
         })

     }

     private val loginTextWatcher: TextWatcher = object : TextWatcher {
         override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

         override fun afterTextChanged(s: Editable) {
             val url_container1 = url_container?.editText?.text.toString().trim()
             val password_container1: String = password_container.editText?.getText().toString().trim()
             val username_container1: String = username_container.editText?.getText().toString().trim()

             fieldsFilled = url_container1.isNotEmpty() && password_container1.isNotEmpty() && username_container1.isNotEmpty()

             if(password_container1.isEmpty()){
                 password_container.isEndIconVisible = false
                 password_container.setEndIconActivated(false)
              }else {
                 password_container.isEndIconVisible = true
                 password_container.setEndIconActivated(true)
                 if (isPasswordVisible) {
                     password_container.setEndIconDrawable(R.drawable.ic_eye_show)
                 } else {
                     password_container.setEndIconDrawable(R.drawable.ic_eye_hide)
                 }
             }
         }
     }



     fun showBanner(value: String) {
         val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

         runOnUiThread {
             frameLayout?.let { cLayout ->
                 cLayout.addView(view, 0)
                 cLayout.bringToFront()

                 cLayout.redBannerTxtV.text = value
                 cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.LightRed);

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