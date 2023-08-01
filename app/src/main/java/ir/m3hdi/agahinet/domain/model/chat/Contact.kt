package ir.m3hdi.agahinet.domain.model.chat

data class Contact(val id:Int, val name:String){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Contact) return false
        return id == other.id
    }
    override fun hashCode(): Int { return id }
}
