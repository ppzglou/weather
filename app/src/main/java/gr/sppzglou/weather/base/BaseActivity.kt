package gr.sppzglou.weather.base

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import gr.sppzglou.weather.R
import gr.sppzglou.weather.framework.exception.NoInternetException
import retrofit2.HttpException


abstract class BaseActivity<VM : BaseViewModel>(clazz: Class<VM>) : ComponentActivity() {
    protected val vm: VM by lazy { ViewModelProvider(this).get(clazz) }
    var showProgress = mutableStateOf(false)

    @Composable
    abstract fun SetupCompose()
    abstract fun setupObservers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setupObservers()
        vm.load.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                showProgress.value = it
            }
        }
        vm.error.observe(this) { e ->
            val er = when (e) {
                is HttpException -> "HTTP error code: ${e.code()}"
                is NoInternetException -> "No Internet!"
                else -> "Error!"
            }
            Toast.makeText(this, er, Toast.LENGTH_LONG).show()
        }
        setContent {
            SetupCompose()
            if (showProgress.value) Progress()
        }
    }

    @Composable
    fun Progress() {
        Column(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures(onTap = {}) },
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            AndroidView(
                {
                    ImageView(it).apply {
                        Glide.with(it).asGif().load(R.mipmap.progress).into(this)
                    }
                }, Modifier.size(120.dp)
            )
        }
    }
}