package ir.m3hdi.agahinet.data.remote

import ir.m3hdi.agahinet.data.remote.model.SearchFiltersRequest
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.ContactInfo
import ir.m3hdi.agahinet.domain.model.UserAuthResponse
import ir.m3hdi.agahinet.domain.model.UserSignin
import ir.m3hdi.agahinet.domain.model.UserSignup
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ANetService {

    @POST("api/signup")
    suspend fun signup(@Body body: UserSignup): UserAuthResponse

    @POST("api/signin")
    suspend fun signin(@Body body: UserSignin): UserAuthResponse

    @POST("api/ad/search")
    suspend fun searchAds(@Body filters: SearchFiltersRequest): List<Ad>

    @GET("api/ad/{ad_id}/images")
    suspend fun getImagesOfAd(@Path("ad_id") adId: Int): List<Int>

    @GET("api/contact_info")
    suspend fun getContactInfoOfUser(@Query("uid") userId:Int): ContactInfo

}