package mo.cmp.weather.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val main: Main,
    val sys: Sys,
    val wind: Wind
)

@Serializable
data class Main(
    val temp: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    @SerialName("feels_like") val feelsLike: Double
)

@Serializable
data class Sys(
    val country: String,
)

@Serializable
data class Wind(
    val speed: Double
)