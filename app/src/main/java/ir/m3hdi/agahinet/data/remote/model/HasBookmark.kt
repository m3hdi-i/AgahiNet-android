package ir.m3hdi.agahinet.data.remote.model

import com.squareup.moshi.Json

data class HasBookmark(@Json(name = "has_bookmark") val has: Boolean)
