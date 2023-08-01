package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.RvAdManageBinding
import ir.m3hdi.agahinet.domain.model.ad.Ad
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.util.AppUtils


class AdManageAdapter(private val citiesViewModel: CitiesViewModel,private val editable:Boolean=true) :  RecyclerView.Adapter<AdManageAdapter.ViewHolder>() {

    private val items= mutableListOf<Ad>()

    var onItemClickFunction:((ad: Ad)->Unit)? = null
    var onItemEditFunction:((ad: Ad)->Unit)? = null
    var onItemDeleteFunction:((ad: Ad)->Unit)? = null

    inner class ViewHolder(val binding: RvAdManageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(RvAdManageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        if (!editable)
            vh.binding.buttonEdit.isGone = true
        return vh
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding){

            val ad = items[position]

            textViewAdTitle.text=ad.title

            textViewPrice.text = AppUtils.formatPrice(ad.price)

            textViewTimeAndLoc.text= citiesViewModel.getTimeAndLocText(ad)

            container.setOnClickListener { onItemClickFunction?.invoke(ad) }
            buttonDelete.setOnClickListener { onItemDeleteFunction?.invoke(ad) }
            if (editable)
                buttonEdit.setOnClickListener { onItemEditFunction?.invoke(ad) }

            if (ad.mainImageId!=null){
                val imageUrl= AppUtils.getImageUrlByImageId(ad.mainImageId)
                imageView.load(imageUrl)
            }else{
                imageView.load(R.drawable.ad_placeholder)
            }
        }
    }

    fun setItems(ads: List<Ad>) {
        if (itemCount>0){
            val range = itemCount
            items.clear()
            notifyItemRangeRemoved(0,range)
        }
        items.addAll(ads)
        notifyItemRangeInserted(0,itemCount)
    }


}