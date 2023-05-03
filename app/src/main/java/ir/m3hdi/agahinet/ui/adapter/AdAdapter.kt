package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.data.model.Ad
import ir.m3hdi.agahinet.databinding.RvAdBinding


class AdAdapter : RecyclerView.Adapter<AdAdapter.ViewHolder>() {

    var items= mutableListOf<Ad>()

    private var onItemClickFunction:((ad:Ad)->Unit)? = null

    fun setOnItemClickListener(func:(ad:Ad)->Unit){
        this.onItemClickFunction=func
    }

    inner class ViewHolder(val binding: RvAdBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){

            items[position].let { ad->
                this.textViewAdTitle.text=ad.title
                this.container.setOnClickListener { onItemClickFunction?.invoke(ad) }
            }
        }
    }

    fun notifyPageInserted(pageItemsCount:Int){
        this.notifyItemRangeInserted(this.itemCount,pageItemsCount)
    }
}