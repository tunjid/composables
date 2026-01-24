import androidx.compose.ui.window.ComposeUIViewController
import com.tunjid.demo.common.ui.App
import com.tunjid.demo.common.ui.AppTheme

@Suppress("ktlint:standard:function-naming")
fun MainViewController() = ComposeUIViewController {
    AppTheme {
        App()
    }
}
