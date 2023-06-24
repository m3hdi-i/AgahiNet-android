package ir.m3hdi.agahinet.data.remote

import ir.m3hdi.agahinet.data.remote.model.HasBookmark
import ir.m3hdi.agahinet.data.remote.model.SearchFiltersRequest
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.ContactInfo
import ir.m3hdi.agahinet.data.remote.model.UserAuthResponse
import ir.m3hdi.agahinet.data.remote.model.UserSignin
import ir.m3hdi.agahinet.data.remote.model.UserSignup
import okhttp3.ResponseBody
import retrofit2.Response
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

    @GET("api/myads")
    suspend fun getMyAds(): List<Ad>

    @GET("api/ad/remove")
    suspend fun deleteAd(@Query("ad_id") adId: Int): ResponseBody

    @GET("api/bookmark")
    suspend fun getMyBookmarks(): List<Ad>

    @GET("api/bookmark/create")
    suspend fun addBookmark(@Query("ad_id") adId: Int): ResponseBody

    @GET("api/bookmark/remove")
    suspend fun deleteBookmark(@Query("ad_id") adId: Int): ResponseBody

    @GET("api/has_bookmark")
    suspend fun hasBookmark(@Query("ad_id") adId: Int): HasBookmark

}