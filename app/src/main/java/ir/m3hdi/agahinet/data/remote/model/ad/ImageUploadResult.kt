package ir.m3hdi.agahinet.data.remote.model.ad

import com.squareup.moshi.Json

data class ImageUploadResult(@Json(name = "image_id") val imageId: Long)