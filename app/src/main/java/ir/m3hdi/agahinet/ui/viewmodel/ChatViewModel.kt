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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.properties.Delegates

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
                wsMessages.collect{ res->

                    when(res){
                        is WsResponse.ChatList ->{

                            with (res.data){
                                if (!chats.isNullOrEmpty()) {
                                    _uiState.update { state ->

                                        val newChats = chats.map { chat ->
                                            val isMyMessage = chat.creatorId == myId
                                            Chat(
                                                contact = Contact(if (isMyMessage) chat.creatorId else chat.recipientId, chat.contactName),
                                                title = ChatTitle(isMyMessage = isMyMessage, lastMessageText = chat.messageBody)
                                            )
                                        }
                                        newChats.forEach {
                                            getRoomMessages(it.contact.contactId)
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
                                        val existingChat = state.chats.find { it.contact.contactId == contactId } ?: return@update state

                                        val newMessageList = messages.map { Message(
                                            isMyMessage = (it.creatorId == myId),
                                            text = it.messageBody,
                                            createdAt = it.createdAt,
                                            messageId = it.messageId
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
                                    val chat = state.chats.firstOrNull { it.contact.contactId == this.creatorId }

                                    if (chat!=null){
                                        val newChat = chat.copy(messages = (listOf(Message(
                                            isMyMessage = false,
                                            text = messageBody,
                                            createdAt = createdAt, messageId = messageId))+chat.messages),
                                            title = ChatTitle(lastMessageText = messageBody, isMyMessage = false))

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
                    Timber.i(res.toString())
                }
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
                val chat = state.chats.firstOrNull { it.contact.contactId == recipient } ?: return@update state

                val newChat = chat.copy(messages = (listOf(Message(
                    isMyMessage = true, text = messageBody, createdAt = null, messageId = null, pendingId = pendingId))
                        +chat.messages),
                    title = ChatTitle(lastMessageText = messageBody, isMyMessage = false))

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

    data class Contact(val contactId:Int,val contactName:String){
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Contact) return false
            return contactId == other.contactId
        }
        override fun hashCode(): Int { return contactId }
    }

    data class Message(val isMyMessage:Boolean,val text: String,val createdAt:String?,val messageId:Long?,val pendingId:Long?=null)
    data class ChatTitle(/*val lastMessageId: Long, */val lastMessageText:String, val isMyMessage: Boolean)
    data class Chat(val contact: Contact, val title:ChatTitle, val messages:List<Message> = listOf()){
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Chat) return false
            return contact == other.contact
        }
        override fun hashCode(): Int {
            return contact.hashCode()
        }
    }
}
