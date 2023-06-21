package ir.m3hdi.agahinet.ui.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import ir.m3hdi.agahinet.databinding.FragmentAdBinding
import ir.m3hdi.agahinet.ui.activity.ImageViewerActivity
import ir.m3hdi.agahinet.ui.adapter.ImagesSliderAdapter
import ir.m3hdi.agahinet.ui.viewmodel.AdViewModel
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.util.AppUtils.Companion.formatPrice
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdFragment : Fragment() {

    private var _binding: FragmentAdBinding? = null
    private val binding get() = _binding!!

    private val viewModel:AdViewModel by viewModels()
    private val citiesViewModel:CitiesViewModel by activityViewModels()

    private val args: AdFragmentArgs by navArgs()

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
            viewModel.ad=args.ad
            viewModel.getImages()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

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
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkboxBookmark.setOnCheckedChangeListener { _, isChecked ->
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

    private fun setBookmarked(bookmark:Boolean){
        // set bookmark state of ad
    }
    override fun onDestroyView() {
        if (::pageChangeCallback.isInitialized){
            binding.viewPagerImages.unregisterOnPageChangeCallback(pageChangeCallback)
        }
        super.onDestroyView()
        _binding = null
    }
}