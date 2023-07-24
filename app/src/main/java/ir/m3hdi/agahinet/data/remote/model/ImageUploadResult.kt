package ir.m3hdi.agahinet.data.remote.model

import com.squareup.moshi.Json

data class ImageUploadResult(@Json(name = "image_id") val image_id: String)