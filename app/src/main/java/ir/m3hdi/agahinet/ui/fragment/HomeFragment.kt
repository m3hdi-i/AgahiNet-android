package ir.m3hdi.agahinet.ui.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.di.App
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.AppUtils.Companion.dpToPx
import ir.m3hdi.agahinet.util.AppUtils.Companion.ioOnUi
import ir.m3hdi.agahinet.util.CustomDividerItemDecoration
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var concatAdapter:ConcatAdapter
    private lateinit var adAdapter:AdAdapter
    private lateinit var progressAdapter:ProgressAdapter

    private val rvScrollPublishSubject=PublishSubject.create<Int>()
    private val rxCompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
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

        setupAdRv()

        binding.buttonSetFilters.setOnClickListener {
            fetchNextPage()
        }

        val earlyPixelsToFetchNewData= dpToPx(requireContext(),32)

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            val end = v.getChildAt(0).measuredHeight - v.measuredHeight
            val isLoading = viewModel.nextPage.value.isLoading

            if ( (scrollY >= end-earlyPixelsToFetchNewData) && scrollY > oldScrollY && !isLoading && !viewModel.isLastPage) {
                rvScrollPublishSubject.onNext(scrollY)
            }
        })

        val disposable= rvScrollPublishSubject.ioOnUi().throttleFirst(1, TimeUnit.SECONDS).subscribe{
            fetchNextPage()
        }
        rxCompositeDisposable.add(disposable)
    }

    private fun setupAdRv()
    {
        adAdapter=AdAdapter()
        adAdapter.items=viewModel.adItems
        progressAdapter=ProgressAdapter()
        concatAdapter = ConcatAdapter(adAdapter)
        val layoutManager=LinearLayoutManager(context)
        binding.recyclerViewAds.layoutManager = layoutManager
        binding.recyclerViewAds.adapter = concatAdapter
        //binding.recyclerViewAds.setHasFixedSize(true)
        binding.recyclerViewAds.addItemDecoration(CustomDividerItemDecoration(requireContext(), layoutManager.orientation,isShowInLastItem = false))

        adAdapter.setOnItemClickListener {
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
                            adAdapter.insertItems(it.value)
                        }
                        is Resultx.Failure->{
                            Toasty.error(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT,false).show()
                            concatAdapter.removeAdapter(progressAdapter)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                adAdapter.submitItems(viewModel.adItems)
            }
        }


        viewModel.scrollState?.let {
            binding.nestedScrollView.scrollY=it
            //binding.nestedScrollView.scrollTo(0,it)
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

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.scrollState = binding.nestedScrollView.scrollY
        super.onSaveInstanceState(outState)
    }


}