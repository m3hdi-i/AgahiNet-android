package ir.m3hdi.agahinet.ui.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ir.m3hdi.agahinet.databinding.FragmentAdBinding
import ir.m3hdi.agahinet.domain.model.Ad


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


        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkboxBookmark.setOnCheckedChangeListener { checkBox, isChecked ->
            // Respond to icon toggle
        }
        binding.buttonShare.setOnClickListener {

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}