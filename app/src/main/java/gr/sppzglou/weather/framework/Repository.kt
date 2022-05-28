package gr.sppzglou.weather.framework

import android.content.SharedPreferences
import javax.inject.Inject


class Repository @Inject constructor(
    private val api: WeatherApi,
    private val sharedPref: SharedPreferences
) {

    suspend fun searchPlace(place: String) = api.searchPlace(place)
}
