package ir.m3hdi.agahinet.data.remote

import ir.m3hdi.agahinet.data.remote.model.SearchFiltersRequest
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.UserAuthResponse
import ir.m3hdi.agahinet.domain.model.UserSignin
import ir.m3hdi.agahinet.domain.model.UserSignup
import retrofit2.http.Body
import retrofit2.http.POST

interface ANetService {

    @POST("api/signup")
    suspend fun signup(@Body body: UserSignup): UserAuthResponse

    @POST("api/signin")
    suspend fun signin(@Body body: UserSignin): UserAuthResponse

    @POST("api/ad/search")
    suspend fun searchAds(@Body filters: SearchFiltersRequest): List<Ad>

}