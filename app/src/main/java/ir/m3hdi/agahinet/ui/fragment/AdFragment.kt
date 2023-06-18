package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ir.m3hdi.agahinet.databinding.FragmentAdBinding
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.ui.adapter.ImagesSliderAdapter
import ir.m3hdi.agahinet.util.AppUtils


class AdFragment : Fragment() {

    private var _binding: FragmentAdBinding? = null
    private val binding get() = _binding!!

    private val args: AdFragmentArgs by navArgs()
    private lateinit var ad:Ad

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ad=args.ad
        setupUI()
    }

    private fun setupUI(){

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkboxBookmark.setOnCheckedChangeListener { checkBox, isChecked ->

        }
        binding.buttonShare.setOnClickListener {

        }
        binding.extendedFabChat.setOnClickListener {

        }
        binding.extendedFabContactInfo.setOnClickListener {
            val modalBottomSheet = ContactInfoModal()
            modalBottomSheet.show(childFragmentManager, ProvinceSelectModal.TAG)
        }

        val i= AppUtils.getImageUrlByImageId(ad.mainImageId!!)
        val images=listOf(i,i,i)
        binding.viewPagerImages.adapter=ImagesSliderAdapter(images)
        binding.dotsIndicator.attachTo(binding.viewPagerImages)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}