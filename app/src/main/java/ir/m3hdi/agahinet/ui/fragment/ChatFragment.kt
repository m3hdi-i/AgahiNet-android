package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import ir.m3hdi.agahinet.ui.components.util.RtlLayout
import ir.m3hdi.agahinet.ui.components.util.Toolbar
import ir.m3hdi.agahinet.ui.theme.AppTheme
import ir.m3hdi.agahinet.ui.viewmodel.ChatViewModel
import ir.m3hdi.agahinet.util.AppUtils
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import ir.m3hdi.agahinet.domain.model.chat.Chat
import ir.m3hdi.agahinet.ui.components.ChatTitle

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
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val connectionState by viewModel.connectionStatus.collectAsStateWithLifecycle()

                ChatScreen(chats = uiState.chats)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                AppUtils.currentUser.collect {
                    AppUtils.handleNeedAuthFragment(it!=null, childFragmentManager, binding.layoutParent, binding.layoutNeedAuth, binding.layoutContent)
                }
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ChatScreen(chats:List<Chat>){

        AppTheme{
            RtlLayout {
                CompositionLocalProvider(LocalOverscrollConfiguration provides null) {

                    Column(modifier = Modifier.fillMaxSize()){

                        Toolbar("چت ها")
                        LazyColumn(modifier = Modifier.fillMaxSize()){

                            items(
                                items = chats,
                                key = { it.contact.id }
                            ) { chat ->
                                Row(Modifier.animateItemPlacement()) {
                                    ChatTitle(contact = chat.contact, lastMessage = chat.lastMessage, onClick = {})
                                }



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

}