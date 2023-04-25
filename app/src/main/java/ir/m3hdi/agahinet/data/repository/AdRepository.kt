package ir.m3hdi.agahinet.data.repository

import ir.m3hdi.agahinet.data.model.Ad
import ir.m3hdi.agahinet.data.model.AdFilters
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdRepository @Inject constructor(private val api: ANetService, private val dispatcher: CoroutineDispatcher)
{
    suspend fun searchAds(filters: AdFilters): Resultx<List<Ad>> = withContext(dispatcher){
        return@withContext AppUtils.suspendRunCatching {
            api.searchAds(filters)
        }
    }


}
