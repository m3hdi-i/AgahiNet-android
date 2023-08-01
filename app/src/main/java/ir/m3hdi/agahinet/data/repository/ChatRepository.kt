package ir.m3hdi.agahinet.data.repository

import com.squareup.moshi.Moshi
import ir.m3hdi.agahinet.data.remote.model.chat.GetChatListRequest
import ir.m3hdi.agahinet.data.remote.model.chat.GetRoomMessagesRequest
import ir.m3hdi.agahinet.data.remote.model.chat.GetUserFullNameRequest
import ir.m3hdi.agahinet.data.remote.model.chat.SendMessageRequest
import ir.m3hdi.agahinet.data.remote.model.chat.WsResponse
import ir.m3hdi.agahinet.util.AppUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


enum class WsState{
    IDLE,CONNECTING,OPEN,FAILED
}

@OptIn(DelicateCoroutinesApi::class)
class ChatRepository @Inject constructor(private val okHttpClient: OkHttpClient, @Named("ws") private val moshi: Moshi)
{
    val wsChannel = Channel<WsResponse>(Channel.UNLIMITED)

    private val _wsStatus = MutableStateFlow(WsState.IDLE)
    val wsStatus = _wsStatus.asStateFlow()


    private val request by lazy { Request.Builder().url(AppUtils.WS_URL).build() }
    private var webSocket: WebSocket? = null

    fun connect(delay:Long=0) {
        GlobalScope.launch {
            withContext(Dispatchers.IO){
                _wsStatus.update { WsState.CONNECTING }
                delay(delay)
                webSocket = okHttpClient.newWebSocket(request, wsListener)
            }
        }
    }

    private fun processMessage(text: String) {

        val wsResponseObject: WsResponse? = try {
            val adapter = moshi.adapter(WsResponse::class.java)
            adapter.fromJson(text)
        }catch (_:Exception){
            null
        }
        wsResponseObject?.let { wsChannel.trySend(it) }
    }

    private fun onWsFailed(){
        _wsStatus.update { WsState.FAILED }
        connect(delay = 2000L)
    }

    suspend fun disconnect() = withContext(Dispatchers.IO){
        webSocket?.close(1000, "Canceled manually.")
    }
    private val wsListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            _wsStatus.update { WsState.OPEN }
            // get chatList whenever WS connects successfully
            GlobalScope.launch {
                getChatList()
            }
        }
        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            processMessage(text)
        }
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            //onWsFailed()
        }
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            onWsFailed()
        }
    }

    private suspend fun send(text:String) = withContext(Dispatchers.IO){
        webSocket?.send(text).toString()
    }

    suspend fun getChatList(){
        val req = GetChatListRequest()
        val adapter = moshi.adapter(GetChatListRequest::class.java)
        val json = adapter.toJson(req)
        send(json)
    }
    suspend fun getRoomMessages(contactId: Int) {
        val req = GetRoomMessagesRequest(contactId = contactId)
        val adapter = moshi.adapter(GetRoomMessagesRequest::class.java).nullSafe()
        val json = adapter.toJson(req)
        send(json)
    }
    suspend fun sendMessage(messageBody: String, recipient: Int):Long {
        val pendingId = System.currentTimeMillis()
        val req = SendMessageRequest(messageBody = messageBody, recipient = recipient, pendingId = pendingId)
        val adapter = moshi.adapter(SendMessageRequest::class.java)
        val json = adapter.toJson(req)
        send(json)
        return pendingId
    }
    suspend fun getUserFullName(uid:Int){
        val req = GetUserFullNameRequest(uid = uid)
        val adapter = moshi.adapter(GetUserFullNameRequest::class.java)
        val json = adapter.toJson(req)
        send(json)
    }
}

