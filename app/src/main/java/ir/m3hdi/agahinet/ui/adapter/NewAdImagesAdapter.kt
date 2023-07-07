package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.data.repository.AD_MAX_IMAGES_NUMBER
import ir.m3hdi.agahinet.databinding.RvNewAdImageBinding
import ir.m3hdi.agahinet.domain.model.Category

class NewAdImagesAdapter : RecyclerView.Adapter<NewAdImagesAdapter.ViewHolder>() {

    var onItemClickFunction:((category: Category)->Unit)? = null

    private val items = List(AD_MAX_IMAGES_NUMBER) { Pair<String?,Boolean>(null,false) }

    inner class ViewHolder(val binding: RvNewAdImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvNewAdImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding){


        }
    }

}