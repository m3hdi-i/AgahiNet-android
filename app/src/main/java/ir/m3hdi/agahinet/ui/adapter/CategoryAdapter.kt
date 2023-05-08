package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.model.Category
import ir.m3hdi.agahinet.databinding.RvCategoryBinding

class CategoryAdapter(private val items:List<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var onItemClickFunction:((category: Category)->Unit)? = null

    inner class ViewHolder(val binding: RvCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding){

            items[position].let { cat->

                buttonCategory.text=cat.title
                buttonCategory.icon= ContextCompat.getDrawable(buttonCategory.context, R.drawable.ic_category)
                buttonCategory.setOnClickListener { onItemClickFunction?.invoke(cat) }
            }



        }
    }

}