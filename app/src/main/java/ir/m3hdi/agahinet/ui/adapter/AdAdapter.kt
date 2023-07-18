package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.RvAdBinding
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.util.AppUtils.Companion.formatPrice
import ir.m3hdi.agahinet.util.AppUtils.Companion.getImageUrlByImageId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class AdAdapter(private val citiesViewModel: CitiesViewModel) : PagingDataAdapter<Ad, AdAdapter.ViewHolder>(DiffUtilCallBack) {

    var onItemClickFunction:((ad: Ad)->Unit)? = null

    inner class ViewHolder(val binding: RvAdBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        getItem(position)?.let {ad->
            with(holder.binding){

                textViewAdTitle.text=ad.title

                textViewPrice.text = formatPrice(ad.price)

                textViewTimeAndLoc.text= citiesViewModel.getTimeAndLocText(ad)
                bottomDivider.isVisible = position != itemCount-1
                container.setOnClickListener { onItemClickFunction?.invoke(ad) }

                if (ad.mainImageId!=null){
                    val imageUrl=getImageUrlByImageId(ad.mainImageId)
                    imageView.load(imageUrl)
                }else{
                    imageView.load(R.drawable.ad_placeholder)
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