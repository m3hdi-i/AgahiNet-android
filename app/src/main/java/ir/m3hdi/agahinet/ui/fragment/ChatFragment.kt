package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ir.m3hdi.agahinet.databinding.FragmentChatBinding
import ir.m3hdi.agahinet.ui.components.RtlLayout
import ir.m3hdi.agahinet.ui.theme.AppTheme
import ir.m3hdi.agahinet.ui.viewmodel.ChatViewModel
import ir.m3hdi.agahinet.util.AppUtils
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.layoutContent.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ChatScreen()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                AppUtils.currentUser.collect {
                    AppUtils.handleNeedAuthFragment(true, childFragmentManager, binding.layoutParent, binding.layoutNeedAuth, binding.layoutContent)
                }
            }
        }


    }


    @Composable
    fun ChatScreen(){
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val connectionState by viewModel.connectionStatus.collectAsStateWithLifecycle()

        AppTheme{
            RtlLayout {

                Column(Modifier.fillMaxSize()){

                    Button(onClick = { viewModel.getChatList() }) {
                        Text("getChatList")
                    }
                    Button(onClick = { viewModel.getRoomMessages(66) }) {
                        Text("getRoomMessages")
                    }
                    Button(onClick = { viewModel.sendMessage("hi",12) }) {
                        Text("sendMessage")
                    }
                    Button(onClick = { viewModel.getUserFullName(12) }) {
                        Text("getUserFullName")
                    }

                }

            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}