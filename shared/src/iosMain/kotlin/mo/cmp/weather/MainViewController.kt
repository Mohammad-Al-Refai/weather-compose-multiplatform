package mo.cmp.weather

import androidx.compose.ui.window.ComposeUIViewController
import mo.cmp.weather.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}