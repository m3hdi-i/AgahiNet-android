package ir.m3hdi.agahinet.ui.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.AppUtils.Companion.ioOnUi
import ir.m3hdi.agahinet.util.CustomDividerItemDecoration
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random


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

        binding.recyclerViewAds.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager=recyclerView.layoutManager as LinearLayoutManager
                if(layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount-1){
                    // We have reached the end of the recycler view.
                    val isLoading = viewModel.nextPage.value.isLoading
                    if ( !isLoading && !viewModel.isLastPage) {
                        rvScrollPublishSubject.onNext(true)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
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
        adAdapter.items=viewModel.adItems // Pass a refrence of ad items in my viewModel to the adapter
        progressAdapter=ProgressAdapter()
        concatAdapter = ConcatAdapter(adAdapter)
        binding.recyclerViewAds.adapter = concatAdapter
        binding.recyclerViewAds.setHasFixedSize(true)
        val layoutManager=binding.recyclerViewAds.layoutManager as LinearLayoutManager
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
                            adAdapter.notifyPageInserted(it.value.size)
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