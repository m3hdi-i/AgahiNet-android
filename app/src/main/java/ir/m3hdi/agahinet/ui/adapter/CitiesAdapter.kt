package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.databinding.RvCityBinding
import ir.m3hdi.agahinet.util.MutablePair
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CitiesAdapter : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    private var items:MutableList<MutablePair<City,Boolean>> = mutableListOf()

    val selectAllButtonBehavior = MutableStateFlow(true)

    inner class ViewHolder(val binding: RvCityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding){

            items[position].let {
                checkBoxCity.setOnCheckedChangeListener(null)

                checkBoxCity.text=it.first.title
                checkBoxCity.isChecked=it.second

                checkBoxCity.setOnCheckedChangeListener { _, isChecked ->
                    items[position].second=isChecked

                    selectAllButtonBehavior.value = isThereAnyUnselectedItem()

                }
            }

        }
    }

    fun setCities(allCities:List<City>,selectedCities:List<City> = listOf()){
        if (items.isNotEmpty())
            clear()
        items = allCities.map { MutablePair(it, (it in selectedCities) ) }.toMutableList()
        notifyItemRangeInserted(0,itemCount)
        selectAllButtonBehavior.value = isThereAnyUnselectedItem()
    }

    fun getSelectedCities():List<City> = items.filter { it.second }.map { it.first }

    fun setCheckedAll(checked:Boolean){
        items.forEach {
            it.second=checked
        }
        notifyItemRangeChanged(0,itemCount)
        selectAllButtonBehavior.value = isThereAnyUnselectedItem()
    }

    private fun isThereAnyUnselectedItem() = items.any { !it.second }

    fun clear() {
        val count=itemCount
        items.clear()
        notifyItemRangeRemoved(0,count)
    }


}