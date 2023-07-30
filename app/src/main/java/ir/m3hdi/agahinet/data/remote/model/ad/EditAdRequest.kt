package ir.m3hdi.agahinet.data.remote.model.ad

import com.squareup.moshi.Json

data class EditAdRequest(
    @Json(name = "ad_id")
    val adId: Long,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "price")
    val price:String?,
    @Json(name = "category")
    val category: Int,
    @Json(name = "city")
    val city: Int,
    @Json(name = "images_list_old")
    val imagesListOld: List<Long>? = null,
    @Json(name = "images_list_new")
    val imagesListNew: List<Long>? = null,
    @Json(name = "main_image_id")
    val mainImageId: Long? = null

)