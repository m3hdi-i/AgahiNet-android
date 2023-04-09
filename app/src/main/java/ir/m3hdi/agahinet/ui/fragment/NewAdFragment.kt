package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ir.m3hdi.agahinet.databinding.FragmentNewAdBinding
import ir.m3hdi.agahinet.ui.viewmodel.NewAdViewModel
import ir.m3hdi.agahinet.util.AppUtils

class NewAdFragment : Fragment() {

    private var _binding: FragmentNewAdBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(NewAdViewModel::class.java)

        _binding = FragmentNewAdBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        AppUtils.manageNeedAuthFragment(childFragmentManager,binding.layoutParent,binding.layoutNeedAuth,binding.layoutContent)
    }
}