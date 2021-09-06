package com.frag.eshophandling.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.frag.alertlocation_kotlin.utils.Preferences.token
import com.frag.eshophandling.MainActivity
import com.frag.eshophandling.MyApplication
import com.frag.eshophandling.R
import com.frag.eshophandling.data.api.ApiClient
import com.frag.eshophandling.data.api.NetworkConnectionIncterceptor
import com.frag.eshophandling.data.api.NoInternetException
 import com.frag.eshophandling.ui.login.LoginActivity
import com.frag.eshophandling.ui.viewmodels.SharedViewModel
import com.frag.eshophandling.utils.Loading_dialog
import com.frag.eshophandling.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//typealias MainComponentProvider = Provider<MainComponent>
class SettingsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: SharedViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        com.frag.alertlocation_kotlin.utils.Preferences.sharedPref = requireContext().getSharedPreferences(
            "sharedPref",
            Context.MODE_PRIVATE
        )


        developer.paintFlags = developer.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        webDev.paintFlags = developer.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        developer.setOnClickListener {
             openToBrowser("https://www.linkedin.com/in/giannis-fragoulis-355877177/")
        }
        webDev.setOnClickListener {
             openToBrowser("https://www.linkedin.com/in/adrian-mold-a1b73b167/?fbclid=IwAR2j1QpiktAxcFXr9YsbulpaCzLKS3dQgvUksbSNzpZZcl7UmFqjcnPzLbE")
        }

        logout.setSafeOnClickListener {
//            val dialog = Loading_dialog(requireContext())
//            dialog.displayLoadingDialog()
//
//            lifecycleScope.launch(Dispatchers.Default) {
//                runCatching {
//                    apiClient.logout()
//                }.onFailure {
//                    if(it is NoInternetException){
//                        (activity as MainActivity?)?.showBanner("Δεν υπάρχει σύνδεση στο internet!")
//                    }
//
//                    dialog.hideLoadingDialog()
//                }.onSuccess {
//                    dialog.hideLoadingDialog()
//
//                    if (it.isSuccessful) {
//                         if (it.body()!!.success == 1) {
//                             token = ""
//                             val intent = Intent(activity, LoginActivity::class.java)
//                             activity?.startActivity(intent)
//                             activity?.finish()
//                          }
//                    } else {
//                          (activity as MainActivity?)?.showBanner("Ουπς, κάτι πήγε λάθος!")
//                    }
//
//                }
//
//            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as MyApplication).appComponent.inject(this)
        // (context as MainComponentProvider).get().inject(this)
    }

    private fun openToBrowser(newUrl: String) {
        try{
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(newUrl)
            view?.context?.startActivity(i)
        }catch (e: Exception){}
    }
}