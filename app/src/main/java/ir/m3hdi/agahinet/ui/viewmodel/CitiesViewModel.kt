package ir.m3hdi.agahinet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.data.repository.CityRepository
import ir.m3hdi.agahinet.domain.model.Ad
import ir.m3hdi.agahinet.util.Constants
import ir.m3hdi.agahinet.util.PersianTimeAgo
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(private val cityRepository: CityRepository):ViewModel() {

    private lateinit var allCities:HashMap<Int,String>
    lateinit var allProvinces:List<City>

    init {
        // Fetch all cities hashmap and all provinces list from prePopulated ROOM database
        viewModelScope.launch {
            allCities = HashMap()
            allCities.putAll(cityRepository.getAllCities().associateBy({ it.cityId }, { it.title }))

            allProvinces=  mutableListOf(Constants.ENTIRE_IRAN_CITY) + cityRepository.getAllProvinces()
        }
    }

    // Generate a `x time ago in City y` text for ads
    fun getTimeAndLocText(ad: Ad): String {
        val cityName = allCities[ad.city]
        val timeAgoText = PersianTimeAgo.dateTimeStringToTimeAgo(ad.createdAt)
        return cityName?.let { "$timeAgoText در $it" } ?: timeAgoText
    }
}