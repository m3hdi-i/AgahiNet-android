package ir.m3hdi.agahinet.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.data.repository.CityRepository
import ir.m3hdi.agahinet.domain.model.Category
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAdViewModel @Inject constructor(private val cityRepository: CityRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {

        viewModelScope.launch {
            val allProvinces = cityRepository.getAllProvinces()
            _uiState.update { it.copy(allProvinces=allProvinces.toImmutableList()) }
        }

    }
    fun setCategory(category:Category){
        _uiState.update { it.copy(category = category) }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setDescription(desc: String) {
        _uiState.update { it.copy(description = desc) }
    }

    fun setPrice(price: String?) {
        _uiState.update { it.copy(price = price) }
    }

    fun setProvince(province: City) {

        if (uiState.value.province != province){
            viewModelScope.launch {
                val subCities = cityRepository.getCitiesOfProvince(province.cityId)
                _uiState.update {
                    it.copy(province = province, city = null, citiesToSelect = subCities.toImmutableList()) }
            }
        }
    }

    fun setCity(city: City) {
        _uiState.update { it.copy(city = city)  }
    }

    fun addImage(uri: Uri){
        val oldImageList = uiState.value.imagesList
        if (uri !in oldImageList.map { it.uri }){
            val newImageList= (oldImageList + NewAdImage(uri, isMainImage = (oldImageList.size == 0)))
            _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
        }
    }

    fun deleteImage(image: NewAdImage) {
        val oldImageList = uiState.value.imagesList
        val newImageList= (oldImageList - image).toMutableList()

        if (newImageList.firstOrNull { it.isMainImage } == null && newImageList.isNotEmpty()) {
            newImageList[0] = newImageList[0].copy(isMainImage = true)
        }

        _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
    }

    fun setMainImage(image: NewAdImage) {

        val newImageList = uiState.value.imagesList.map { item ->
            if (item == image)
                item.copy(isMainImage = true)
            else
                item.copy(isMainImage = false)
        }
        _uiState.update { it.copy(imagesList = newImageList.toImmutableList())  }
    }

}

const val MAX_IMAGES_COUNT = 6

@Immutable
data class NewAdImage(val uri:Uri, val isMainImage:Boolean=false, val uploadUrl:String?=null)

@Immutable
data class UiState(
    val title: String="",
    val description: String="",
    val price: String?="",
    val category: Category?=null,
    val province: City?=null,
    val city: City?=null,
    val imagesList: ImmutableList<NewAdImage> = persistentListOf(),

    val allProvinces:ImmutableList<City> = persistentListOf(),
    val citiesToSelect:ImmutableList<City> = persistentListOf()
)

