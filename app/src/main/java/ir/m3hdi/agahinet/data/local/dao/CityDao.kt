package ir.m3hdi.agahinet.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ir.m3hdi.agahinet.data.local.entity.City

@Dao
interface CityDao {

    @Query("SELECT * FROM city WHERE parent_province_id IS NOT NULL")
    suspend fun getAllCities(): List<City>

    @Query("SELECT * FROM city WHERE parent_province_id IS NULL")
    suspend fun getAllProvinces(): List<City>

    @Query("SELECT * FROM city WHERE parent_province_id=:provinceId")
    suspend fun getCitiesOfProvince(provinceId:Int): List<City>

    @Query("SELECT * FROM city WHERE city_id = (SELECT parent_province_id FROM city WHERE city_id=:cityId)")
    suspend fun getParentProvinceOfCity(cityId:Int): City

    @Query("SELECT * FROM city WHERE city_id=:cityId LIMIT 1")
    suspend fun getCityById(cityId:Int): City

}