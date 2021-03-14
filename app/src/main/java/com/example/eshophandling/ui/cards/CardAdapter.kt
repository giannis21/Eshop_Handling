package com.example.eshophandling.ui.cards

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.eshophandling.R
import com.example.eshophandling.databinding.ViewCardsBinding
import com.example.eshophandling.databinding.ViewListBinding
import com.example.eshophandling.data.model.product_response.Data
import com.example.eshophandling.utils.setSafeOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CardAdapter(var context: Context, products: MutableList<Data>, private var coroutineScope: CoroutineScope, var vertical: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var isScrolling: Boolean=false
    private val products_list = mutableListOf<Data>()
    var itemHandler:ItemHandler?=null
    var isEnabled1 = true

    init {
        products_list.addAll(products)
    }


    override fun getItemViewType(position: Int): Int {
        return if (vertical) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
            0 -> { // vertical scrolling
                val binding = DataBindingUtil.inflate<ViewListBinding>(LayoutInflater.from(parent.context), R.layout.view_list, parent, false)
                VerticalViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ViewCardsBinding>(LayoutInflater.from(parent.context), R.layout.view_cards, parent, false)
                ItemViewHolder(binding)
            }
        }

    }


    override fun getItemCount() = products_list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        when (holder) {
            is ItemViewHolder -> {
                holder.bind(holder, position)
            }
            is VerticalViewHolder -> {
                holder.bind(holder, position)
            }
        }
    }

    inner class VerticalViewHolder(var viewListBinding: ViewListBinding) : RecyclerView.ViewHolder(viewListBinding.root) {

        fun bind(holder: VerticalViewHolder, position: Int) {
            holder.viewListBinding.product = products_list[position]
            holder.viewListBinding.minusButton.setOnClickListener {

                if(holder.viewListBinding.ammountText.text.toString().toInt().minus(1) == 0) {
                    holder.viewListBinding.ammountText.text = "1"
                    products_list[position].quantity_minus = "1"
                    coroutineScope.launch(Dispatchers.Default) {
                        itemHandler?.onRefreshQuantity(holder.adapterPosition, products_list[holder.adapterPosition].id, products_list[holder.adapterPosition].quantity_minus!!)
                    }
                }else{
                    coroutineScope.launch(Dispatchers.Default) {
                        itemHandler?.onRefreshQuantity(holder.adapterPosition, products_list[holder.adapterPosition].id, products_list[holder.adapterPosition].quantity_minus!!)
                    }
                    products_list[position].quantity_minus= holder.viewListBinding.ammountText.text.toString().toInt().minus(1).toString()
                    holder.viewListBinding.ammountText.text=  holder.viewListBinding.ammountText.text.toString().toInt().minus(1).toString()
                }
            }

            holder.viewListBinding.plusButton.setOnClickListener {
                holder.viewListBinding.ammountText.text=  holder.viewListBinding.ammountText.text.toString().toInt().plus(1).toString()
                products_list[position].quantity_minus= holder.viewListBinding.ammountText.text.toString().toInt().plus(1).toString()

                coroutineScope.launch(Dispatchers.Default) {
                    itemHandler?.onRefreshQuantity(holder.adapterPosition,products_list[holder.adapterPosition].id,products_list[holder.adapterPosition].quantity_minus ?: "1")
                }

            }
        }

    }
    inner class ItemViewHolder(var viewCardsBinding: ViewCardsBinding) : RecyclerView.ViewHolder(viewCardsBinding.root){

        fun bind(holder: ItemViewHolder, position: Int) {
            holder.viewCardsBinding.productObj = products_list[position]

            holder.viewCardsBinding.quantityEdittext.setSelection(holder.viewCardsBinding.quantityEdittext.text.length)
            holder.viewCardsBinding.priceEdittext.setSelection(holder.viewCardsBinding.priceEdittext.text.length)
            holder.viewCardsBinding.priceEdittext.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            holder.viewCardsBinding.priceEdittext.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            holder.viewCardsBinding.quantityEdittext.addTextChangedListener(CustomEditTextListener(holder,"quantity"))
            holder.viewCardsBinding.priceEdittext.addTextChangedListener(CustomEditTextListener(holder,"price"))


            holder.viewCardsBinding.quantityPlus.setOnClickListener {

                holder.viewCardsBinding.quantityEdittext.setText("${holder.viewCardsBinding.quantityEdittext.text.toString().toInt().plus(1)}")
                holder.viewCardsBinding.quantityEdittext.setSelection(holder.viewCardsBinding.quantityEdittext.text.length)

            }

            holder.viewCardsBinding.quantityMinus.setOnClickListener {

                holder.viewCardsBinding.quantityEdittext.setText("${holder.viewCardsBinding.quantityEdittext.text.toString().toInt().minus(1)}")
                holder.viewCardsBinding.quantityEdittext.setSelection(holder.viewCardsBinding.quantityEdittext.text.length)

            }


            if(products_list[position].status == 1 ){
                holder.viewCardsBinding.switch1.isChecked=true
                holder.viewCardsBinding.switch1.text = context.getString(R.string.activated)
            }else{
                holder.viewCardsBinding.switch1.isChecked=false
                holder.viewCardsBinding.switch1.text = context.getString(R.string.deactivated)
            }

            holder.viewCardsBinding.switch1.setOnCheckedChangeListener { compoundButton, isEnabled ->
                isEnabled1=isEnabled
                if(isEnabled) {
                    holder.viewCardsBinding.switch1.text = context.getString(R.string.activated)
                    holder.viewCardsBinding.switch1.isChecked = true
                    products_list[holder.adapterPosition].status = 1
                }else {
                    holder.viewCardsBinding.switch1.text = context.getString(R.string.deactivated)
                    holder.viewCardsBinding.switch1.isChecked = false
                    products_list[holder.adapterPosition].status = 0
                }
            }



            holder.viewCardsBinding.submitBtn.setSafeOnClickListener {
                itemHandler?.onSubmit(holder.adapterPosition, products_list[position].id, holder.viewCardsBinding.quantityEdittext.text.toString(), holder.viewCardsBinding.priceEdittext.text.toString(), isEnabled1)
            }

            holder.viewCardsBinding.deleteCard.setSafeOnClickListener {
                if(position >=0 && holder.adapterPosition < itemCount)
                    itemHandler?.onDelete(products_list[holder.adapterPosition].id)
            }
        }
        private inner class CustomEditTextListener(var holder: ItemViewHolder,var inputField: String) : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                  try {
                      if (inputField == "price")
                          products_list[holder.adapterPosition].price = charSequence.toString()
                      else
                          products_list[holder.adapterPosition].quantity = charSequence.toString()
                  }catch (e: Exception){
                      println("errorrr ${e.message}" )
                  }


                    synchronized(this){
                        coroutineScope.launch(Dispatchers.Default) {
                            try {
                                itemHandler?.onRefresh(holder.adapterPosition, products_list[holder.adapterPosition].id, holder.viewCardsBinding.quantityEdittext.text.toString(), holder.viewCardsBinding.priceEdittext.text.toString(), isEnabled1)
                            }catch (e: Exception){}
                        }
                    }

            }

            override fun afterTextChanged(editable: Editable) {}

        }
    }




    fun clear() {
        val diffCallback = UserDiffCallback(this.products_list, mutableListOf())
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.products_list.clear()
        diffResult.dispatchUpdatesTo(this)
    }

    fun submitList(products: List<Data>) {
        val diffCallback = UserDiffCallback(this.products_list, products)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.products_list.clear()
        this.products_list.addAll(products)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateList(products: List<Data>) {

        this.products_list.clear()
        this.products_list.addAll(products)
        notifyDataSetChanged()

    }

}


class UserDiffCallback(private val oldList: List<Data>, private val newList: List<Data>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

}

