package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.model.Category
import ir.m3hdi.agahinet.databinding.RvCategoryBinding
import ir.m3hdi.agahinet.databinding.RvCityBinding
import ir.m3hdi.agahinet.databinding.RvProvinceBinding
import ir.m3hdi.agahinet.util.AppUtils.Companion.dpToPx
import kotlin.properties.Delegates

class CitiesAdapter(private val items:List<String>) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    var onItemClickFunction:((province: String)->Unit)? = null

    inner class ViewHolder(val binding: RvCityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding){

            items[position].let { c->
                checkBoxCity.text=c

            }

        }
    }

}