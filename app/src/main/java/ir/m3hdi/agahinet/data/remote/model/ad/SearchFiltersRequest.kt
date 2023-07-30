package ir.m3hdi.agahinet.data.remote.model.ad

import com.squareup.moshi.Json

data class SearchFiltersRequest(
    @Json(name = "keyword") var keyword: String?=null,
    @Json(name = "category") var category: Int?=null,
    @Json(name = "cities") var cities: List<Int>?=null,
    @Json(name = "min_price") var minPrice: String?=null,
    @Json(name = "max_price") var maxPrice: String?=null,
    @Json(name = "limit") val limit: Int,
    @Json(name = "offset") var offset: Int
)