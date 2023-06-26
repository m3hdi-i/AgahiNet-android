package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentNewAdBinding
import ir.m3hdi.agahinet.ui.adapter.AdManageAdapter
import ir.m3hdi.agahinet.ui.adapter.NewAdImagesAdapter
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.ui.viewmodel.NewAdViewModel
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.GridAutoFitLayoutManager
import kotlinx.coroutines.launch

class NewAdFragment : Fragment() {

    private var _binding: FragmentNewAdBinding? = null
    private val binding get() = _binding!!

    private val citiesViewModel: CitiesViewModel by activityViewModels()
    private val imagesAdapter: NewAdImagesAdapter by lazy { NewAdImagesAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    private fun setupUI() {

        //ViewCompat.setNestedScrollingEnabled(binding.layoutContent,false)


        with(binding.recyclerViewImages){
            layoutManager= GridLayoutManager(requireContext(),2).apply {
                orientation = GridLayoutManager.VERTICAL
            }
            adapter = imagesAdapter
        }


    }

    private fun setupViewModelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                AppUtils.currentUser.collect {
                    AppUtils.handleNeedAuthFragment(it != null, childFragmentManager, binding.layoutParent, binding.layoutNeedAuth, binding.layoutContent)
                }
            }
        }
    }


        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}