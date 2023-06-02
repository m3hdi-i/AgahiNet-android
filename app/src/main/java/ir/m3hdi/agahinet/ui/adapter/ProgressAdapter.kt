package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.databinding.RvProgressBinding

class ProgressAdapter : SingleViewBaseAdapter<ProgressAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RvProgressBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
}