package ir.m3hdi.agahinet.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.FilterAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val adAdapter:AdAdapter by lazy { AdAdapter() }


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private val viewModel: HomeViewModel by activityViewModels()


    private lateinit var concatAdapter:ConcatAdapter
    private lateinit var progressAdapter:ProgressAdapter

    private lateinit var filtersAdapter:FilterAdapter

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set enter/exit transitions
        /*exitTransition = AppUtils.getMaterialSharedAxisZTransition(true)
        reenterTransition = AppUtils.getMaterialSharedAxisZTransition(false)*/
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModelObservers()

        // Start app with showing all recent ads
        viewModel.doNewSearch("")
    }

    private fun setupUI(){

        setupAdsRv()
        setupFiltersRv()

        binding.buttonSetFilters.setOnClickListener {
            viewModel.fillTempFilters()
            findNavController().navigate(
                R.id.action_home_to_filters)
        }

        inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // When user types query and hits OK, Hide the keyborad and clear focus of editText
        binding.editTextSearch.setOnEditorActionListener { view, actionId, _ ->
            view.clearFocus()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Hide the keyboard
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.editTextSearch.doOnTextChanged { text, start, before, count ->
            viewModel.doNewSearch(text.toString())
            //binding.recyclerViewAds.smoothScrollToPosition(0)

        }

    }

    private fun setupAdsRv()
    {
        progressAdapter=ProgressAdapter()
        concatAdapter = ConcatAdapter(adAdapter)
        binding.recyclerViewAds.adapter = concatAdapter
        binding.recyclerViewAds.setHasFixedSize(true)
        adAdapter.onItemClickFunction = {
            Toasty.info(requireContext(),"...",Toast.LENGTH_SHORT,false).show()
        }

    }

    private fun setupFiltersRv()
    {
        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewFilters, false)
        // TODO : Use DiffUtil for this RV
        filtersAdapter=FilterAdapter()
        binding.recyclerViewFilters.adapter=filtersAdapter
        filtersAdapter.onItemCloseFunction = {
            Toasty.info(requireContext(),"closed",Toast.LENGTH_SHORT,false).show()
            //viewModel.setCategory(null)
        }
    }

    private fun setupViewModelObservers(){

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                /*launch {

                    viewModel.nextPage.drop(1).collect {
                        when(it){
                            is Resultx.Loading->{
                                if (progressAdapter !in concatAdapter.adapters)
                                    concatAdapter.addAdapter(1,progressAdapter)
                            }
                            is Resultx.Success->{
                                if (progressAdapter in concatAdapter.adapters)
                                    concatAdapter.removeAdapter(progressAdapter)

                                if (it.value.isEmpty() && viewModel.isLastPage)
                                {
                                    // No results for this search at all
                                    Toasty.info(requireContext(), getString(R.string.no_result_for_this_search), Toast.LENGTH_SHORT,false).show()

                                }else
                                {
                                    // Update recyclerView with new page
                                    adAdapter.notifyPageInserted(it.value.size)
                                }

                            }
                            is Resultx.Failure->{
                                if (progressAdapter in concatAdapter.adapters)
                                    concatAdapter.removeAdapter(progressAdapter)
                                Toasty.error(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT,false).show()
                            }
                            else -> {}
                        }
                    }
                }*/

                launch {
                    viewModel.pagingDataFlow.collectLatest{
                        adAdapter.submitData(it)
                    }
                }


            }
        }

        viewModel.filters.observe(viewLifecycleOwner) {
            filtersAdapter.setFilters(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewAds.adapter=null
        _binding = null
    }


}