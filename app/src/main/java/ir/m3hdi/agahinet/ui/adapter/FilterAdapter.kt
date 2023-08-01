package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.domain.model.ad.SearchFilters
import ir.m3hdi.agahinet.databinding.RvFilterBinding
import ir.m3hdi.agahinet.domain.model.ad.FilterTag

class FilterAdapter : ListAdapter<FilterTag, FilterAdapter.ViewHolder>(DiffUtilCallBack) {

    var onItemCloseFunction:((filterTag: FilterTag)->Unit)? = null

    inner class ViewHolder(val binding: RvFilterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.chip.apply {
            text = item.title
            setOnCloseIconClickListener {
                onItemCloseFunction?.invoke(item)
            }
        }
    }

    fun setFilters(filters: SearchFilters) {
        val newList= mutableListOf<FilterTag>()

        filters.category?.let {
            newList.add(FilterTag.CATEGORY(it))
        }
        if (filters.minPrice!=null || filters.maxPrice!=null)
            newList.add(FilterTag.PRICE())

        filters.cities?.let {
            it.forEach{c->
                newList.add(FilterTag.CITY(c))
            }
        }
        submitList(newList)
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<FilterTag>() {
        override fun areItemsTheSame(oldItem: FilterTag, newItem: FilterTag): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: FilterTag, newItem: FilterTag): Boolean {
            return oldItem == newItem
        }
    }
}