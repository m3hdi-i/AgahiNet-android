package ir.m3hdi.agahinet.ui.viewmodel

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

}

const val MAX_IMAGES_COUNT = 6

@Immutable
data class UiState(
    val title: String="",
    val description: String="",
    val price: String?="",
    val category: Category?=null,
    val province: City?=null,
    val city: City?=null,
    val imagesList: ImmutableList<Pair<String,Boolean>> = persistentListOf(),

    val allProvinces:ImmutableList<City> = persistentListOf(),
    val citiesToSelect:ImmutableList<City> = persistentListOf()
)