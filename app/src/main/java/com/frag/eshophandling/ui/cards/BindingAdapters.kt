package com.frag.tvshows

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.frag.eshophandling.R


object bindingAdapters {


    @BindingAdapter(value = ["priceText"])
    @JvmStatic
    fun TextView.priceText(price: MutableLiveData<Int>?) {
        this.text = "$price€"
    }

    @BindingAdapter(value = ["isEnabled"])
    @JvmStatic
    fun SwitchCompat.isEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            this.isChecked = false
            this.text = "Απενεργοποιημένο"
        } else {
            this.isChecked = true
            this.text = "Ενεργοποιημένο"
        }

    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        var progressBar = (view.parent as ConstraintLayout).findViewById<ProgressBar>(R.id.progressBar)
        Glide.with(view.context).load(url)
                .error(Glide.with(view.context).load(R.drawable.shoppingbasket))
                .apply(RequestOptions().transform(RoundedCorners(40)))
                .skipMemoryCache(true)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                }).into(view)

    }
}