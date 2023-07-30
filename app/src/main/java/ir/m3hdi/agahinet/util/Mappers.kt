package ir.m3hdi.agahinet.util

import ir.m3hdi.agahinet.data.remote.model.ad.EditAdRequest
import ir.m3hdi.agahinet.data.remote.model.ad.NewAdRequest
import ir.m3hdi.agahinet.data.remote.model.ad.SearchFiltersRequest
import ir.m3hdi.agahinet.domain.model.SearchFilters
import ir.m3hdi.agahinet.ui.viewmodel.NewAdViewModel

class Mappers {
    companion object {
        fun SearchFilters.toSearchFiltersRequest(limit:Int, offset:Int): SearchFiltersRequest {
            return SearchFiltersRequest(this.keyword,
                this.category?.id,
                this.cities?.map { it.cityId },
                this.minPrice,
                this.maxPrice,
                limit,
                offset)
        }


        fun NewAdViewModel.UiState.toNewAdRequest(): NewAdRequest {

            return NewAdRequest(
                title = this.title,
                description =this.description,
                price = this.price,
                category = this.category!!.id,
                city = this.city!!.cityId,
                imagesList = this.imagesList.map { it.uploadId!! }.toList(),
                mainImageId = this.imagesList.firstOrNull { it.isMainImage }?.uploadId
                )
        }
        fun NewAdViewModel.UiState.toEditAdRequest(): EditAdRequest {

            return EditAdRequest(
                adId = this.adId!!,
                title = this.title,
                description =this.description,
                price = this.price,
                category = this.category!!.id,
                city = this.city!!.cityId,
                imagesListOld = this.oldImages,
                imagesListNew = this.imagesList.map { it.uploadId!! }.toList(),
                mainImageId = this.imagesList.firstOrNull { it.isMainImage }?.uploadId
            )
        }

    }
}