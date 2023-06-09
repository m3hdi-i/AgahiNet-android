package ir.m3hdi.agahinet.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.VpImageBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImagesSliderAdapter(private val images:List<String>) : RecyclerView.Adapter<ImagesSliderAdapter.ViewHolder>() {

    var onClickListener:(()->Unit)?=null

    inner class ViewHolder(val binding: VpImageBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(VpImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ImagesSliderAdapter.ViewHolder, position: Int) {

        with (holder.binding){
            imageView.load(images[position]){
                target(onStart = {
                    progressBar.isVisible = true
                }, onSuccess = {
                    progressBar.isGone=true
                    imageView.setImageDrawable(it)
                }, onError = {
                    progressBar.isGone = true
                })
            }
            imageView.setOnClickListener {
                onClickListener?.invoke()
            }
        }
    }




}
