package ir.m3hdi.agahinet.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.data.paging.AdsPagingSource
import ir.m3hdi.agahinet.data.paging.NETWORK_PAGE_SIZE
import ir.m3hdi.agahinet.domain.model.SearchFilters
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdRepository @Inject constructor(private val api: ANetService)
{
    /*suspend fun searchAds(filters: SearchFiltersRequest): Resultx<List<Ad>> = withContext(Dispatchers.IO){
        return@withContext AppUtils.suspendRunCatching {
            api.searchAds(filters)
        }
    }*/

    fun searchAds(filters: SearchFilters): Flow<PagingData<Ad>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                initialLoadSize = NETWORK_PAGE_SIZE,
                prefetchDistance = NETWORK_PAGE_SIZE ,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                AdsPagingSource(api,filters)
            }
        ).flow
    }


}
