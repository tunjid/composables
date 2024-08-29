import androidx.compose.ui.window.ComposeUIViewController
import com.tunjid.demo.common.ui.AppTheme
import com.tunjid.demo.common.ui.App

fun MainViewController() = ComposeUIViewController {
    AppTheme {
        App()
    }
}
