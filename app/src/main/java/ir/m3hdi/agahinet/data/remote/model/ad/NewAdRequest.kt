package ir.m3hdi.agahinet.data.remote.model.ad

import com.squareup.moshi.Json

data class NewAdRequest(
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "price")
    val price: String? = null,
    @Json(name = "category")
    val category: Int,
    @Json(name = "city")
    val city: Int,
    @Json(name = "images_list")
    val imagesList: List<Long>? = null,
    @Json(name = "main_image_id")
    val mainImageId: Long? = null
)