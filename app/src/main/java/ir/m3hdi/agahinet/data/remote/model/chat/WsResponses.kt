package ir.m3hdi.agahinet.data.remote.model.chat

import com.squareup.moshi.Json
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

sealed class WsResponse(@Json(name="response_type") val responseType: ResponseType, open val data: Any?){

    data class ChatList(override val data: ChatListData) : WsResponse(ResponseType.CHATLIST,data)
    data class RoomMessages(override val data: RoomMessagesData) : WsResponse(ResponseType.ROOM_MESSAGES,data)
    data class SendMessage(override val data: SendMessageData) : WsResponse(ResponseType.SEND_MESSAGE,data)
    data class IncomingMessage(override val data: IncomingMessageData) : WsResponse(ResponseType.INCOMING_MESSAGE,data)
    data class UserFullName(override val data: UserFullNameData) : WsResponse(ResponseType.USER_FULLNAME,data)
    data class Error(override val data: ErrorData):WsResponse(ResponseType.ERROR,data)
}

data class ChatListData(@Json(name = "list") val chats:List<ChatWs>?)
data class RoomMessagesData(@Json(name = "messages") val messages:List<MessageWs>?)
data class SendMessageData(@Json(name = "pending_id") val pendingId: Long)
data class IncomingMessageData(@Json(name = "message") val message: MessageWs)
data class UserFullNameData(@Json(name = "fullname") val fullName:String?)
data class ErrorData(@Json(name = "status") val status: String)

enum class ResponseType {
    CHATLIST, ROOM_MESSAGES, SEND_MESSAGE, USER_FULLNAME, INCOMING_MESSAGE, ERROR;
}


data class ChatWs(
    @Json(name = "mid") val messageId: Long,
    @Json(name = "message_body") val messageBody: String,
    @Json(name = "creator_id") val creatorId: Int,
    @Json(name = "recipient_id") val recipientId: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "is_read") val isRead: Boolean,
    @Json(name = "contact_name") val contactName: String
)
data class MessageWs(
    @Json(name = "mid") val messageId: Long,
    @Json(name = "message_body") val messageBody: String,
    @Json(name = "creator_id") val creatorId: Int,
    @Json(name = "recipient_id") val recipientId: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "is_read") val isRead: Boolean
)

val wsResponseAdapterFactory: PolymorphicJsonAdapterFactory<WsResponse> = PolymorphicJsonAdapterFactory.of(
    WsResponse::class.java, "response_type")
    .withSubtype(WsResponse.ChatList::class.java, ResponseType.CHATLIST.name)
    .withSubtype(WsResponse.RoomMessages::class.java, ResponseType.ROOM_MESSAGES.name)
    .withSubtype(WsResponse.SendMessage::class.java, ResponseType.SEND_MESSAGE.name)
    .withSubtype(WsResponse.IncomingMessage::class.java, ResponseType.INCOMING_MESSAGE.name)
    .withSubtype(WsResponse.UserFullName::class.java, ResponseType.USER_FULLNAME.name)
    .withSubtype(WsResponse.Error::class.java, ResponseType.ERROR.name)
