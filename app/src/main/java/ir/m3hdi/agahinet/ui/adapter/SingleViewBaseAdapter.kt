package ir.m3hdi.agahinet.ui.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class SingleViewBaseAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    var onClickListener:(()->Unit)?=null

    private val items = mutableListOf<Unit>()
    override fun getItemCount(): Int = items.size

    fun showView(show:Boolean){
        if (show){
            if (items.isEmpty()){
                items.add(Unit)
                notifyItemInserted(0)
            }
        }else{
            if (items.isNotEmpty()){
                items.clear()
                notifyItemRemoved(0)
            }
        }
    }
}