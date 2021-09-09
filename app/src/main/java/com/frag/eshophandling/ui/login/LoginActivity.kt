package com.frag.eshophandling.ui.login

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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.frag.eshophandling.MainActivity
import com.frag.eshophandling.MyApplication
import com.frag.eshophandling.R
import com.frag.eshophandling.ui.viewmodels.LoginViewModel
import com.frag.eshophandling.utils.getDateInMilli
import com.frag.eshophandling.utils.hideKeyboard
import com.frag.eshophandling.utils.milliToDate
import com.frag.eshophandling.utils.Datastore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.banner_layout.view.*
import java.util.*
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var datastore: Datastore

    val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }
  //  lateinit var loginComponent: LoginComponent

    private var fieldsFilled = false
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val component= (application as MyApplication).appComponent.loginComponent().create()
        component.inject(this)
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_login)
        setStatusBarColor()

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val diff: Long = Calendar.getInstance().timeInMillis - getDateInMilli(datastore.getLastLoginDate())
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24


        if (datastore.getToken().isNotEmpty() && days <= 20) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            group.visibility = View.VISIBLE
        }

        username_container.editText?.setText("admin")
        password_container.editText?.setText("qwe123!")
        url_container?.editText?.setText("www.Adrian-mol.com")

        submit_btn.setOnClickListener {
//            if (!fieldsFilled) {
//                showBanner("Πρέπει να συμπληρώσεις όλα τα πεδία!")
//                return@setOnClickListener
//            }

            window.decorView.rootView.hideKeyboard()
            viewModel.getCredentials(username_container.editText?.text.toString().trim(), password_container.editText?.text.toString().trim(), getAbsoluteUrl(url_container.editText?.text.toString().trim()))
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
            } else {
                username_line?.setBackgroundResource(R.drawable.ic_generic_line_gray)
            }
        }

        password_container.editText?.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                password_line?.setBackgroundResource(R.drawable.ic_generic_line_black)
            } else {
                password_line?.setBackgroundResource(R.drawable.ic_generic_line_gray)
            }
        }

        url_container.editText?.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                url_line?.setBackgroundResource(R.drawable.ic_generic_line_black)
            } else {
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
            if (textViewHttp.text.toString() == "https://") {
                textViewHttp.text = "http://"
            } else {
                textViewHttp.text = "https://"
            }
        }
    }

    fun getAbsoluteUrl(baseUrl: String): String {

        var temp = baseUrl.removeSuffix("/")
        if (temp.contains("https")) {
            temp = temp.removePrefix("https://")
        } else if (temp.contains("http")) {
            temp = temp.removePrefix("http://")
        }
        temp = textViewHttp.text.toString() + temp + "/"
        datastore.setBaseUrl(temp)
        return temp + "api/rest_admin/oauth2/token/client_credentials"
    }

    private fun observeViewModel() {

        viewModel.LoggedIn.observe(this, Observer {
            if (it) {
                viewModel.LoggedIn.postValue(false)
                datastore.setLastLoginDate(milliToDate(Calendar.getInstance().timeInMillis.toString()))
                startActivity(Intent(this, MainActivity::class.java))
            }
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                spin_kit.visibility = View.VISIBLE
                submit_btn.text = ""
            } else {
                submit_btn.text = getString(R.string.login)
                spin_kit.visibility = View.GONE
            }
        })

        viewModel.noInternetException.observe(this, Observer {
            if (it)
                showBanner("Δεν υπάρχει σύνδεση στο internet!")
        })

        viewModel.error.observe(this, Observer {
            if (it)
                showBanner("Ουπς, κάτι πήγε λάθος!")
        })
        viewModel.invalid_creds.observe(this, Observer {
            if (it)
                showBanner("Ουπς, κάτι πήγε λάθος! Έλεγξε τα στοιχεία που έδωσες!")
        })

        viewModel.unknownHostException.observe(this, Observer {
            if (it)
                showBanner("Ουπς, κάτι πήγε λάθος με το url!")
        })

    }

    private val loginTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            val url_container1 = url_container?.editText?.text.toString().trim()
            val password_container1: String = password_container.editText?.text.toString().trim()
            val username_container1: String = username_container.editText?.text.toString().trim()

            fieldsFilled = url_container1.isNotEmpty() && password_container1.isNotEmpty() && username_container1.isNotEmpty()

            if (password_container1.isEmpty()) {
                password_container.isEndIconVisible = false
                password_container.setEndIconActivated(false)
            } else {
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
                }, 4000)
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