package mo.cmp.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import mo.cmp.weather.api.WeatherAPI
import mo.cmp.weather.api.WeatherAPIStatus
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import weathercmp.shared.generated.resources.Res
import weathercmp.shared.generated.resources.requestFailed
import weathercmp.shared.generated.resources.requestSucceeded

sealed class LandingSideEffect {
    data class ErrorSnackBar(val text: StringResource) : LandingSideEffect()
    data class SuccessSnackBar(val text: StringResource) : LandingSideEffect()
}

data class LandingState(
    var searchValue: String = "Syria",
    var temp: Double = 0.0,
    var countryName: String = "",
    var feelsLike: Double = 0.0,
    var windSpeed: Double = 0.0,
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var isSuccess: Boolean = false
)

class LandingViewModel : ViewModel(),
    ContainerHost<LandingState, LandingSideEffect>, KoinComponent {
    private val weatherAPI by inject<WeatherAPI>()
    override val container =
        viewModelScope.container<LandingState, LandingSideEffect>(LandingState())

    fun updateSearch(value: String) = intent {
        reduce {
            state.copy(searchValue = value)
        }
    }

    fun search() = intent {
        reduce {
            state.copy(isLoading = true, isError = false, isSuccess = false)
        }
        when (val result = weatherAPI.getDataBySearch(searchValue = state.searchValue)) {
            is WeatherAPIStatus.Error -> {
                reduce {
                    state.copy(isLoading = false, isError = true, isSuccess = false)
                }
                postSideEffect(LandingSideEffect.ErrorSnackBar(Res.string.requestFailed))
            }

            is WeatherAPIStatus.Success -> {
                reduce {
                    state.copy(
                        isLoading = false,
                        isSuccess = true,
                        windSpeed = result.data.wind.speed,
                        feelsLike = result.data.main.feelsLike,
                        countryName = result.data.sys.country,
                        temp = result.data.main.temp
                    )
                }
                postSideEffect(LandingSideEffect.SuccessSnackBar(Res.string.requestSucceeded))
            }
        }

    }
}