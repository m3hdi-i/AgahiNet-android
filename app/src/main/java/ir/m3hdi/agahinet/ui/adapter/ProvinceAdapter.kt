package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.domain.model.Category
import ir.m3hdi.agahinet.databinding.RvCategoryBinding
import ir.m3hdi.agahinet.databinding.RvProvinceBinding
import ir.m3hdi.agahinet.util.AppUtils.Companion.dpToPx
import kotlin.properties.Delegates

class ProvinceAdapter(private val items:List<City>) : RecyclerView.Adapter<ProvinceAdapter.ViewHolder>() {

    var sheetPadding by Delegates.notNull<Int>()

    var onItemClickFunction:((provinceId: City)->Unit)? = null

    inner class ViewHolder(val binding: RvProvinceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        sheetPadding= dpToPx(parent.context,24)
        return ViewHolder(RvProvinceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding){

            items[position].let {p->
                textViewProvince.text = p.title
                divider.isGone = position == itemCount-1
                container.setOnClickListener { onItemClickFunction?.invoke(p) }

                with(container){
                    if (position==0 ){
                        setPadding(0,sheetPadding,0,0)
                    }else if (position == itemCount-1){
                        setPadding(0,0,0,sheetPadding)
                    }else{
                        setPadding(0,0,0,0)
                    }
                }

            }

        }
    }

}