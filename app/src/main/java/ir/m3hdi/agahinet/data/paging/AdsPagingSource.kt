package ir.m3hdi.agahinet.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.domain.model.SearchFilters
import ir.m3hdi.agahinet.util.Mappers.Companion.toSearchFiltersRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val NETWORK_PAGE_SIZE = 10
private const val INITIAL_PAGE = 1


class AdsPagingSource(private val service:ANetService,private val searchFilters: SearchFilters) : PagingSource< Int, Ad>()  {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Ad> {
        // Start refresh at position 1 if undefined.
        val position = params.key ?: INITIAL_PAGE
        println("--------------------------------------------------------------- $position")
        val offset = if (params.key != null) ((position - 1) * NETWORK_PAGE_SIZE) else 0
        return try {
            withContext(Dispatchers.IO){

                val ads = service.searchAds(searchFilters.toSearchFiltersRequest(limit = params.loadSize,offset=offset))
                val nextKey = if (ads.size < NETWORK_PAGE_SIZE) null else (position + (params.loadSize / NETWORK_PAGE_SIZE))

                LoadResult.Page(
                    data = ads,
                    prevKey = null, // Only paging forward.
                    // assume that if a full page is not loaded, that means the end of the data
                    nextKey = nextKey
                )
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Ad>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return null
    }
}

