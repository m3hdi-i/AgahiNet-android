package ir.m3hdi.agahinet.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.databinding.RvAdBinding
import ir.m3hdi.agahinet.util.AppUtils.Companion.formatPriceAndAddCurrencySuffix
import ir.m3hdi.agahinet.util.AppUtils.Companion.getImageUrlByImageId
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlin.random.Random


class AdAdapter : PagingDataAdapter<Ad, AdAdapter.ViewHolder>(DiffUtilCallBack) {

    var onItemClickFunction:((ad: Ad)->Unit)? = null

    inner class ViewHolder(val binding: RvAdBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        getItem(position)?.let {ad->
            with(holder.binding){


                textViewAdTitle.text=ad.title

                textViewPrice.text = ad.price?.takeIf { it.isNotBlank() }?.formatPriceAndAddCurrencySuffix() ?: "توافقی"

                bottomDivider.isVisible = position != itemCount-1
                container.setOnClickListener { onItemClickFunction?.invoke(ad) }
                ad.mainImageId?.let {
                    val imageUrl=getImageUrlByImageId(it)
                    imageView.load(imageUrl){
                        crossfade(true)
                    }
                }

            }
        }

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<Ad>() {
        override fun areItemsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem.adId == newItem.adId
        }

        override fun areContentsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem == newItem
        }
    }


}