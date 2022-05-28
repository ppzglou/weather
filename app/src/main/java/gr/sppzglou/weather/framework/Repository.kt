package gr.sppzglou.weather.framework

import gr.sppzglou.weather.dao.AppDatabase
import javax.inject.Inject


class Repository @Inject constructor(
    private val api: WeatherApi,
    private val db: AppDatabase
) {

    suspend fun searchPlace(place: String) = api.searchPlace(place)

    suspend fun insertCity(city: City) = db.citiesDao().insert(city)

    suspend fun deleteCity(title: String) = db.citiesDao().delete(title)

    suspend fun getCities() = db.citiesDao().getCities()
}
