package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentFiltersBinding
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.AppUtils.Companion.priceToPersianCurrencyLetters

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goBack={ findNavController().popBackStack() }

        binding.topAppBar.setNavigationOnClickListener{
           goBack()
        }
        binding.layCat.setOnClickListener {
            findNavController().navigate(R.id.action_filters_to_categories)
        }

        binding.layCity.setOnClickListener {
            findNavController().navigate(R.id.action_filters_to_cities)
        }

        binding.selectedCategory.text = viewModel.tempCategory?.title ?: requireContext().getString(R.string.all_categories)

        binding.selectedCities.text = viewModel.tempCities?.joinToString("، ") { city -> city.title }
            ?: requireContext().getString(R.string.all_cities_of_iran)

        viewModel.tempMinPrice?.let {min->
            binding.inputMinPrice.setText(min)
            binding.fieldMinPrice.helperText = priceToPersianCurrencyLetters(min)
        }
        viewModel.tempMaxPrice?.let {max->
            binding.inputMaxPrice.setText(max)
            binding.fieldMaxPrice.helperText = priceToPersianCurrencyLetters(max)
        }

        binding.inputMinPrice.doOnTextChanged { text, _, _, _ ->
            binding.fieldMinPrice.helperText = priceToPersianCurrencyLetters(text.toString())
        }
        binding.inputMaxPrice.doOnTextChanged { text, _, _, _ ->
            binding.fieldMaxPrice.helperText = priceToPersianCurrencyLetters(text.toString())
        }

        binding.fabOk.setOnClickListener {

            val minPrice = binding.inputMinPrice.text.toString().takeIf { it.isNotBlank() }?.toLongOrNull()
            val maxPrice = binding.inputMaxPrice.text.toString().takeIf { it.isNotBlank() }?.toLongOrNull()

            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                Toasty.warning(requireContext(), getString(R.string.min_greater_than_max_warning), Toast.LENGTH_LONG).show()
            } else {
                viewModel.tempMinPrice = minPrice?.toString()
                viewModel.tempMaxPrice = maxPrice?.toString()
                viewModel.applyTempFilters()
                goBack()
            }

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}