package com.example.tvshows

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.eshophandling.R
import org.xml.sax.DTDHandler


object bindingAdapters {


    @BindingAdapter(value = ["priceText"])
    @JvmStatic
    fun TextView.priceText(price: MutableLiveData<Int>?) {
        this.text = "$price€"
    }

    @BindingAdapter(value = ["isEnabled"])
    @JvmStatic
    fun SwitchCompat.isEnabled(isEnabled: Boolean) {
        if(isEnabled) {
            this.isChecked = false
            this.text = "Απενεργοποιημένο"
        }else{
            this.isChecked=true
            this.text = "Ενεργοποιημένο"
        }

    }
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
       var progressBar=   (view.parent as ConstraintLayout).findViewById<ProgressBar>(R.id.progressBar)
        Glide.with(view.context).load(url)
                .error(Glide.with(view.context).load(R.drawable.shoppingbasket))
                .apply(RequestOptions().transform(RoundedCorners(40)))
                .skipMemoryCache(true)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility=View.GONE
                        return false
                    }

                }).into(view)

    }
//    @BindingAdapter("imageUrl", "progressbar")
//    @JvmStatic
//    fun loadImage(view: ImageView, url: String?, progressBar: ProgressBar) {
//
//        Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/$url")
//            .error(Glide.with(view.context).load(R.drawable.placeholder))
//            .listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    progressBar.setGone()
//                    return false
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    progressBar.setGone()
//                    return false
//                }
//            }).into(view)
//    }
//
//    @BindingAdapter("imageDetails", "progressBar_details")
//    @JvmStatic
//    fun loadImageDetails(view: ImageView, url: String?, progressBar: ProgressBar) {
//
//        Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/$url")
//            .listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    progressBar.setGone()
//                    return false
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    progressBar.setGone()
//                    return false
//                }
//            })
//            .error(Glide.with(view.context).load(R.drawable.clapperboard)).into(view)
//    }
//
//    @BindingAdapter("imageWatchlist")
//    @JvmStatic
//    fun loadWatchlistImage(view: ImageView, url: String?) {
//
//        Glide.with(view.context).load("https://image.tmdb.org/t/p/w500/$url")
//
//            .listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//            })
//            .error(Glide.with(view.context).load(R.drawable.clapperboard)).into(view)
//    }
//
//    @BindingAdapter("formatDate")
//    @JvmStatic
//    fun changeformatDate(view: TextView, date: String?) {
//        var newDate: String = "-"
//
//        if (!date.isNullOrEmpty()) {
//            val date1 = date.split("-")
//            newDate = date1[2] + "/" + date1[1] + "/" + date1[0]
//        }
//        view.text = newDate
//    }
//
//    @BindingAdapter("Stringifnull")
//    @JvmStatic
//    fun setString(view: TextView, overview: String?) {
//        var over = ""
//        if (view.id == R.id.status_)
//            over = "Status: (-)"
//        else
//            over = "-"
//
//        if (!overview.isNullOrEmpty())
//            if (view.id == R.id.status_)
//                over = "Status: $overview"
//            else
//                over = overview
//
//        view.setText(over)
//    }
//
//    @BindingAdapter("statusifnull")
//    @JvmStatic
//    fun setstatus(view: TextView, overview: String?) {
//
//        var over = "Status: (-)"
//
//        if (!overview.isNullOrEmpty())
//            over = "Status: $overview"
//
//        view.setText(over)
//    }
//
//    @BindingAdapter("genres")
//    @JvmStatic
//    fun loadgenres(view: TextView, genres: List<Genre>?) {
//        var genr = "-"
//        if (!genres.isNullOrEmpty()) {
//            genr = ""
//            genres.indices.forEach { i ->
//                genr += genres[i].name
//                if (i < genres.size - 1)
//                    genr += " | "
//            }
//
//        }
//
//        view.setText(genr)   //apla bazo ta eidi tis seiras kai an den iparxo0n bazo "-"
//    }
//
//    @BindingAdapter("createdBy")
//    @JvmStatic
//    fun loadcreators(view: TextView, creators: List<CreatedBy>?) {
//        var creator = "-"
//        if (!creators.isNullOrEmpty()) {
//            creator = ""
//            creators.indices.forEach { i ->
//                creator += creators[i].name
//                if (i < creators.size - 1)
//                    creator += ","
//            }
//        }
//        view.setText(creator)   //apla bazo ta eidi tis seiras kai an den iparxo0n bazo "-"
//    }
//





}
