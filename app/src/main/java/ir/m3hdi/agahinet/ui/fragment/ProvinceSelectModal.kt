package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.ui.adapter.ProvinceAdapter

class ProvinceSelectModal : BottomSheetDialogFragment() {

    var onProvinceSelectedListener:((City)->Unit)?=null
    lateinit var provinces:List<City>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    = inflater.inflate(R.layout.modal_bottom_sheet_province, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val provincesRv=view.findViewById<RecyclerView>(R.id.recyclerView_provinces)
        val adapter=ProvinceAdapter(provinces)
        provincesRv.adapter=adapter
        adapter.onItemClickFunction={
            onProvinceSelectedListener?.invoke(it)
            dismiss()
        }

    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}