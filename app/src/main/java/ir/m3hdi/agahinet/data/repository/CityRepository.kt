package ir.m3hdi.agahinet.data.repository

import ir.m3hdi.agahinet.data.local.dao.CityDao
import ir.m3hdi.agahinet.data.local.entity.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CityRepository @Inject constructor(private val cityDao: CityDao) {

    suspend fun getAllProvinces():List<City> {
        return withContext(Dispatchers.IO){
            cityDao.getAllProvinces()
        }
    }

    suspend fun getCitiesOfProvince(provinceId: Int): List<City> {
        return withContext(Dispatchers.IO){
            cityDao.getCitiesOfProvince(provinceId)
        }
    }


}