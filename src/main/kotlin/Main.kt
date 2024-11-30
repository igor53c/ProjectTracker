import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.*
import presentation.custom_title_bar.CustomTitleBar
import presentation.main_screen.AppViewModel
import presentation.main_screen.mainScreen
import presentation.theme.AppTheme
import java.awt.Frame.ICONIFIED
import java.awt.Frame.MAXIMIZED_BOTH

fun main() = application {

    val viewModel = remember { AppViewModel() }

    Window(
        title = "ProjectTracker",
        onCloseRequest = ::exitApplication,
        undecorated = true,
//        icon = painterResource("drawables/launcher_icons/icon.ico"),
        state = WindowState(
            placement = WindowPlacement.Maximized,
            position = WindowPosition.PlatformDefault,
        )
    ) {
        DisposableEffect(Unit) {
            onDispose {
                viewModel.onCleared()
            }
        }
            AppTheme {
                Column {
                    CustomTitleBar(
                        onClose = { exitApplication() },
                        onMinimize = { window.extendedState = ICONIFIED },
                        onMaximize = { window.extendedState = MAXIMIZED_BOTH }
                    )
                mainScreen(
                    viewModel = remember { AppViewModel() }
                )
            }
        }
    }
}

