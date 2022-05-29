package gr.sppzglou.weather

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import gr.sppzglou.weather.base.BaseActivity
import gr.sppzglou.weather.ui.theme.BlueTrans

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<DashboardVM>(DashboardVM::class.java) {
    private val anim = mutableStateOf(false)
    private val endSplash = mutableStateOf(false)

    override fun setupObservers() {
    }

    @Composable
    override fun SetupCompose() {
        val end by animateFloatAsState(if (endSplash.value) 0f else 1f, tween(1000, 3000))
        endSplash.value = true

        Box(
            Modifier
                .fillMaxSize()
                .background(BlueTrans)
        )

        if (end > 0) SplashScreen(end, anim)
        else {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Composable
    private fun SplashScreen(end: Float, anim: MutableState<Boolean>) {
        val matrix = ColorMatrix()
        matrix.setToSaturation(0F)

        Box(Modifier.alpha(end)) {
            GlideImage(
                R.mipmap.splash,
                Modifier.fillMaxSize(),
                colorFilter = ColorFilter.colorMatrix(matrix)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(BlueTrans))
            Column(
                Modifier.fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                val alpha by animateFloatAsState(if (anim.value) 1f else 0f, tween(3000))
                val p1 by animateDpAsState(if (anim.value) 0.dp else 120.dp, tween(3000))
                val p2 by animateDpAsState(if (anim.value) 0.dp else 150.dp, tween(3000))
                val p4 by animateDpAsState(if (anim.value) 0.dp else 180.dp, tween(3000))
                val p5 by animateDpAsState(if (anim.value) 0.dp else 100.dp, tween(3000))

                anim.value = true
                Row {
                    Text(
                        "W",
                        Modifier
                            .padding(top = p1)
                            .alpha(alpha),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp
                    )
                    Text(
                        "E",
                        Modifier
                            .padding(top = p2)
                            .alpha(alpha),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp
                    )
                    Text(
                        "A",
                        Modifier
                            .padding(bottom = p1)
                            .alpha(alpha),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp
                    )
                    Text(
                        "T",
                        Modifier
                            .padding(top = p4)
                            .alpha(alpha),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp
                    )
                    Text(
                        "H",
                        Modifier
                            .padding(top = p5)
                            .alpha(alpha),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp
                    )
                    Text(
                        "E",
                        Modifier
                            .padding(bottom = 0.dp)
                            .alpha(alpha),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp
                    )
                    Text(
                        "R",
                        Modifier
                            .padding(top = p1)
                            .alpha(alpha),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp
                    )
                }
            }
        }
    }
}