package ir.m3hdi.agahinet.domain.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ad(
    @Json(name = "ad_id")
    val adId: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "price")
    val price: String?,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "category")
    val category: Int,
    @Json(name = "creator")
    val creator: Int,
    @Json(name = "city")
    val city: Int,
    @Json(name = "main_image_id")
    val mainImageId: Int?
) : Parcelable
