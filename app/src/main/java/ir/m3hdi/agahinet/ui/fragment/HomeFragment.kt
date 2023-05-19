package ir.m3hdi.agahinet.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.FilterAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var concatAdapter:ConcatAdapter
    private lateinit var adAdapter:AdAdapter
    private lateinit var progressAdapter:ProgressAdapter
    private lateinit var filtersAdapter:FilterAdapter

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){

        setupAdsRv()
        setupFiltersRv()
        setupViewModelObservers()

        binding.buttonSetFilters.setOnClickListener {
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


        val adsRvLayoutManager=binding.recyclerViewAds.layoutManager as LinearLayoutManager

        binding.recyclerViewAds.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if(dy > 0 && adsRvLayoutManager.findLastVisibleItemPosition() == adsRvLayoutManager.itemCount-1 ){
                    // We have reached the end of the recycler view.
                    viewModel.fetchNextPage()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        binding.editTextSearch.doOnTextChanged { text, start, before, count ->
            viewModel.search(text.toString())
        }

    }

    private fun setupAdsRv()
    {
        adAdapter=AdAdapter()
        adAdapter.items=viewModel.adItems // Pass a refrence of ad items in my viewModel to the adapter
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
            viewModel.setCategory(null)
        }
    }

    private fun setupViewModelObservers(){

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
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
                }

                launch {
                    viewModel.rvClear.collect{
                        adAdapter.clearItems()
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
        _binding = null
    }


}