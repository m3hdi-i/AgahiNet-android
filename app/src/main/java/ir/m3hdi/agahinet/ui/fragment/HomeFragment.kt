package ir.m3hdi.agahinet.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.model.AdFilters
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.FilterAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.AppUtils.Companion.ioOnUi
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var concatAdapter:ConcatAdapter
    private lateinit var adAdapter:AdAdapter
    private lateinit var progressAdapter:ProgressAdapter

    private val rvScrollPublishSubject=PublishSubject.create<Boolean>()
    private val rxCompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        arguments?.getInt("category_id")?.let {
            Toasty.info(requireContext(),"cat $it",Toast.LENGTH_SHORT,false).show()
        }
    }


    private fun setupUI(){

        binding.searchView
            .editText
            .setOnEditorActionListener { v, actionId, event ->
                binding.searchBar.text = binding.searchView.text
                binding.searchView.hide()
                false
            }

        /*binding.searchView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom -> // Get the height of the SearchView's inner EditText
            //val kha = binding.aaa.height

            // Set the SearchView's height to the sum of the EditText's height and some extra padding
            binding.searchView.layoutParams.height = binding.appBarLayout.height - binding.layoutFilters.height -binding.searchBar.baseline
            binding.searchView.requestLayout()
        }*/

        setupAdsRv()
        setupFiltersRv()

        binding.buttonSetCategories.setOnClickListener {
            val bundle = bundleOf("origin" to "home")

            findNavController().navigate(
                R.id.action_home_to_category,
                bundle)
        }

        binding.buttonSetFilters.setOnClickListener {
            binding.appBarLayout.isLifted = false
            if (viewModel.adItems.size>0){
                adAdapter.clearItems()
            }

            val filters=AdFilters()
            viewModel.doNewSearch(filters)
        }

        binding.recyclerViewAds.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager=recyclerView.layoutManager as LinearLayoutManager
                if(dy > 0 && layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount-1 ){
                    // We have reached the end of the recycler view.
                    val isLoading = viewModel.nextPage.value.isLoading
                    if ( !isLoading && !viewModel.isLastPage) {
                        rvScrollPublishSubject.onNext(true)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })


        val disposable= rvScrollPublishSubject.ioOnUi().throttleFirst(2, TimeUnit.SECONDS).subscribe{
            fetchNextPage()
        }
        rxCompositeDisposable.add(disposable)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.nextPage.drop(1).collect {
                    when(it){
                        is Resultx.Loading->{
                            concatAdapter.addAdapter(1,progressAdapter)
                        }
                        is Resultx.Success->{
                            concatAdapter.removeAdapter(progressAdapter)
                            if (it.value.isEmpty() && viewModel.isLastPage)
                            {
                                // No results for this search at all
                                Toasty.info(requireContext(), getString(R.string.no_result_for_this_search), Toast.LENGTH_SHORT,false).show()

                            }else
                            {
                                // Update recyclerView with new results
                                adAdapter.notifyPageInserted(it.value.size)
                            }

                        }
                        is Resultx.Failure->{
                            concatAdapter.removeAdapter(progressAdapter)
                            Toasty.error(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT,false).show()
                        }
                    }
                }
            }
        }

    }

    private fun setupFiltersRv()
    {
        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewFilters, false)
        // TODO : Use DiffUtil for this RV
        val filterAdapter=FilterAdapter()
        binding.recyclerViewFilters.adapter=filterAdapter
        filterAdapter.onItemCloseFunction = {
            Toasty.info(requireContext(),"closed",Toast.LENGTH_SHORT,false).show()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.filters.collect {
                    filterAdapter.setFilters(it)
                }
            }
        }



    }

    private fun fetchNextPage(){
        if (AppUtils.hasInternetConnection(requireContext())){
            viewModel.fetchNextPage()
        }else{
            Toasty.warning(requireContext(), getString(R.string.no_network), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        rxCompositeDisposable.clear()
    }

}