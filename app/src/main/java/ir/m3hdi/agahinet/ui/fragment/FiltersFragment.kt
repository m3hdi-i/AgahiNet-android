package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentFiltersBinding
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.Constants.Companion.CATEGORIES

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_filters_to_home)
        }
        binding.layCat.setOnClickListener {
            findNavController().navigate(R.id.action_filters_to_categories)
        }

        binding.layCity.setOnClickListener {
            findNavController().navigate(R.id.action_filters_to_cities)
        }

        viewModel.filters.observe(viewLifecycleOwner){
            it?.let {
                binding.selectedCategory.text= CATEGORIES.find { s-> s.id == it.category }?.title ?: "کل دسته ها"
            }
        }



    }
}