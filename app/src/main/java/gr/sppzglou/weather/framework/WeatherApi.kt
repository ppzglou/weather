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
        @Query("num_of_days") days: Int = 0,
        @Query("key") token: String = Api.ApiToken.value,
        @Query("format") format: String = Api.Format.value
    ): SearchPlaceResponse
}

data class SearchPlaceResponse(
    var data: Data
)

data class Data(
    var request: MutableList<Request>
)

data class Request(
    var type: String,
    var query: String
)