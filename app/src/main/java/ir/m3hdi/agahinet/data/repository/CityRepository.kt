package ir.m3hdi.agahinet.data.repository

import ir.m3hdi.agahinet.data.local.dao.CityDao
import ir.m3hdi.agahinet.data.local.entity.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CityRepository @Inject constructor(private val cityDao: CityDao) {

    private var allCities:HashMap<Int,String>? = null
    private var allProvinces:List<City>? = null

    suspend fun getAllProvinces():List<City> {
        return allProvinces ?: withContext(Dispatchers.IO) {
            cityDao.getAllProvinces().also { allProvinces = it }
        }
    }

    suspend fun getCitiesOfProvince(provinceId: Int): List<City> {
        return withContext(Dispatchers.IO){
            cityDao.getCitiesOfProvince(provinceId)
        }
    }

    suspend fun getAllCities(): HashMap<Int, String> {
        return allCities ?: withContext(Dispatchers.IO) {
            val hm = HashMap<Int,String>()
            hm.putAll(cityDao.getAllCities().associateBy({ it.cityId }, { it.title }))
            hm.also { allCities=it }
        }
    }

}