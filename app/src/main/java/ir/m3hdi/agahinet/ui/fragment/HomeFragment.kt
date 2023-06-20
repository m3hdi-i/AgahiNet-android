package ir.m3hdi.agahinet.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.domain.model.NetworkException
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.BtnSetFiltersAdapter
import ir.m3hdi.agahinet.ui.adapter.FilterAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.adapter.RetryAdapter
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private val citiesViewModel: CitiesViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val adAdapter:AdAdapter by lazy { AdAdapter(citiesViewModel) }

    private lateinit var concatAdapter:ConcatAdapter
    private lateinit var pagingProgressBarAdapter:ProgressAdapter
    private lateinit var headerAdapter:RetryAdapter
    private lateinit var footerAdapter:RetryAdapter

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

    }

    private fun setupUI(){

        setupAdsRv()
        setupFiltersRv()

        inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // When user types query and hits OK, Hide the keyborad and clear focus of editText
        binding.editTextSearch.setOnEditorActionListener { view, actionId, _ ->
            view.clearFocus()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.editTextSearch.doOnTextChanged { text, start, before, count ->
            viewModel.setSearchQuery(text.toString())
        }

        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.seed))
    }

    private fun setupAdsRv()
    {
        pagingProgressBarAdapter=ProgressAdapter()
        headerAdapter= RetryAdapter()
        footerAdapter = RetryAdapter()
        concatAdapter = ConcatAdapter(headerAdapter,adAdapter,pagingProgressBarAdapter,footerAdapter)
        binding.recyclerViewAds.adapter = concatAdapter
        binding.recyclerViewAds.setHasFixedSize(true)
        adAdapter.onItemClickFunction = {
            val action = HomeFragmentDirections.actionHomeToAd(it)
            findNavController().navigate(action)
        }
        adAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.swipeRefreshLayout.setOnRefreshListener {
            adAdapter.refresh()
        }
        val retryFunc = { adAdapter.retry() }
        headerAdapter.onClickListener = retryFunc
        footerAdapter.onClickListener = retryFunc
    }


    private fun setupFiltersRv()
    {

        filtersAdapter=FilterAdapter()
        val btnSetFiltersAdapter=BtnSetFiltersAdapter()
        binding.recyclerViewFilters.adapter=ConcatAdapter(btnSetFiltersAdapter,filtersAdapter)
        filtersAdapter.onItemCloseFunction = {
            viewModel.clearFilterTag(it)
        }
        btnSetFiltersAdapter.showView(true)
        btnSetFiltersAdapter.onClickListener = {
            viewModel.fillTempFilters()
            findNavController().navigate(
                R.id.action_home_to_filters)
        }
    }

    private fun setupViewModelObservers(){

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // observe paging data
                launch {
                    viewModel.pagingDataFlow.collect{
                        adAdapter.submitData(viewLifecycleOwner.lifecycle,it)
                    }
                }

                // Handle paging events
                launch {
                    adAdapter.loadStateFlow.collect{loadState->

                        // handle empty list
                        val isListEmpty = loadState.refresh is LoadState.NotLoading && adAdapter.itemCount == 0
                        binding.textViewNoResults.isVisible=isListEmpty

                        // Show loading spinner during initial load or refresh.
                        binding.swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading

                        // Show progressBar in bottom while fetching next pages
                        pagingProgressBarAdapter.showView(loadState.append is LoadState.Loading)

                        // Show a toast when we have a error
                        val errorState = when {
                            loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                            else -> null
                        }
                        errorState?.let {
                            val messageResId =
                                if (it.error is NetworkException.NoNetwork) { R.string.no_network_error }
                                else { R.string.network_error }
                            Toasty.error(requireContext(), getString(messageResId), Toast.LENGTH_SHORT, false).show()
                        }

                        // Show retry button on top or bottom, if we have a error
                        val showHeaderRetryButton= loadState.refresh is LoadState.Error
                        headerAdapter.showView(showHeaderRetryButton)
                        if (showHeaderRetryButton)
                            binding.recyclerViewAds.smoothScrollToPosition(0)

                        val showFooterRetryButton=loadState.append is LoadState.Error
                        footerAdapter.showView(showFooterRetryButton)
                        if (showFooterRetryButton)
                            binding.recyclerViewAds.smoothScrollToPosition(concatAdapter.itemCount-1)

                    }
                }

                // Just for scrolling RV to top when searching new query
                launch {
                    adAdapter.loadStateFlow
                        .distinctUntilChanged { old, new ->
                            old.prepend.endOfPaginationReached ==
                                    new.prepend.endOfPaginationReached }
                        .filter { it.refresh is LoadState.NotLoading && it.prepend.endOfPaginationReached}
                        .drop(1)
                        .collect {
                            //binding.appBarLayout.isLifted=false
                            binding.recyclerViewAds.scrollToPosition(0)
                        }
                }

                // Observe filters to show in filters RV
                launch {
                    viewModel.filters.collect{
                        filtersAdapter.setFilters(it)
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewAds.adapter=null
        _binding = null
    }

}