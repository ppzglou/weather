package gr.sppzglou.weather.framework

import retrofit2.http.GET
import retrofit2.http.Query

enum class Api(val value: String) {
    BaseUrl("https://api.worldweatheronline.com/premium/v1/"),
    ApiToken("0565fe3d7492481c99c225850222705"),
    Format("json")
}

interface WeatherApi {

    @GET("weather.ashx")
    suspend fun searchPlace(
        @Query("q") place: String,
        @Query("key") token: String = Api.ApiToken.value,
        @Query("format") format: String = Api.Format.value
    ): SearchPlaceResponse

    @GET("weather.ashx")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("num_of_days") days: Int = 7,
        @Query("key") token: String = Api.ApiToken.value,
        @Query("format") format: String = Api.Format.value
    ): WeatherResponse
}