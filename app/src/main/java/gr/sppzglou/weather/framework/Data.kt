package gr.sppzglou.weather.framework

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(
    @PrimaryKey
    var title: String
)

data class SearchPlaceResponse(
    var data: Data
)

data class Data(
    var request: MutableList<Request>?
)

data class Request(
    var type: String?,
    var query: String?
)

data class WeatherResponse(
    var data: WeatherData
)

data class WeatherData(
    var request: MutableList<Request>?,
    var current_condition: MutableList<CurrentCondition>?,
    var weather: MutableList<Weather>?
)


data class CurrentCondition(
    var observation_time: String?,
    var temp_C: String?,
    var weatherCode: String?,
    var weatherDesc: MutableList<WeatherDesc>?,
    var FeelsLikeC: String?,
)


data class WeatherDesc(
    var value: String?
)

data class Weather(
    val date: String?,
    val astronomy: MutableList<Astronomy>?,
    val maxtempC: String?,
    val mintempC: String?,
    val hourly: MutableList<Hourly>?
)

data class Astronomy(
    val sunrise: String?,
    val sunset: String?,
    val moonrise: String?,
    val moonset: String?,
    val moon_phase: String?,
    val moon_illumination: String?
)

data class Hourly(
    val time: String?,
    val tempC: String?,
    val weatherCode: String?,
    val weatherDesc: MutableList<WeatherDesc>?,
)