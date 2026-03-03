package mo.cmp.weather.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import weathercmp.composeapp.generated.resources.Res
import weathercmp.composeapp.generated.resources.country
import weathercmp.composeapp.generated.resources.feelsLike
import weathercmp.composeapp.generated.resources.search
import weathercmp.composeapp.generated.resources.temperature
import weathercmp.composeapp.generated.resources.tryAgain
import weathercmp.composeapp.generated.resources.unexpectedError
import weathercmp.composeapp.generated.resources.windSpeed

@Composable
fun LandingPage(vm: LandingViewModel = koinViewModel()) {
    val state by vm.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.search()
    }
    // Listen for side effects
    vm.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LandingSideEffect.ErrorSnackBar -> when (snackbarHostState.showSnackbar(
                getString(sideEffect.text),
                actionLabel = getString(Res.string.tryAgain)
            )) {
                SnackbarResult.Dismissed -> {}
                SnackbarResult.ActionPerformed -> vm.search()
            }

            is LandingSideEffect.SuccessSnackBar -> snackbarHostState.showSnackbar(getString(sideEffect.text))
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it)
            }
        },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = state.searchValue,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = vm::updateSearch
                )
                Button(onClick = vm::search) {
                    Text(stringResource(Res.string.search))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (state.isError) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .fillMaxHeight(0.1f)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colors.error),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(Res.string.unexpectedError),
                        color = MaterialTheme.colors.onError
                    )
                }
            }
            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.isSuccess) {
                PageContent(
                    countryName = state.countryName,
                    temp = state.temp,
                    feelsLike = state.feelsLike,
                    windSpeed = state.windSpeed
                )
            }

        }
    }
}

@Composable
fun PageContent(countryName: String, temp: Double, feelsLike: Double, windSpeed: Double) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible.value = !visible.value
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(visible = visible.value, enter = fadeIn()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RoundedBox(
                    title = stringResource(Res.string.country),
                    content = countryName,
                    color = MaterialTheme.colors.primary
                )
                Spacer(Modifier.padding(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RoundedBox(
                        title = stringResource(Res.string.temperature),
                        content = "${temp}C",
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.padding(5.dp))
                    RoundedBox(
                        title = stringResource(Res.string.feelsLike),
                        content = "${feelsLike}C",
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.padding(5.dp))
                RoundedBox(
                    title = stringResource(Res.string.windSpeed),
                    content = windSpeed.toString(),
                    color = MaterialTheme.colors.secondaryVariant,
                )
            }
        }
    }
}

@Composable
fun RoundedBox(title: String, content: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = color)
    ) {
        Text(
            title,
            modifier = Modifier.padding(10.dp),
            color = MaterialTheme.colors.onPrimary
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                content,
                fontSize = MaterialTheme.typography.h3.fontSize,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

