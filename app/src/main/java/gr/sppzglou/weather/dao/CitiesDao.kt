package gr.sppzglou.weather.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gr.sppzglou.weather.framework.City

@Dao
interface CitiesDao {
    @Query("SELECT * FROM cities")
    suspend fun getCities(): MutableList<City>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: City)

    @Query("DELETE FROM cities WHERE title = :title")
    suspend fun delete(title: String)
}