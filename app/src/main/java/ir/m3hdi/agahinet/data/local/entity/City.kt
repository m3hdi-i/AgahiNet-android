package ir.m3hdi.agahinet.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "city",
    foreignKeys = [
        ForeignKey(entity = City::class,
            parentColumns = ["city_id"],
            childColumns = ["parent_province_id"]
            ,onDelete = ForeignKey.CASCADE
            ,onUpdate = ForeignKey.CASCADE) ])
data class City(
    @ColumnInfo(name = "city_id") @PrimaryKey val cityId: Int,
    @ColumnInfo(name = "parent_province_id") val parentProvinceId: Int?,
    val title: String
){
    override fun toString(): String {
        return title
    }
}