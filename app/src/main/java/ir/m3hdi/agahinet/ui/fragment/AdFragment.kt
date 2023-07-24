package ir.m3hdi.agahinet.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentAdBinding
import ir.m3hdi.agahinet.ui.activity.ImageViewerActivity
import ir.m3hdi.agahinet.ui.adapter.ImagesSliderAdapter
import ir.m3hdi.agahinet.ui.viewmodel.AdViewModel
import ir.m3hdi.agahinet.ui.viewmodel.AdViewModel.UiEvent.BookmarkSetOK
import ir.m3hdi.agahinet.ui.viewmodel.AdViewModel.UiEvent.FailedToSetBookmark
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.ui.viewmodel.NavigationViewModel
import ir.m3hdi.agahinet.util.AppUtils.Companion.formatPrice
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AdFragment : Fragment() {

    private var _binding: FragmentAdBinding? = null
    private val binding get() = _binding!!

    private val viewModel:AdViewModel by viewModels()
    private val citiesViewModel:CitiesViewModel by activityViewModels()
    private val navigationViewModel:NavigationViewModel by activityViewModels()

    private val args: AdFragmentArgs by navArgs()

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
            viewModel.initAd(args.ad)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModelObservers()
    }


    private fun setupUI(){

        val ad = viewModel.ad

        with(binding){
            textViewTitle.text = ad.title
            textViewDescription.text = ad.description
            textViewTimeAndLoc.text = citiesViewModel.getTimeAndLocText(ad)
            textViewPrice.text = formatPrice(ad.price)
            textViewCategory.text= viewModel.getCategoryTitle()
        }


        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkboxBookmark.setOnCheckedChangeListener{_, isChecked ->
                setBookmarked(isChecked)
        }

        /*binding.buttonShare.setOnClickListener {

        }*/
        binding.extendedFabChat.setOnClickListener {

        }
        binding.extendedFabContactInfo.setOnClickListener {
            val modalBottomSheet = ContactInfoModal()
            val args = Bundle()
            args.putInt("uid", ad.creator)
            modalBottomSheet.arguments=args
            modalBottomSheet.onBookmarkFunc = {
                setBookmarked(true)
            }
            modalBottomSheet.show(childFragmentManager, ContactInfoModal.TAG)
        }
    }

    private fun setupViewModelObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.images.collect{

                        if (it.isNotEmpty()){
                            binding.dotsIndicator.isVisible=true
                            binding.noimagePlaceholder.isGone=true

                            val imagesAdapter=ImagesSliderAdapter(it)
                            binding.viewPagerImages.adapter=imagesAdapter
                            binding.viewPagerImages.currentItem = viewModel.selectedImagePosition
                            binding.dotsIndicator.attachTo(binding.viewPagerImages)
                            imagesAdapter.onClickListener = {
                                val intent = Intent(activity,ImageViewerActivity::class.java)
                                intent.putStringArrayListExtra("images", ArrayList(it))
                                startActivity(intent)
                            }

                            pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    viewModel.selectedImagePosition = position
                                }
                            }

                            binding.viewPagerImages.registerOnPageChangeCallback(pageChangeCallback)

                        }
                        else{
                            binding.dotsIndicator.isGone=true
                            binding.noimagePlaceholder.isVisible=true
                        }

                    }
                }

                launch {
                    viewModel.uiEventFlow.collect{
                        when(it){
                            is BookmarkSetOK -> {
                                val messageResId = if (it.bookmark) R.string.bookmarked_message else R.string.unbookmarked_message
                                Toasty.info(requireContext(), getString(messageResId), Toast.LENGTH_SHORT, false).show()
                            }
                            is FailedToSetBookmark -> {
                                binding.checkboxBookmark.isChecked = !binding.checkboxBookmark.isChecked
                                if (it.showToast)
                                    Toasty.error(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT, false).show()
                            }
                            is AdViewModel.UiEvent.AuthenticationRequired -> {
                                navigationViewModel.goToAuthScreen()
                                Toasty.warning(requireContext(), getString(R.string.authentication_required), Toast.LENGTH_SHORT, false).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.bookmarkState.collect{
                        binding.checkboxBookmark.isChecked = it
                    }
                }
            }
        }
    }

    private fun setBookmarked(bookmark:Boolean){
        if (viewModel.bookmarkState.value != bookmark)
            viewModel.setBookmarked(bookmark)
    }
    override fun onDestroyView() {
        if (::pageChangeCallback.isInitialized){
            binding.viewPagerImages.unregisterOnPageChangeCallback(pageChangeCallback)
        }
        super.onDestroyView()
        _binding = null
    }
}