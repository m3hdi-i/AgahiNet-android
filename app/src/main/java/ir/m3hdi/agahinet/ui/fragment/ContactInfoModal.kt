package ir.m3hdi.agahinet.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.m3hdi.agahinet.databinding.ModalBottomSheetContactInfoBinding
import ir.m3hdi.agahinet.ui.viewmodel.ContactInfoViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.number


@AndroidEntryPoint
class ContactInfoModal : BottomSheetDialogFragment() {

    private val viewModel: ContactInfoViewModel by viewModels()

    private var _binding: ModalBottomSheetContactInfoBinding? = null
    private val binding get() = _binding!!

    var onBookmarkFunc:(()->Unit)?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt("uid")?.let {
            viewModel.getContactInfo(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ModalBottomSheetContactInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.contactInfo.collect{
                    it?.let {ci->
                        with(binding){
                            val smsTitle = "ارسال پیامک به " + ci.phoneNumber
                            val callTitle = "تماس تلفنی با " + ci.phoneNumber
                            textViewSms.text = smsTitle
                            textViewCall.text = callTitle

                            content.isVisible=true
                            progressBar.isGone=true

                            textViewSms.setOnClickListener {
                                val intent = Intent(Intent.ACTION_SENDTO)
                                intent.data = Uri.parse("smsto:${ci.phoneNumber}")
                                startActivity(intent)

                                dismiss()
                            }
                            textViewCall.setOnClickListener {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:${ci.phoneNumber}")

                                startActivity(intent)

                                dismiss()
                            }
                            textViewBookmark.setOnClickListener {
                                onBookmarkFunc?.invoke()

                                dismiss()
                            }
                        }
                    }
                }
            }
        }

   }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}