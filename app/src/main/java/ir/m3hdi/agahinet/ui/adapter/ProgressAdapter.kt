package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.databinding.RvProgressBinding

class ProgressAdapter() : RecyclerView.Adapter<ProgressAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RvProgressBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

}