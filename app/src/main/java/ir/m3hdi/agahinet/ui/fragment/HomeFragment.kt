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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
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
import ir.m3hdi.agahinet.util.AppUtils.Companion.dpToPx
import ir.m3hdi.agahinet.util.AppUtils.Companion.ioOnUi
import ir.m3hdi.agahinet.util.CustomDividerItemDecoration
import ir.m3hdi.agahinet.util.Resultx
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

        viewModel.ads.observe(viewLifecycleOwner){

            when(it){
                is Resultx.Success->{
                    concatAdapter.removeAdapter(progressAdapter)
                    adAdapter.insertAds(it.value)
                }
                is Resultx.Loading->{
                    concatAdapter.addAdapter(1,progressAdapter)
                }
                is Resultx.Failure->{
                    Toasty.error(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT,false).show()
                    concatAdapter.removeAdapter(progressAdapter)
                }
            }

        }


        val earlyPixelsToFetchNewData= dpToPx(requireContext(),32)

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            val end = v.getChildAt(0).measuredHeight - v.measuredHeight
            val isLoading =viewModel.ads.value?.isLoading ?: false

            if ( (scrollY >= end-earlyPixelsToFetchNewData) && scrollY > oldScrollY && !isLoading && !viewModel.isLastPage) {
                rvScrollPublishSubject.onNext(scrollY)
            }
        })

        val a= rvScrollPublishSubject.ioOnUi().throttleFirst(2, TimeUnit.SECONDS).subscribe{
            fetchNextPage()
        }
        rxCompositeDisposable.add(a)

    }


    private fun setupAdRv()
    {
        adAdapter=AdAdapter()
        progressAdapter=ProgressAdapter()
        concatAdapter = ConcatAdapter(adAdapter)
        val layoutManager=LinearLayoutManager(context)
        binding.recyclerViewAds.layoutManager = layoutManager
        binding.recyclerViewAds.adapter = concatAdapter
        binding.recyclerViewAds.addItemDecoration(CustomDividerItemDecoration(requireContext(), layoutManager.orientation,isShowInLastItem = false))

        adAdapter.setOnItemClickListener {
            Toast.makeText(context,"...",Toast.LENGTH_SHORT).show()
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
        Log.e("000","0000000000000000000000000000000")
    }

    override fun onStart() {
        super.onStart()
    }


}