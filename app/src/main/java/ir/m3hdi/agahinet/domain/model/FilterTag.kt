package ir.m3hdi.agahinet.domain.model

import ir.m3hdi.agahinet.data.local.entity.City

sealed class FilterTag(val title: String) {
    class CATEGORY(category: Category) : FilterTag(category.title)
    class PRICE : FilterTag("قیمت")
    class CITY(val city: City) : FilterTag(city.title)
}