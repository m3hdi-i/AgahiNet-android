package ir.m3hdi.agahinet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.m3hdi.agahinet.data.local.dao.CityDao
import ir.m3hdi.agahinet.data.local.entity.City

@Database(entities = [City::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}