package mo.cmp.weather.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import mo.cmp.weather.api.models.Weather

class WeatherAPI(private val httpClient: HttpClient) {
    suspend fun getDataBySearch(searchValue: String): WeatherAPIStatus {
        return try {
            val response = httpClient.get(BASE_URL){
                parameter("q",searchValue)
                parameter("appid", APP_ID)
                parameter("units","metric")
            }
            println("Response status: ${response.status}")
            println("Response body: ${response.bodyAsText()}")

            WeatherAPIStatus.Success(response.body<Weather>())
        } catch (e: Exception) {
            println("Error: ${e.message}")
            WeatherAPIStatus.Error(e.message ?: "Error")
        }
    }
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
        const val APP_ID = "2e75ee7603fa06447e9d6c346a1aea3b"
    }
}

sealed class WeatherAPIStatus {
    data class Success(var data: Weather) : WeatherAPIStatus()
    data class Error(var message: String) : WeatherAPIStatus()
}