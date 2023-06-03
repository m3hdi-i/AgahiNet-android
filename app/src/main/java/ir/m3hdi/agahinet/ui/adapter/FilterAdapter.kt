package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.domain.model.SearchFilters
import ir.m3hdi.agahinet.databinding.RvFilterBinding
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.FilterBox
import ir.m3hdi.agahinet.domain.model.FilterBoxType

class FilterAdapter : ListAdapter<FilterBox, FilterAdapter.ViewHolder>(DiffUtilCallBack) {

    var onItemCloseFunction:((filter: FilterBox)->Unit)? = null

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
        val newList= mutableListOf<FilterBox>()

        filters.category?.let {
            newList.add(FilterBox(FilterBoxType.CATEGORY,it.title))
        }
        if (filters.minPrice!=null || filters.maxPrice!=null)
            newList.add(FilterBox(FilterBoxType.PRICE,"قیمت"))

        filters.cities?.let {
            it.forEach{c->
                newList.add(FilterBox(FilterBoxType.PRICE,c.title))
            }
        }
        submitList(newList)
    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<FilterBox>() {
        override fun areItemsTheSame(oldItem: FilterBox, newItem: FilterBox): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: FilterBox, newItem: FilterBox): Boolean {
            return oldItem == newItem
        }
    }
}