package ir.m3hdi.agahinet.data.model

import com.squareup.moshi.Json


data class UserSignup (
    @Json(name = "fullname") val fullname: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "phone_number") val phoneNumber: String
)

data class UserSignin (
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
)

data class User(
    @Json(name = "uid") val uid: String,
    @Json(name = "fullname") val fullname: String
)

data class UserAuthResponse(
    @Json(name = "message") val message: String,
    @Json(name = "user") val user: User?,
    @Json(name = "access-token") val accessToken: String?
)
