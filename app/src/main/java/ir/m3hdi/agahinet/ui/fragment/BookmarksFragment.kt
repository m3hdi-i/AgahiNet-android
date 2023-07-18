package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentBookmarksBinding
import ir.m3hdi.agahinet.domain.model.onFailure
import ir.m3hdi.agahinet.domain.model.onLoading
import ir.m3hdi.agahinet.domain.model.onSuccess
import ir.m3hdi.agahinet.ui.adapter.AdManageAdapter
import ir.m3hdi.agahinet.ui.viewmodel.BookmarksViewModel
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel

@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarksViewModel by viewModels()
    private val citiesViewModel: CitiesViewModel by activityViewModels()

    private val adAdapter: AdManageAdapter by lazy { AdManageAdapter(citiesViewModel, editable = false) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModelObservers()
    }

    private fun setupUI(){

        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.seed))
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getBookmarks()
        }

        binding.recyclerViewAds.adapter = adAdapter
        binding.recyclerViewAds.setHasFixedSize(true)
        adAdapter.onItemClickFunction = {
            val action = BookmarksFragmentDirections.actionBookmarksToAd(it)
            findNavController().navigate(action)
        }
        adAdapter.onItemDeleteFunction = {
            MaterialAlertDialogBuilder(requireContext(), R.style.materialDialogStyle).setTitle(resources.getString(R.string.delete_ad))
                .setMessage(resources.getString(R.string.delete_confirm))
                .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                    viewModel.deleteBookmark(it)
                    dialog.dismiss()
                }
                .show()
        }
        binding.topAppBar.setNavigationOnClickListener{
            findNavController().popBackStack()
        }

    }

    private fun setupViewModelObservers() {

        viewModel.bookmarks.observe(viewLifecycleOwner){
            it.onSuccess {ads->
                adAdapter.setItems(ads)
                binding.textViewNoItems.isGone = ads.isNotEmpty()
                binding.swipeRefreshLayout.isRefreshing = false
            }.onLoading{
                binding.swipeRefreshLayout.isRefreshing = true
            }.onFailure {
                Toasty.error(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT, false).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewAds.adapter=null
        _binding = null
    }
}