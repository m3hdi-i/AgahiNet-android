package ir.m3hdi.agahinet.data.remote.model.chat

import com.squareup.moshi.Json

data class GetChatListRequest(
    @Json(name = "func")
    val func:String="CHATLIST"
)
data class GetRoomMessagesRequest(
    @Json(name = "func")
    val func:String="ROOM_MESSAGES",
    @Json(name = "cid")
    val contactId:Int,
    @Json(name = "limit")
    val limit:Int?=null,
    @Json(name = "offset")
    val offset:Int?=null
)

data class SendMessageRequest(
    @Json(name = "func")
    val func:String="SEND_MESSAGE",
    @Json(name = "message_body")
    val messageBody:String,
    @Json(name = "recipient")
    val recipient:Int,
    @Json(name = "pending_id")
    val pendingId: Long
)

data class GetUserFullNameRequest(
    @Json(name = "func")
    val func:String="USER_FULLNAME",
    @Json(name = "uid")
    val uid:Int
)
