package ir.m3hdi.agahinet.domain.model.chat

data class Message(val isMyMessage:Boolean,
                   val text: String,
                   val createdAt:String?,
                   val id:Long?,
                   val pendingId:Long?=null)
