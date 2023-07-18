package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.databinding.BtnSetFiltersBinding

class BtnSetFiltersAdapter : SingleViewBaseAdapter<BtnSetFiltersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BtnSetFiltersBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BtnSetFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.buttonSetFilters.setOnClickListener { onClickListener?.invoke() }
    }
}