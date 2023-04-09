package ir.m3hdi.agahinet.data.remote

import ir.m3hdi.agahinet.data.model.UserSignup
import ir.m3hdi.agahinet.data.model.UserAuthResponse
import ir.m3hdi.agahinet.data.model.UserSignin
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ANetService {

    @POST("api/signup")
    suspend fun signup(@Body body: UserSignup): UserAuthResponse

    @POST("api/signin")
    suspend fun signin(@Body body: UserSignin): UserAuthResponse

}