package ir.m3hdi.agahinet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ir.m3hdi.agahinet.util.AppUtils.Companion.PAGE_SIZE

data class AdFilters(
    @Json(name = "keyword") val keyword: String?,
    @Json(name = "category") val category: Int?,
    @Json(name = "cities") val cities: List<Int>?,
    @Json(name = "min_price") val minPrice: String?,
    @Json(name = "max_price") val maxPrice: String?,
    @Json(name = "limit") val limit: Int,
    @Json(name = "offset") val offset: Int
)