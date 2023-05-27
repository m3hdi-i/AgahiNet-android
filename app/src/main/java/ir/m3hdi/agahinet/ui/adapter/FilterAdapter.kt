package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.domain.model.SearchFilters
import ir.m3hdi.agahinet.databinding.RvFilterBinding

class FilterAdapter : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    private val items= mutableListOf<String>()

    var onItemCloseFunction:((filter: String)->Unit)? = null

    inner class ViewHolder(val binding: RvFilterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.chip.apply {
            text= item
            setOnCloseIconClickListener {
                onItemCloseFunction?.invoke(item)
            }
        }
    }

    fun setFilters(filters: SearchFilters) {
        items.clear()
        filters.category?.let {
            items.add(it.title)
        }

        if (filters.minPrice!=null || filters.maxPrice!=null)
            items.add("قیمت")

        filters.cities?.let {
            it.forEach{c->
                items.add(c.title)
            }
        }
        notifyDataSetChanged()
    }

}