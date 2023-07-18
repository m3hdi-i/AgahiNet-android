package ir.m3hdi.agahinet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.databinding.RvRetryBinding

class RetryAdapter : SingleViewBaseAdapter<RetryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RvRetryBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvRetryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.buttonRetry.setOnClickListener {
            onClickListener?.invoke()
        }
    }

}