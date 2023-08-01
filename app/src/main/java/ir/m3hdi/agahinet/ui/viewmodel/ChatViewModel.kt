package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.remote.model.chat.WsResponse
import ir.m3hdi.agahinet.data.repository.ChatRepository
import ir.m3hdi.agahinet.util.AppUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates
import ir.m3hdi.agahinet.domain.model.chat.Chat
import ir.m3hdi.agahinet.domain.model.chat.Contact
import ir.m3hdi.agahinet.domain.model.chat.Message

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    val connectionStatus = chatRepository.wsStatus
    private val wsMessages = chatRepository.wsChannel.receiveAsFlow()

    private var myId by Delegates.notNull<Int>()

    init {

        viewModelScope.launch {

            launch {
                collectWsMessages()
            }

            launch {
                connectionStatus.collect{
                    Timber.e("WS Connection status: $it")
                }
            }
            launch {
                AppUtils.currentUser.collect {auth->
                    _uiState.update { UiState() }
                    myId = auth?.user?.uid ?: -1
                    if (myId != -1) { chatRepository.connect() } else { chatRepository.disconnect() }
                }
            }
        }
    }


    private suspend fun collectWsMessages(){
        wsMessages.collect{ res->

            when(res){
                is WsResponse.ChatList ->{

                    with (res.data){
                        if (!chats.isNullOrEmpty()) {
                            _uiState.update { state ->

                                val newChats = chats.map { chat ->
                                    val isMyMessage = chat.creatorId == myId
                                    val lastMessage = Message(isMyMessage = isMyMessage,
                                        id = chat.messageId, text= chat.messageBody,
                                        createdAt = chat.createdAt)

                                    Chat(
                                        contact = Contact(if (isMyMessage) chat.recipientId else chat.creatorId, chat.contactName),
                                        lastMessage = lastMessage)
                                }
                                newChats.forEach {
                                    getRoomMessages(it.contact.id)
                                }
                                state.copy(chats = newChats)
                            }
                        }
                    }
                }

                is WsResponse.RoomMessages ->{

                    with (res.data){
                        if (!messages.isNullOrEmpty()){
                            _uiState.update { state ->
                                val contactId = messages.first().let {
                                    if (it.creatorId==myId) it.recipientId else it.creatorId
                                }
                                val existingChat = state.chats.find { it.contact.id == contactId } ?: return@update state

                                val newMessageList = messages.map { Message(
                                    isMyMessage = (it.creatorId == myId),
                                    text = it.messageBody,
                                    createdAt = it.createdAt,
                                    id = it.messageId
                                ) }
                                val newChatList = state.chats.map { if (it == existingChat) it.copy(messages = newMessageList) else it }
                                state.copy(chats = newChatList)
                            }
                        }
                    }
                }
                is WsResponse.SendMessage ->{
                    with (res.data){

                        _uiState.update { state ->

                            val chat = state.chats.firstOrNull { it.messages.firstOrNull()?.pendingId == pendingId } ?: return@update state

                            val newChat = chat.copy(messages = chat.messages.map{
                                if (it.pendingId==pendingId){
                                    it.copy(pendingId = null)
                                }
                                else{
                                    it
                                }
                            })

                            state.copy(chats = state.chats.map {
                                if (it.contact == chat.contact) newChat else it
                            })
                        }
                    }


                }
                is WsResponse.UserFullName ->{


                }
                is WsResponse.IncomingMessage ->{

                    _uiState.update { state ->

                        with(res.data.message){
                            val chat = state.chats.firstOrNull { it.contact.id == this.creatorId }

                            if (chat!=null){
                                val newMessage = Message(
                                    isMyMessage = false,
                                    text = messageBody,
                                    createdAt = createdAt, id = messageId)

                                val newChat = chat.copy(messages =
                                (listOf(newMessage)+chat.messages), lastMessage = newMessage)

                                state.copy(chats = state.chats.map {
                                    if (it.contact == chat.contact) newChat else it
                                })
                            }else{
                                getChatList()
                                state
                            }
                        }
                    }

                }
                is WsResponse.Error ->{
                }
            }
        }

    }

    fun getChatList(){
        viewModelScope.launch {
            chatRepository.getChatList()
        }
    }
    fun getRoomMessages(contactId:Int){
        viewModelScope.launch {
            chatRepository.getRoomMessages(contactId)
        }
    }
    fun sendMessage(messageBody:String,recipient:Int){
        viewModelScope.launch {
            val pendingId = chatRepository.sendMessage(messageBody,recipient)
            // Add message to UiState with pending state
            _uiState.update { state ->
                val chat = state.chats.firstOrNull { it.contact.id == recipient } ?: return@update state

                val newMessage = Message(
                    isMyMessage = true, text = messageBody, createdAt = null,
                    id = null, pendingId = pendingId)

                val newChat = chat.copy(lastMessage = newMessage,
                    messages = (listOf(newMessage) +chat.messages))

                state.copy(chats = state.chats.map {
                    if (it.contact == chat.contact) newChat else it
                })
            }
        }
    }
    fun getUserFullName(uid:Int){
        viewModelScope.launch {
            chatRepository.getUserFullName(uid)

        }
    }

    // UiState of chat screen
    data class UiState(val chats:List<Chat> = listOf())

}
