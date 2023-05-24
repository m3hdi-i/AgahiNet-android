package ir.m3hdi.agahinet.data.remote

import ir.m3hdi.agahinet.data.model.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.POST

interface ANetService {

    @POST("api/signup")
    suspend fun signup(@Body body: UserSignup): UserAuthResponse

    @POST("api/signin")
    suspend fun signin(@Body body: UserSignin): UserAuthResponse

    @POST("api/ad/search")
    suspend fun searchAds(@Body filters: AdFilters): List<Ad>

}