package ir.m3hdi.agahinet.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.data.model.AdFilters
import ir.m3hdi.agahinet.databinding.RvFilterBinding

class FilterAdapter : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    val items= mutableListOf<String>()

    var onItemCloseFunction:((filter: String)->Unit)? = null

    inner class ViewHolder(val binding: RvFilterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.chip.text=items[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilters(filters: AdFilters) {
        items.clear()
        filters.category?.let {
            items.add("دسته $it")
        }

        if (filters.minPrice!=null || filters.maxPrice!=null)
            items.add("قیمت")

        filters.cities?.let {
            it.forEach{cityId->
                items.add("شهر $cityId")
            }
        }
        notifyDataSetChanged()
    }

}