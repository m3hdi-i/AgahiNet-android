package ir.m3hdi.agahinet.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentCitiesBinding
import ir.m3hdi.agahinet.ui.adapter.CitiesAdapter
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class CitiesFragment : Fragment() {

    private var _binding: FragmentCitiesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()
    private val citiesViewModel: CitiesViewModel by activityViewModels()

    private var _citiesAdapter:CitiesAdapter?=null
    private val citiesAdapter get() = _citiesAdapter!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.tempCurrentProvince.collect{
                        binding.textViewSelectedProvince.text=it.title

                        if (it.cityId==0){
                            binding.textViewAllCitiesOfIran.isGone=false
                            binding.buttonSelectAll.isEnabled=false
                            citiesAdapter.clear()

                        }else{
                            binding.textViewAllCitiesOfIran.isGone=true
                            binding.buttonSelectAll.isEnabled=true

                            val cities = citiesViewModel.getCitiesOfProvince(it.cityId)

                            viewModel.tempCities?.let { selecteds ->
                                citiesAdapter.setCities(cities, selecteds)
                            } ?: citiesAdapter.setCities(cities)

                        }
                    }
                }

                launch {
                    citiesAdapter.selectAllButtonBehavior.collect{
                        updateSelectAllButton(it)
                    }
                }
            }
        }
    }

    private fun setupUI()
    {
        _citiesAdapter = CitiesAdapter()
        binding.recyclerViewCities.adapter=citiesAdapter

        val goBack={ findNavController().popBackStack() }
        binding.topAppBar.setNavigationOnClickListener{
            goBack()
        }
        binding.fabOk.setOnClickListener {
            val selectedCities= citiesAdapter.getSelectedCities()
            if (viewModel.tempCurrentProvince.value.cityId!=0 && selectedCities.isEmpty()){
                Toasty.warning(requireContext(), getString(R.string.select_at_least_one_city), Toast.LENGTH_SHORT).show()
            }else
            {
                viewModel.tempCities = selectedCities.ifEmpty { null }
                goBack()
            }

        }

        setupSelectProvinceModal()
    }

    private val selectAllIcon by lazy { ContextCompat.getDrawable(requireContext(), R.drawable.ic_select_all) }
    private val selectAllText by lazy { resources.getString(R.string.select_all) }
    private val deSelectAllIcon by lazy {  ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear) }
    private val deSelectAllText by lazy { resources.getString(R.string.deselect_all) }

    private fun updateSelectAllButton(selectAllBehavior:Boolean)
    {

        with(binding.buttonSelectAll){
            setOnClickListener(null)
            if (selectAllBehavior){
                icon= selectAllIcon
                text= selectAllText
                setOnClickListener {
                    citiesAdapter.setCheckedAll(true)
                }
            }
            else {
                icon= deSelectAllIcon
                text= deSelectAllText
                setOnClickListener {
                    citiesAdapter.setCheckedAll(false)
                }
            }
        }
    }

    private fun setupSelectProvinceModal(){

        binding.buttonSelectProvince.setOnClickListener {

            val modalBottomSheet = ProvinceSelectModal().apply {
                provinces=citiesViewModel.allProvinces.value
                onProvinceSelectedListener={
                    viewModel.setTempCurrentProvince(it)
                }
            }
            modalBottomSheet.show(childFragmentManager, ProvinceSelectModal.TAG)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}