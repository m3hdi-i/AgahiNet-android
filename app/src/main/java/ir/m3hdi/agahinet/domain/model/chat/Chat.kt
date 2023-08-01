package ir.m3hdi.agahinet.domain.model.chat

data class Chat(val contact: Contact, val lastMessage: Message, val messages:List<Message> = listOf()){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Chat) return false
        return contact == other.contact
    }
    override fun hashCode(): Int {
        return contact.hashCode()
    }
}