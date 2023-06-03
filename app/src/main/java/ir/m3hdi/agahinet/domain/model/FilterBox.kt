package ir.m3hdi.agahinet.domain.model

enum class FilterBoxType {
    CATEGORY,PRICE,CITY
}
data class FilterBox (val type:FilterBoxType,val title:String)