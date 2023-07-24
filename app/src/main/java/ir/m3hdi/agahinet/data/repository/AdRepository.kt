package ir.m3hdi.agahinet.data.repository

import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.m3hdi.agahinet.data.paging.AdsPagingSource
import ir.m3hdi.agahinet.data.paging.NETWORK_PAGE_SIZE
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.data.remote.model.EditAdRequest
import ir.m3hdi.agahinet.data.remote.model.HasBookmark
import ir.m3hdi.agahinet.data.remote.model.ImageUploadResult
import ir.m3hdi.agahinet.data.remote.model.NewAdRequest
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.ContactInfo
import ir.m3hdi.agahinet.domain.model.Resultx
import ir.m3hdi.agahinet.domain.model.SearchFilters
import ir.m3hdi.agahinet.util.AppUtils.Companion.suspendRunCatching
import ir.m3hdi.agahinet.util.ContentUriRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

const val AD_MAX_IMAGES_NUMBER = 5

class AdRepository @Inject constructor(private val api: ANetService, @ApplicationContext private val context: Context)
{

    fun searchAds(filters: SearchFilters): Flow<PagingData<Ad>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                initialLoadSize = NETWORK_PAGE_SIZE,
                prefetchDistance = NETWORK_PAGE_SIZE ,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                AdsPagingSource(api,filters, context)
            }
        ).flow
    }

    suspend fun getImagesOfAd(adId:Int) : Resultx<List<String>> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.getImagesOfAd(adId)
        }
    }

    suspend fun getContactInfoOfUser(userId:Int) : Resultx<ContactInfo> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.getContactInfoOfUser(userId)
        }
    }

    suspend fun getMyAds() : Resultx<List<Ad>> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.getMyAds()
        }
    }

    suspend fun deleteAd(adId: Int) : Resultx<Boolean> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.deleteAd(adId).isSuccessful
        }
    }

    suspend fun getBookmarks() : Resultx<List<Ad>> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.getMyBookmarks()
        }
    }

    suspend fun addBookmark(adId: Int) : Resultx<Boolean> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.addBookmark(adId).isSuccessful
        }
    }

    suspend fun deleteBookmark(adId: Int) : Resultx<Boolean> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.deleteBookmark(adId).isSuccessful
        }
    }

    suspend fun hasBookmark(adId: Int) : Resultx<HasBookmark> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.hasBookmark(adId)
        }
    }



    suspend fun uploadImage(uri:Uri):Resultx<ImageUploadResult?> = withContext(Dispatchers.IO){

        return@withContext suspendRunCatching {

            /*val inputStream = context.contentResolver.openInputStream(uri)

            val res = inputStream?.let {

                val bytes =  inputStream.readBytes()

                val filePart = inputStream?.let {
                    val contentType = "image/".toMediaTypeOrNull()
                    val requestBody = it.source().use { source ->
                        source.asRequestBody(contentType)
                    }
                    MultipartBody.Part.createFormData("file", "image.jpg", requestBody)
                }

                api.uploadImage(filePart)
            }
            withContext(Dispatchers.IO){
                inputStream?.close()
            }*/

            val requestBody = ContentUriRequestBody(context.contentResolver, uri)
            val filePart = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

            return@suspendRunCatching api.uploadImage(filePart)

        }
    }

    suspend fun publishAd(ad:NewAdRequest) : Resultx<Boolean> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.publishAd(ad).isSuccessful
        }
    }

    suspend fun editAd(ad:EditAdRequest) : Resultx<Boolean> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.editAd(ad).isSuccessful
        }
    }

}
