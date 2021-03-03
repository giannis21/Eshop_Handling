package com.example.eshophandling.ui.cards

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.eshophandling.R
import com.example.eshophandling.SharedViewModel
import com.example.eshophandling.databinding.ViewCardsBinding
import com.example.eshophandling.databinding.ViewListBinding
import com.example.eshophandling.ui.cards.product_response.Data
import com.example.eshophandling.ui.cards.product_response.Product
import com.example.eshophandling.utils.setSafeOnClickListener

class CardAdapter(var context: Context, products: MutableList<Data>, var viewModel: SharedViewModel,var vertical:Boolean=false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val products_list = mutableListOf<Data>()
    var itemListener: ((Int, Product) -> Unit)? = null
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
        }

    }
    inner class ItemViewHolder(var viewCardsBinding: ViewCardsBinding) : RecyclerView.ViewHolder(viewCardsBinding.root){

        fun bind(holder: ItemViewHolder, position: Int) {
            holder.viewCardsBinding.productObj = products_list[position]

            holder.viewCardsBinding.quantityEdittext.setSelection(holder.viewCardsBinding.quantityEdittext.text.length)
            holder.viewCardsBinding.priceEdittext.setSelection(holder.viewCardsBinding.priceEdittext.text.length)
            holder.viewCardsBinding.priceEdittext.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            holder.viewCardsBinding.priceEdittext.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            holder.viewCardsBinding.quantityPlus.setOnClickListener {

                holder.viewCardsBinding.quantityEdittext.setText("${holder.viewCardsBinding.quantityEdittext.text.toString().toInt().plus(1)}")
                holder.viewCardsBinding.quantityEdittext.setSelection(holder.viewCardsBinding.quantityEdittext.text.length)

            }

            holder.viewCardsBinding.quantityMinus.setOnClickListener {

                holder.viewCardsBinding.quantityEdittext.setText("${holder.viewCardsBinding.quantityEdittext.text.toString().toInt().minus(1)}")
                holder.viewCardsBinding.quantityEdittext.setSelection(holder.viewCardsBinding.quantityEdittext.text.length)

            }

            if (holder.adapterPosition == 0) {
                if (CardsFragment.firstObject) {
                    //   holder.viewCardsBinding.deleteCard.alpha = 1f
                    CardsFragment.firstObject = false
                } else {
                    //    holder.viewCardsBinding.deleteCard.alpha = 0.5f
                }
            }

            if(products_list[position].status == 1 ){
                holder.viewCardsBinding.switch1.isChecked=true
                holder.viewCardsBinding.switch1.text = "Ενεργοποημένο"
            }else{
                holder.viewCardsBinding.switch1.isChecked=false
                holder.viewCardsBinding.switch1.text = "Απενεργοποιημένο"
            }

            holder.viewCardsBinding.switch1.setOnCheckedChangeListener { compoundButton, isEnabled ->
                isEnabled1=isEnabled
                if(isEnabled) {
                    holder.viewCardsBinding.switch1.text = "Ενεργοποημένο"
                    holder.viewCardsBinding.switch1.isChecked = true
                }else {
                    holder.viewCardsBinding.switch1.text = "Απενεργοποιημένο"
                    holder.viewCardsBinding.switch1.isChecked = false
                }
            }

            holder.viewCardsBinding.manuallyText.setSafeOnClickListener {
                itemHandler?.onRefresh(holder.adapterPosition,products_list[position].id,holder.viewCardsBinding.quantityEdittext.text.toString(), holder.viewCardsBinding.priceEdittext.text.toString(),isEnabled1)
            }

            holder.viewCardsBinding.submitBtn.setSafeOnClickListener {
                itemHandler?.onSubmit(holder.adapterPosition,products_list[position].id,holder.viewCardsBinding.quantityEdittext.text.toString(), holder.viewCardsBinding.priceEdittext.text.toString(),isEnabled1)
            }

            holder.viewCardsBinding.deleteCard.setSafeOnClickListener {
                if(position >=0 && holder.adapterPosition < itemCount)
                    itemHandler?.onDelete(products_list[ holder.adapterPosition ].id)
            }
        }
    }




    fun clear() {
        val diffCallback = UserDiffCallback(this.products_list, mutableListOf())
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.products_list.clear()
        diffResult.dispatchUpdatesTo(this)
    }

    fun swap(products: List<Data>) {
        val diffCallback = UserDiffCallback(this.products_list, products)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.products_list.clear()
        this.products_list.addAll(products)
        diffResult.dispatchUpdatesTo(this)
    }



//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        private val title: TextView = itemView.findViewById(R.id.title)
//        private val priceEdittext: EditText = itemView.findViewById(R.id.priceEdittext)
//        private val quantityEdittext: EditText = itemView.findViewById(R.id.quantityEdittext)
//        private val quantity_plus: ImageView = itemView.findViewById(R.id.quantity_plus)
//        private val quantity_minus: ImageView = itemView.findViewById(R.id.quantity_minus)
//        private val delete_card: TextView = itemView.findViewById(R.id.delete_card)
//
//
//        fun bind(context: Context, product: Data, itemListener: ((Int, Data) -> Unit)?) {
//
////            priceEdittext?.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
////            priceEdittext?.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
//            // priceEdittext?.filters = arrayOf<InputFilter>(WalletAmountInputFilter(0.00, 15000.00))
//            // priceEdittext?.setTextColor(resources.getColor(R.color.transparent, null))
//
//
//            priceEdittext.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                // val _spannable = SpannableStringBuilder("${s.toString()}€")
//                // priceEdittext.text = _spannable
//
//
//               }
//
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                }
//            })
//
//
//
//            itemListener?.invoke(adapterPosition, product)
//
//            if (adapterPosition == 0) {
//                if (CardsFragment.firstObject) {
//                    delete_card.alpha = 1f
//                    CardsFragment.firstObject = false
//                } else {
//                    delete_card.alpha = 0.5f
//                }
//            }
//
//
//
//            priceEdittext.setText("${product.price}")
//            quantityEdittext.setText("${product.quantity}")
//            quantity_plus.setOnClickListener {
//                quantityEdittext.setText("${quantityEdittext.text.toString().toInt().plus(1)}")
//                quantityEdittext.setSelection(quantityEdittext.text.length)
//            }
//            quantity_minus.setOnClickListener {
//                quantityEdittext.setText("${quantityEdittext.text.toString().toInt().minus(1)}")
//                quantityEdittext.setSelection(quantityEdittext.text.length)
//            }
//        }
//
//
//    }

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

