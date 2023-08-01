package ir.m3hdi.agahinet.domain.model.ad

import com.squareup.moshi.Json

data class ContactInfo(
    @Json(name = "uid")
    val userId: Int,
    @Json(name = "phone_number")
    val phoneNumber: String,
)