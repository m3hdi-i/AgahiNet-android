package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.data.repository.CityRepository
import ir.m3hdi.agahinet.domain.model.ad.Ad
import ir.m3hdi.agahinet.util.PersianTimeAgo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CitiesViewModel @Inject constructor(private val cityRepository: CityRepository):ViewModel() {


    private val _allProvinces=MutableStateFlow<List<City>>(listOf())
    val allProvinces = _allProvinces.asStateFlow()

    private lateinit var allCities: HashMap<Int,String>

    init {
        // Fetch all cities hashmap and all provinces list from prePopulated ROOM database
        viewModelScope.launch {
            allCities=cityRepository.getAllCities()
            _allProvinces.value=cityRepository.getAllProvinces()
        }
    }

    suspend fun getCitiesOfProvince(provinceId:Int) = cityRepository.getCitiesOfProvince(provinceId)

    // Generate a `x time ago in City y` text for ads
    fun getTimeAndLocText(ad: Ad): String {
        val cityName = allCities[ad.city]
        val timeAgoText = PersianTimeAgo.dateTimeStringToTimeAgo(ad.createdAt)
        return cityName?.let { "$timeAgoText در $it" } ?: timeAgoText
    }
}