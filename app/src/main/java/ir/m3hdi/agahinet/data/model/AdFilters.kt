package ir.m3hdi.agahinet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ir.m3hdi.agahinet.util.Constants.Companion.PAGE_SIZE

data class AdFilters(
    @Json(name = "keyword") var keyword: String?=null,
    @Json(name = "category") var category: Int?=null,
    @Json(name = "cities") var cities: List<Int>?=null,
    @Json(name = "min_price") var minPrice: String?=null,
    @Json(name = "max_price") var maxPrice: String?=null,
    @Json(name = "limit") val limit: Int = PAGE_SIZE,
    @Json(name = "offset") var offset: Int=0
)