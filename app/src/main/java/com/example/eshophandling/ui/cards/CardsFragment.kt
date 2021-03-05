package com.example.eshophandling.ui.cards

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.example.eshophandling.MainActivity
import com.example.eshophandling.R
import com.example.eshophandling.SharedViewModel
import com.example.eshophandling.api.ApiClient
import com.example.eshophandling.api.ApiClientBasicAuth
import com.example.eshophandling.api.NetworkConnectionIncterceptor
import com.example.eshophandling.api.RemoteRepository
import com.example.eshophandling.ui.cards.product_response.Data
import com.example.eshophandling.ui.cards.submit_product.SubmittedProduct
import com.example.eshophandling.utils.setSafeOnClickListener
import com.example.tvshows.ui.nowplaying.ViewmodelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_cards.*
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

    private lateinit var viewModel: SharedViewModel
    private lateinit var viewModelFactory: ViewmodelFactory
    private var currentPosition=0
    private var adapterCountItems=0
    companion object{
        var firstObject=true
    }

    override fun onResume() {
        super.onResume()
        firstObject=true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstObject=false
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

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

        val dataArticles = ArrayList<Data>()
        var a=2.toString()
        var product=Data(1, "1", 2, 30, a, 1)
        var product1=Data(2, "1", 1, 45, a, 1)
        var product2=Data(3, "1", 1, 40, a, 1)
        var product3=Data(4, "1", 1, 23, a, 1)
        var product4=Data(5, "1", 1, 43, a, 0)
        var product5=Data(11, "1", 1, 30, a, 0)
        var product6=Data(122, "1", 1, 45, a, 0)
        var product7=Data(32, "1", 1, 40, a, 0)

        dataArticles.add(product)
        dataArticles.add(product1)
        dataArticles.add(product2)
        dataArticles.add(product3)
        dataArticles.add(product4)
        dataArticles.add(product5)
        dataArticles.add(product6)
        dataArticles.add(product7)

        viewModel.allProducts.value = dataArticles
        mAdapter = CardAdapter(requireContext(), viewModel.allProducts.value
                ?: mutableListOf(), viewModel)
        mAdapter!!.itemHandler=this
        recyclerview!!.adapter = mAdapter

        mAdapterVertical = CardAdapter(requireContext(), viewModel.allProducts.value
                ?: mutableListOf(), viewModel, true)
        verticalrecyclerview!!.adapter = mAdapterVertical
        initSwipe()

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerview)

        positionstxt.text = "1/${mAdapter?.itemCount}"

        show_bottomsheet.setSafeOnClickListener {
            showDialog()
        }

        if(mAdapter!!.itemCount == 0){
        //    positionstxt.visibility=View.GONE
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


            }
        })

        var showCards=true
        show_list.setOnClickListener {
            if(showCards){
                horizontalRecyclerGroup.visibility=View.GONE
                verticalrecyclerview!!.visibility=View.VISIBLE
                showCards=false

            }else{
                if(mAdapter!!.itemCount == 0)
                    horizontalRecyclerGroup.visibility=View.GONE
                else
                    horizontalRecyclerGroup.visibility=View.VISIBLE

                verticalrecyclerview!!.visibility=View.GONE
                showCards=true

                if (currentPosition>viewModel.allProducts.value?.size ?: 0)
                    positionstxt.text = "${viewModel.allProducts.value?.size}/${viewModel.allProducts.value?.size}"
                else
                    positionstxt.text = "${currentPosition}/${viewModel.allProducts.value?.size}"
            }

        }

        viewModel.allProducts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.isEmpty())
                positionstxt.visibility = View.GONE
            else{
                if(recyclerview!!.isVisible)
                    positionstxt.visibility = View.VISIBLE
            }


            if (it.isEmpty()) {
                mAdapter?.clear()
                mAdapterVertical?.clear()
                empty_placeholder.visibility = View.VISIBLE
            } else {
                mAdapter?.swap(it)
                mAdapterVertical?.swap(it)
                empty_placeholder.visibility = View.GONE
            }

        })


        viewModel.noInternetException.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Δεν υπάρχει σύνδεση στο internet!")
        })
        viewModel.error.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Ουπς, κάτι πήγε λάθος!")
        })

        viewModel.updated.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                (activity as MainActivity?)?.showBanner("Το προϊόν ενημερώθηκε επιτυχώς!", true)
        })

    }

    fun showDialog(){
        val dialog = BottomSheetDialog(requireContext())
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

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

        val prod_to_be_submitted=SubmittedProduct(product?.id!!, price.toInt(), quantity.toInt(), product.sku, if (isEnabled1) 1 else 0, "user1", "date")
        viewModel.submitProduct(prod_to_be_submitted)
    }

    override fun onDelete(id: Int) {
        positionstxt.text = "${currentPosition}/${viewModel.allProducts.value?.size?.minus(1)}"
        viewModel.deleteProduct(id)
    }

    override fun onRefresh(position: Int, id: Int, quantity: String, price: String, isEnabled1: Boolean) {
        val product= viewModel.allProducts.value?.find { it.id == id }

        product?.quantity=quantity.toInt()
        product?.price = price.toInt()
        product?.status= if(isEnabled1) 1 else 0

        product?.let {
            viewModel.allProducts.value?.set(position, it)
        }
        mAdapter?.swap(viewModel.allProducts.value!!)

    }


}