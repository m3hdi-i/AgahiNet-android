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
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ir.m3hdi.agahinet.data.model.Ad
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.ui.adapter.AdAdapter
import ir.m3hdi.agahinet.ui.adapter.ProgressAdapter
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.CustomDividerItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private lateinit var concatAdapter:ConcatAdapter
    private lateinit var adAdapter:AdAdapter
    private lateinit var progressAdapter:ProgressAdapter

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
            insertData()
        }

    }

    var isLoading=false

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

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            println(scrollY.toString() + " : "+ (v.getChildAt(0).measuredHeight - v.measuredHeight).toString())
            val end = v.getChildAt(0).measuredHeight - v.measuredHeight
            if ( (scrollY >= end-50) && !isLoading) {
                isLoading=true
                Log.e("...", "Last Item Wow !")
                insertData()
            }
        })




    }

    var c=0
    private fun insertData(){

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                concatAdapter.addAdapter(1,progressAdapter)
                delay(1000)
                concatAdapter.removeAdapter(progressAdapter)


                val ads= mutableListOf<Ad>()
                for (i in 0..2){
                    val ad = Ad(
                        adId = 56,
                        title = "Default Title $c",
                        description = "Default Description",
                        price = "Not Available",
                        createdAt = "2022-01-01",
                        category = 0,
                        creator = 0,
                        city = 0,
                        mainImageId = null
                    )
                    ads.add(ad)
                    c++
                }

                adAdapter.insertAds(ads)

                isLoading=false
            }

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

    }
}