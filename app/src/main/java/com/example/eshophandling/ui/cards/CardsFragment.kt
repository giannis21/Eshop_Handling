package com.example.eshophandling.ui.cards

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.example.alertlocation_kotlin.utils.Preferences.username
import com.example.eshophandling.MainActivity
import com.example.eshophandling.R
import com.example.eshophandling.ui.viewmodels.SharedViewModel
import com.example.eshophandling.data.api.ApiClient
import com.example.eshophandling.data.api.ApiClientBasicAuth
import com.example.eshophandling.data.api.NetworkConnectionIncterceptor
import com.example.eshophandling.data.api.RemoteRepository
import com.example.eshophandling.data.model.product_response.Data
import com.example.eshophandling.data.model.submit_product.SubmittedProduct
import com.example.eshophandling.utils.milliToDate
import com.example.eshophandling.utils.setSafeOnClickListener
import com.example.eshophandling.ui.viewmodels.ViewmodelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_cards.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CardsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardsFragment : Fragment(),ItemHandler {

    private var isScrolling=false
    var recyclerview: RecyclerView? = null
    var mAdapter: CardAdapter? = null

    var verticalrecyclerview: RecyclerView? = null
    var mAdapterVertical: CardAdapter? = null
    private val job = SupervisorJob()
    private val defaultScope = CoroutineScope(Dispatchers.Default + job)
    private lateinit var viewModel: SharedViewModel
    private lateinit var viewModelFactory: ViewmodelFactory
    private var currentPosition=0
    private var adapterCountItems=0
    var showCards=true


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updated.postValue(false)
        viewModel.productRetrieved.postValue(false)
        viewModel.noInternetException.postValue(false)
        viewModel.error.postValue(false)
        viewModel.productExists.postValue(false)
        viewModel.productNotFound.postValue(false)
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        recyclerview=view.findViewById(R.id.recyclerview)
        verticalrecyclerview=view.findViewById(R.id.verticalrecyclerview)

        val networkConnectionIncterceptor = this.let { NetworkConnectionIncterceptor(requireContext()) }
        val apiClient = ApiClient(networkConnectionIncterceptor)
        val apiClientBasic = ApiClientBasicAuth(networkConnectionIncterceptor)
        val repository = RemoteRepository(apiClient, apiClientBasic)

        viewModelFactory = ViewmodelFactory(repository, requireContext())
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(SharedViewModel::class.java)


        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerview!!.layoutManager = layoutManager

        val verticallayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        verticalrecyclerview!!.layoutManager = verticallayoutManager


        mAdapter = CardAdapter(requireContext(), viewModel.allProducts.value
                ?: mutableListOf(), defaultScope)
        mAdapter!!.itemHandler=this
        recyclerview!!.adapter = mAdapter

        mAdapterVertical = CardAdapter(requireContext(), viewModel.allProducts.value
                ?: mutableListOf(), defaultScope, true)
        verticalrecyclerview!!.adapter = mAdapterVertical
        mAdapterVertical!!.itemHandler=this
        initSwipe()

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerview)

        if(mAdapter?.itemCount!! > 0)
          positionstxt.text = "1/${mAdapter?.itemCount}"

        show_bottomsheet.setSafeOnClickListener {
            showDialog()
        }


        recyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mAdapter!!.itemCount > 0) {
                    val visibleChild: View = recyclerView.getChildAt(0)
                    val firstChild: Int = recyclerView.getChildAdapterPosition(visibleChild)
                    positionstxt.text = "${firstChild.plus(1)}/${mAdapter?.itemCount}"
                    currentPosition = firstChild.plus(1)

                    mAdapter?.itemCount?.let {
                        adapterCountItems = it
                    }
                }
                mAdapter!!.isScrolling=true

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                mAdapter!!.isScrolling=false
            }
        })


        show_list.setOnClickListener {
            if(showCards){
                showCards=false
                horizontalRecyclerGroup.visibility=View.GONE
                verticalrecyclerview!!.visibility=View.VISIBLE

                mAdapterVertical!!.updateList(viewModel.allProducts.value?.toList()!!)
                show_list.setImageResource(R.drawable.cards_icon)

            }else{

                showCards=true
                if(mAdapter!!.itemCount == 0)
                    horizontalRecyclerGroup.visibility=View.GONE
                else
                    horizontalRecyclerGroup.visibility=View.VISIBLE

                verticalrecyclerview!!.visibility=View.GONE

                show_list.setImageResource(R.drawable.ic_list)

                if (currentPosition > viewModel.allProducts.value?.size ?: 0)
                    positionstxt.text = "${viewModel.allProducts.value?.size}/${viewModel.allProducts.value?.size}"
                else
                    positionstxt.text = "${currentPosition}/${viewModel.allProducts.value?.size}"
            }

        }

        viewModel.allProducts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.isEmpty())
                positionstxt.visibility = View.GONE
            else {
                if (recyclerview!!.isVisible)
                    positionstxt.visibility = View.VISIBLE
            }

            if (it.size >= 1){
                show_bottomsheet.visibility = View.VISIBLE
                show_list.visibility = View.VISIBLE
            }else{
                show_bottomsheet.visibility = View.GONE
                show_list.visibility = View.GONE
            }


            if (it.isEmpty()) {
                mAdapter?.clear()
                mAdapterVertical?.clear()
                empty_placeholder.visibility = View.VISIBLE
            } else {
                mAdapter?.submitList(it)
                mAdapterVertical?.submitList(it)
                empty_placeholder.visibility = View.GONE
            }

        })


        viewModel.noInternetException.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                (activity as MainActivity?)?.showBanner("Δεν υπάρχει σύνδεση στο internet!")
                viewModel.noInternetException.postValue(false)
            }
        })

        viewModel.error.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Ουπς, κάτι πήγε λάθος!")
        })

        viewModel.updated.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Το προϊόν ενημερώθηκε επιτυχώς!", true)
        })

        viewModel.productsDeleted.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                viewModel.deleteAllProducts()
                viewModel.productsDeleted.postValue(false)
            }
        })

    }

    fun showDialog(){
        val dialog = BottomSheetDialog(requireContext())
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)
        dialog.findViewById<TextView>(R.id.delete_cards)?.setSafeOnClickListener {
            showconfirmDialog(msg = getString(R.string.delete_all_products_dialog_title)){
                if(it){
                    viewModel.deleteAllProducts()
                    (activity as MainActivity?)?.showBanner(getString(R.string.proucts_deleted), true)
                }
                dialog.dismiss()
            }

        }
        dialog.findViewById<TextView>(R.id.submit_all_btn)?.setSafeOnClickListener {
            showconfirmDialog(msg = getString(R.string.submit_all_products_title_dialog)){
                if(it){
                    viewModel.submitAllProducts(showCards){
                        (activity as MainActivity?)?.showBanner(getString(R.string.products_submitted), true)
                    }

                }
                dialog.dismiss()
            }

        }
        dialog.show()
    }

    private fun initSwipe() {


        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel.removeItemAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(verticalrecyclerview)
    }




    override fun onSubmit(position: Int, id: Int, quantity: String, price: String, isEnabled1: Boolean)  {
        val product= viewModel.allProducts.value?.find { it.id == id }
        product?.let {
            val prod_to_be_submitted= SubmittedProduct(product.id, price.toFloat(), quantity.toInt(), product.sku, if (isEnabled1) 1 else 0, username!!, milliToDate(Calendar.getInstance().timeInMillis.toString(),true))
            viewModel.submitProduct(prod_to_be_submitted)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onDelete(id: Int) {
        positionstxt.text = "${currentPosition}/${viewModel.allProducts.value?.size?.minus(1)}"
        viewModel.deleteProduct(id)
    }

    override fun onRefresh(position: Int, id: Int, quantity: String, price: String, isEnabled1: Boolean) {
        val product= viewModel.allProducts.value?.find { it.id == id }

        if(!mAdapter!!.isScrolling){
            product?.quantity=quantity
            product?.price = price
            product?.status= if(isEnabled1) 1 else 0

            product?.let {
                if(position>=0 && position<= mAdapter!!.itemCount){
                    viewModel.allProducts.value?.set(position, it)
                    viewModel.notifyObserver()
                }

            }

        }


    }

    override fun onRefreshQuantity(position: Int, id: Int, quantity: String) {
        try {
            synchronized(this){
                val product= viewModel.allProducts.value?.find { it.id == id }

                product?.quantity_minus=quantity.toInt().minus(1).toString()

                product?.let {
                    if(position>=0 && position<= mAdapter!!.itemCount){
                        viewModel.allProducts.value?.set(position, it)
                        viewModel.notifyObserver()
                    }

                }
            }



        }catch (e: Exception){}

    }

    fun showconfirmDialog(msg: String, callback: ((Boolean) -> Unit)){
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setCancelable(true)

        alertDialogBuilder.setPositiveButton("Ναι") { dialog, _ ->
            dialog.cancel()
            callback.invoke(true)
        }
        alertDialogBuilder.setNegativeButton("Όχι") { dialog, _ ->
            dialog.cancel()
            callback.invoke(false)
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}