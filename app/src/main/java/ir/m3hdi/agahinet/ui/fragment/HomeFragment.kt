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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.domain.model.NetworkException
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.FilterAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.viewmodel.DEBOUNCE_TIMEOUT_MS
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
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
        }

        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.seed))

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
        //adAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerViewAds.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val scrollPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                binding.swipeRefreshLayout.isEnabled = scrollPosition == 0
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            adAdapter.refresh()
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

                launch {
                    viewModel.pagingDataFlow.collectLatest{
                        adAdapter.submitData(viewLifecycleOwner.lifecycle,it)
                    }
                }

                launch {
                    adAdapter.loadStateFlow.collect{loadState->

                        // handle empty list
                        val isListEmpty = loadState.refresh is LoadState.NotLoading && adAdapter.itemCount == 0
                        binding.textViewNoResults.isVisible=isListEmpty

                        // Show loading spinner during initial load or refresh.
                        handleLoading(loadState.source.refresh is LoadState.Loading)

                         if (loadState.append is LoadState.Loading){
                             if (progressAdapter !in concatAdapter.adapters)
                                 concatAdapter.addAdapter(1,progressAdapter)
                         }else{
                             if (progressAdapter in concatAdapter.adapters)
                                 concatAdapter.removeAdapter(progressAdapter)
                         }

                        // Only show the list if refresh succeeds.
                        //binding.recyclerViewAds.isVisible = loadState.source.refresh is LoadState.NotLoading

                        /**
                         * loadState.refresh - represents the load state for loading the PagingData for the first time.
                         * loadState.prepend - represents the load state for loading data at the start of the list.
                         * loadState.append - represents the load state for loading data at the end of the list.
                         * */
                        // If we have an error, show a toast
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
                    }
                }

                // Just for scrolling RV to top when searching new query
                launch {
                    adAdapter.loadStateFlow
                        .distinctUntilChanged { old, new ->
                            old.prepend.endOfPaginationReached ==
                                    new.prepend.endOfPaginationReached }
                        .filter { it.refresh is LoadState.NotLoading && it.prepend.endOfPaginationReached}
                        .collect {
                            //binding.appBarLayout.isLifted=false
                            binding.recyclerViewAds.scrollToPosition(0)
                        }
                }



            }
        }

        viewModel.filters.observe(viewLifecycleOwner) {
            filtersAdapter.setFilters(it)
        }

    }

    private fun handleLoading(loading: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = loading == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewAds.adapter=null
        _binding = null
    }


}