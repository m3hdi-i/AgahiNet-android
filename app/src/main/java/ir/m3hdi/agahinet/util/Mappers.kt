package ir.m3hdi.agahinet.util

import ir.m3hdi.agahinet.data.remote.model.SearchFiltersRequest
import ir.m3hdi.agahinet.domain.model.SearchFilters

class Mappers {
    companion object {
        fun SearchFilters.toSearchFiltersRequest(limit:Int, offset:Int):SearchFiltersRequest{
            return SearchFiltersRequest(this.keyword,
                this.category?.id,
                this.cities?.map { it.cityId },
                this.minPrice,
                this.maxPrice,
                limit,
                offset)
        }
    }
}