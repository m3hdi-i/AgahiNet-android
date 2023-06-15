package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.databinding.RvAdBinding
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.AppUtils.Companion.formatPriceAndAddCurrencySuffix
import ir.m3hdi.agahinet.util.AppUtils.Companion.getImageUrlByImageId
import ir.m3hdi.agahinet.util.PersianTimeAgo.Companion.dateTimeStringToTimeAgo


class AdAdapter : PagingDataAdapter<Ad, AdAdapter.ViewHolder>(DiffUtilCallBack) {

    var onItemClickFunction:((ad: Ad)->Unit)? = null

    lateinit var allCities: HashMap<Int,String>

    inner class ViewHolder(val binding: RvAdBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        getItem(position)?.let {ad->
            with(holder.binding){

                textViewAdTitle.text=ad.title

                textViewPrice.text = ad.price?.takeIf { it.isNotBlank() }?.formatPriceAndAddCurrencySuffix() ?: "توافقی"

                val cityName = allCities[ad.city]
                val timeAgoText = dateTimeStringToTimeAgo(ad.createdAt)
                val timeAndLocation = cityName?.let { "$timeAgoText در $it" } ?: timeAgoText

                textViewTimeAndLoc.text= timeAndLocation
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