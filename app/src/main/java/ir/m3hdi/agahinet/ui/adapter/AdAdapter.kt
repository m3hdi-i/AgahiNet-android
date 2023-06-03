package ir.m3hdi.agahinet.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.databinding.RvAdBinding
import kotlin.random.Random


class AdAdapter : PagingDataAdapter<Ad, AdAdapter.ViewHolder>(DiffUtilCallBack) {

    var onItemClickFunction:((ad: Ad)->Unit)? = null

    inner class ViewHolder(val binding: RvAdBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){

            /*items[position].let { ad->

                textViewAdTitle.text=ad.title
                container.setOnClickListener { onItemClickFunction?.invoke(ad) }
            }*/

            textViewAdTitle.text=getItem(position)?.title
            //bottomDivider.isVisible = position != itemCount-1
        }
    }

    /*fun notifyPageInserted(pageItemsCount:Int){
        this.notifyItemRangeInserted(this.itemCount,pageItemsCount)
    }
    fun clear() {
        if (itemCount>0){
            val count = itemCount
            items.clear()
            notifyItemRangeRemoved(0, count)
        }
    }*/


    object DiffUtilCallBack : DiffUtil.ItemCallback<Ad>() {
        override fun areItemsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem.adId == newItem.adId
        }

        override fun areContentsTheSame(oldItem: Ad, newItem: Ad): Boolean {
            return oldItem == newItem
        }
    }


}