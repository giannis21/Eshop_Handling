package com.example.eshophandling

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.utils.Preferences
import com.example.eshophandling.api.ApiClient
import com.example.eshophandling.api.ApiClientBasicAuth
import com.example.eshophandling.api.NetworkConnectionIncterceptor
import com.example.eshophandling.api.RemoteRepository
import com.example.eshophandling.utils.getDateInMilli
import com.example.eshophandling.utils.hideKeyboard
import com.example.eshophandling.utils.setSafeOnClickListener
import com.example.tvshows.ui.nowplaying.ViewmodelFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.android.synthetic.main.scanner_fragment.*
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
            barcodeView!!.setStatusText("Barcode: ${result.text}")
          //  beepManager!!.playBeepSoundAndVibrate()

        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeView = view.findViewById(R.id.barcode_scanner)
        val formats: Collection<BarcodeFormat> = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView!!.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView!!.initializeFromIntent(activity?.intent)
        barcodeView!!.decodeContinuous(callback)
        beepManager = BeepManager(activity)

        val networkConnectionIncterceptor = this.let { NetworkConnectionIncterceptor(requireContext()) }
        val apiClient = ApiClient(networkConnectionIncterceptor)
        val apiClientBasic = ApiClientBasicAuth(networkConnectionIncterceptor)
        val repository = RemoteRepository(apiClient,apiClientBasic)

        viewModelFactory = ViewmodelFactory(repository, requireContext())
        viewModel = ViewModelProvider(requireActivity(),viewModelFactory).get(SharedViewModel::class.java)


        val diff: Long = Calendar.getInstance().timeInMillis - getDateInMilli(Preferences.lastLoginDate!!)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        if(days  < 20){
            activity?.finish()
        }

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
//                flash_light.setBackgroundResource(R.drawable.turn_off_flash)
//                flash_light.background = ContextCompat.getDrawable(requireContext(), R.drawable.bt_ui)
                barcodeView!!.resume()
                barcodeView!!.setTorchOff()
                cameraScanner.visibility=View.VISIBLE
                flash_light.visibility=View.VISIBLE
            }else{
                requestPermission()
            }
        }

        var flashOpened=false
       // flash_light.setColorFilter(Color.argb(0, 0, 0, 0))
        flash_light.setSafeOnClickListener {
            if(!flashOpened){
                flashOpened=true
                barcodeView!!.setTorchOn()
                flash_light.setImageDrawable(resources.getDrawable(R.drawable.turn_on_flash,null))
                flash_light.background = ContextCompat.getDrawable(requireContext(), R.drawable.bt_ui)
            }else{
                flashOpened=false
                barcodeView!!.setTorchOff()
                flash_light.setImageDrawable(resources.getDrawable(R.drawable.turn_off_flash,null))
                flash_light.background = ContextCompat.getDrawable(requireContext(), R.drawable.bt_ui)
            }

        }
        exit_scan.setSafeOnClickListener {
            cameraScanner.visibility=View.GONE
            flash_light.visibility=View.GONE
            flash_light.setColorFilter(Color.argb(0, 0, 0, 0))
            barcodeView!!.pause()
        }

        add_product.setSafeOnClickListener {
             viewModel.getProduct(lastText)
        }

        viewModel.productRetrieved.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                (activity as MainActivity?)?.showBanner("Το προιόν προστέθηκε επιτυχώς στην λίστα!",true)
            }
        })

        viewModel.productExists.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                (activity as MainActivity?)?.showBanner("Το προιόν υπάρχει ήδη στην λίστα!")
            }
        })

        viewModel.noInternetException.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Δεν υπάρχει σύνδεση στο internet!")
        })
        viewModel.error?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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
        viewModel.error?.postValue(false)
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