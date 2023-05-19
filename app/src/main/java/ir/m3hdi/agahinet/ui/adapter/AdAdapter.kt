package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.data.model.Ad
import ir.m3hdi.agahinet.databinding.RvAdBinding


class AdAdapter : RecyclerView.Adapter<AdAdapter.ViewHolder>() {

    var items= mutableListOf<Ad>()

    var onItemClickFunction:((ad:Ad)->Unit)? = null

    inner class ViewHolder(val binding: RvAdBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){

            items[position].let { ad->

                textViewAdTitle.text=ad.title
                container.setOnClickListener { onItemClickFunction?.invoke(ad) }
            }

            bottomDivider.isVisible = position != itemCount-1
        }
    }

    fun notifyPageInserted(pageItemsCount:Int){
        this.notifyItemRangeInserted(this.itemCount,pageItemsCount)
    }
    fun clearItems() {
        if (itemCount>0){
            val count = itemCount
            items.clear()
            notifyItemRangeRemoved(0, count)
        }

    }
}