package ir.m3hdi.agahinet.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.m3hdi.agahinet.data.paging.AdsPagingSource
import ir.m3hdi.agahinet.data.paging.NETWORK_PAGE_SIZE
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.data.remote.model.HasBookmark
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.ContactInfo
import ir.m3hdi.agahinet.domain.model.Resultx
import ir.m3hdi.agahinet.domain.model.SearchFilters
import ir.m3hdi.agahinet.util.AppUtils.Companion.suspendRunCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import javax.inject.Inject

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

    suspend fun getImagesOfAd(adId:Int) : Resultx<List<Int>> = withContext(Dispatchers.IO){
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

    suspend fun deleteAd(adId: Int) : Resultx<ResponseBody> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.deleteAd(adId)
        }
    }

    suspend fun getBookmarks() : Resultx<List<Ad>> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.getMyBookmarks()
        }
    }

    suspend fun addBookmark(adId: Int) : Resultx<ResponseBody> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.addBookmark(adId)
        }
    }

    suspend fun deleteBookmark(adId: Int) : Resultx<ResponseBody> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.deleteBookmark(adId)
        }
    }

    suspend fun hasBookmark(adId: Int) : Resultx<HasBookmark> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.hasBookmark(adId)
        }
    }

}
