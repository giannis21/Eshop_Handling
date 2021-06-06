    package com.example.eshophandling.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.utils.Preferences
import com.example.eshophandling.MainActivity
import com.example.eshophandling.R
import com.example.eshophandling.data.api.ApiClient
import com.example.eshophandling.data.api.ApiClientBasicAuth
import com.example.eshophandling.data.api.NetworkConnectionIncterceptor
import com.example.eshophandling.data.api.RemoteRepository
import com.example.eshophandling.ui.viewmodels.SharedViewModel
import com.example.eshophandling.utils.getDateInMilli
import com.example.eshophandling.utils.hideKeyboard
import com.example.eshophandling.utils.setSafeOnClickListener
import com.example.eshophandling.ui.viewmodels.ViewmodelFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.android.synthetic.main.scanner_fragment.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.util.*


class ScannerFragment : Fragment() {

    companion object {
        fun newInstance() = ScannerFragment()
    }

    private lateinit var viewModel: SharedViewModel
    private lateinit var viewModelFactory: ViewmodelFactory
    private var barcodeView: DecoratedBarcodeView? = null
    private var beepManager: BeepManager? = null
    private var lastText: String? = null
    private var PERMISSION_REQUEST_CODE=0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scanner_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // οταν ειναι ιδιο barcode δεν θελω να ξανα σκαναρω
                return
            }
            lastText = result.text
            barcodeView!!.setStatusText("")
            custom_barcode_view!!.visibility=View.VISIBLE
            custom_barcode_view!!.text= "Barcode: ${result.text}"

          //  beepManager!!.playBeepSoundAndVibrate()

        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(MainActivity.screenInches <=5.0)
           activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        com.example.alertlocation_kotlin.utils.Preferences.sharedPref = context?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        barcodeView = view.findViewById(R.id.barcode_scanner)
        val formats: Collection<BarcodeFormat> = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView!!.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView!!.initializeFromIntent(activity?.intent)
        barcodeView!!.decodeContinuous(callback)
        beepManager = BeepManager(activity)

        val networkConnectionIncterceptor = this.let { NetworkConnectionIncterceptor(requireContext()) }
        val apiClient = ApiClient(networkConnectionIncterceptor)
        val apiClientBasic = ApiClientBasicAuth(networkConnectionIncterceptor)
        val repository = RemoteRepository(apiClient, apiClientBasic)

        viewModelFactory = ViewmodelFactory(repository, requireContext())
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(SharedViewModel::class.java)

        if(MainActivity.screenInches <=5.0)
        KeyboardVisibilityEvent.setEventListener(((activity as? FragmentActivity)!!),
                object : KeyboardVisibilityEventListener {
                   override fun onVisibilityChanged(isOpen: Boolean) {
                        if(isOpen)
                            MainActivity.hideKeyboardListener?.invoke(true)
                        else
                            MainActivity.hideKeyboardListener?.invoke(false)

                        }
                })


        val diff: Long = Calendar.getInstance().timeInMillis - getDateInMilli(Preferences.lastLoginDate!!)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24


        manuallyText.setSafeOnClickListener {
            requireContext().showKeyboard(barcodeEdittext?.editText!!)
        }

        barcodeEdittext.editText?.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                barcode_line?.setBackgroundResource(R.drawable.ic_generic_line_black)
            }else{
                barcode_line?.setBackgroundResource(R.drawable.ic_generic_line_gray)
            }
        }

        manualOkay.setSafeOnClickListener {
            if(barcodeEdittext.editText?.text?.isNotEmpty()!!) {
                viewModel.getProduct(barcodeEdittext.editText?.text.toString())
                view.hideKeyboard()
            }
        }

        scanneImage.setSafeOnClickListener {
            if(checkPermission()){
                barcodeView!!.resume()
                barcodeView!!.setTorchOff()
                scrollview1.visibility=View.GONE
                cameraScanner.visibility=View.VISIBLE
                flash_light.visibility=View.VISIBLE
            }else{
                requestPermission()
            }
        }

        var flashOpened=false

        flash_light.setOnClickListener {
            if(!flashOpened){
                flashOpened=true
                barcodeView!!.setTorchOn()
                flash_light.setImageResource(R.drawable.turn_on_flash)
                flash_light.background = ContextCompat.getDrawable(requireContext(), R.drawable.bt_ui)
            }else{
                flashOpened=false
                barcodeView!!.setTorchOff()
                flash_light.setImageResource(R.drawable.turn_off_flash)
                flash_light.background = ContextCompat.getDrawable(requireContext(), R.drawable.bt_ui)
            }

        }
        exit_scan.setSafeOnClickListener {
            scrollview1.visibility=View.VISIBLE
            custom_barcode_view!!.visibility=View.GONE
            cameraScanner.visibility=View.GONE
            flash_light.visibility=View.GONE
            flash_light.setImageResource(R.drawable.turn_off_flash)
            flash_light.background = ContextCompat.getDrawable(requireContext(), R.drawable.bt_ui)
            barcodeView!!.pause()
        }

        add_product.setSafeOnClickListener {
             lastText?.let {
                 viewModel.getProduct(lastText)
             } ?:kotlin.run {
                 (activity as MainActivity?)?.showBanner("Σκανάρετε πρώτα κάποιο προϊόν!")
             }

        }

        viewModel.productRetrieved.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                (activity as MainActivity?)?.showBanner("Το προιόν προστέθηκε επιτυχώς στην λίστα!", true)
                barcodeEdittext?.editText!!.setText("")
            }
        })

        viewModel.productExists.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                (activity as MainActivity?)?.showBanner("Το προιόν υπάρχει ήδη στην λίστα!")
            }
        })

        viewModel.noInternetException.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Ουπς, κάτι πήγε λάθος! Πιθανόν πρόβλημα σύνδεσης.")
        })
        viewModel.error.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Ουπς, κάτι πήγε λάθος!")
        })

        viewModel.productNotFound.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Ουπς, το προϊόν δεν βρέθηκε!")
        })



    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.productRetrieved.postValue(false)
        viewModel.noInternetException.postValue(false)
        viewModel.error.postValue(false)
        viewModel.productExists.postValue(false)
        viewModel.productNotFound.postValue(false)
    }

    override fun onResume() {
        super.onResume()
        barcodeView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView!!.pause()
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }



    private fun Context.showKeyboard(editText: EditText) {
        val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
                editText.applicationWindowToken,
                InputMethodManager.SHOW_IMPLICIT,
                0
        )
        editText.requestFocus()
        editText.setSelection(editText.text.length)
    }



}