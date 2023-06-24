package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentUserBinding
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.AppUtils.Companion.currentUser
import kotlinx.coroutines.launch


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                currentUser.collect{
                    AppUtils.handleNeedAuthFragment(it!=null ,childFragmentManager,binding.layoutParent,binding.layoutNeedAuth,binding.layoutContent)

                    it?.let {
                        binding.textViewFullname.text = it.user.fullname
                        binding.textViewPhoneNumber.text = it.user.phoneNumber
                    }
                }
            }
        }

        binding.textViewMyAds.setOnClickListener {
            findNavController().navigate(R.id.action_user_to_my_ads)
        }
        binding.textViewBookmarkeds.setOnClickListener {
            findNavController().navigate(R.id.action_user_to_bookmarks)
        }

        binding.textViewSignout.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext(), R.style.materialDialogStyle).setTitle(resources.getString(R.string.signout))
                .setMessage(resources.getString(R.string.signout_confirm))
                .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                    AppUtils.signOutUser(requireContext())
                    dialog.dismiss()
                }
                .show()

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}