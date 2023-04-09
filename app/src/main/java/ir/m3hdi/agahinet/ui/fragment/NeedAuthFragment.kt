package ir.m3hdi.agahinet.ui.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.m3hdi.agahinet.databinding.FragmentNeedAuthBinding
import ir.m3hdi.agahinet.ui.activity.AuthActivity
import ir.m3hdi.agahinet.ui.viewmodel.AuthViewModel

@AndroidEntryPoint
class NeedAuthFragment : Fragment() {


    private var _binding: FragmentNeedAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNeedAuthBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonGotoAuth.setOnClickListener {
            startActivity(Intent(activity, AuthActivity::class.java))
        }
    }


}