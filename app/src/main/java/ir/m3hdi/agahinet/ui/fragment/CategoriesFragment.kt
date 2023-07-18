package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentCategoriesBinding
import ir.m3hdi.agahinet.databinding.FragmentHomeBinding
import ir.m3hdi.agahinet.ui.adapter.CategoryAdapter
import ir.m3hdi.agahinet.ui.adapter.FilterAdapter
import ir.m3hdi.agahinet.ui.viewmodel.HomeViewModel
import ir.m3hdi.agahinet.util.Constants.Companion.CATEGORIES
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener{
            findNavController().popBackStack()
        }

        val catsAdapter=CategoryAdapter(CATEGORIES)
        binding.recyclerViewCategories.adapter=catsAdapter
        catsAdapter.onItemClickFunction={category->
            viewModel.tempCategory = if (category.id == 0) null else category
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}