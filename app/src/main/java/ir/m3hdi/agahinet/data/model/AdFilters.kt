package ir.m3hdi.agahinet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ir.m3hdi.agahinet.util.Constants.Companion.PAGE_SIZE

data class AdFilters(
    @Json(name = "keyword") var keyword: String?=null,
    @Json(name = "category") val category: Int?=null,
    @Json(name = "cities") val cities: List<Int>?=null,
    @Json(name = "min_price") val minPrice: String?=null,
    @Json(name = "max_price") val maxPrice: String?=null,
    @Json(name = "limit") val limit: Int = PAGE_SIZE,
    @Json(name = "offset") var offset: Int=0
)