package gr.sppzglou.weather

import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import gr.sppzglou.weather.base.BaseActivity
import gr.sppzglou.weather.framework.City
import gr.sppzglou.weather.ui.theme.BlackTrans
import gr.sppzglou.weather.ui.theme.BlueTrans

@AndroidEntryPoint
class DashboardActivity : BaseActivity<DashboardVM>(DashboardVM::class.java) {
    private var flow = mutableStateOf(Flow.FirstScreen)

    @SuppressLint("MutableCollectionMutableState")
    private val citiesList = mutableStateOf<MutableList<City>?>(null)

    enum class Flow {
        FirstScreen,
        CitiesList,
        AddCity,
        Pager
    }

    override fun onBackPressed() {
        when (flow.value) {
            Flow.FirstScreen, Flow.Pager -> super.onBackPressed()
            Flow.CitiesList -> flow.value = Flow.Pager
            Flow.AddCity -> flow.value = Flow.FirstScreen
        }
    }

    @Composable
    override fun SetupCompose() {

        AndroidView(
            {
                ImageView(it).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0F)
                    val filter = ColorMatrixColorFilter(matrix)
                    colorFilter = filter
                    foreground =
                        ContextCompat.getDrawable(this.context, R.drawable.foreground)
                    Glide.with(it).asGif().load(R.mipmap.bg).into(this)
                }
            },
            Modifier.fillMaxSize()
        )
        when (flow.value) {
            Flow.FirstScreen -> FirstScreen(flow)
            Flow.CitiesList -> CitiesList()
            Flow.AddCity -> SearchScreen()
        }
        //CitiesList()

    }

    @Composable
    private fun FirstScreen(flow: MutableState<Flow>) {
        Column(Modifier.fillMaxSize()) {
            Text(
                "Weather App",
                Modifier.padding(start = 30.dp, top = 50.dp),
                color = White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Press the button to add a city",
                Modifier.padding(start = 30.dp, top = 20.dp),
                color = White,
                fontSize = 20.sp
            )
        }
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(BlueTrans)
                    .border(1.dp, White, RoundedCornerShape(100))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = White)
                    ) { flow.value = Flow.AddCity },
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    Arrangement.Center,
                    Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        painter = painterResource(R.drawable.ic_baseline_add_24),
                        colorFilter = ColorFilter.tint(White),
                        contentDescription = "add"
                    )
                }
            }
        }
    }

    @SuppressLint("MutableCollectionMutableState", "CoroutineCreationDuringComposition")
    @Composable
    private fun CitiesList() {
        Box(Modifier.background(BlackTrans)) {
            Column {
                Row(
                    Modifier
                        .padding(top = 40.dp)
                        .padding(horizontal = 10.dp),
                    Arrangement.Start,
                    Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = White)
                            ) { onBackPressed() },
                    ) {
                        Image(
                            modifier = Modifier.padding(10.dp),
                            painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
                            colorFilter = ColorFilter.tint(White),
                            contentDescription = "back"
                        )
                    }
                    Text("My Cities", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                LazyColumn(Modifier.padding(horizontal = 20.dp)) {
                    items(citiesList.value?.size ?: 0) { position ->
                        Item(
                            citiesList.value!![position],
                            if (position == citiesList.value!!.size - 1) 100 else 0
                        )
                    }
                }
            }
            Column(
                Modifier.fillMaxSize(),
                Arrangement.Bottom,
                Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(BlueTrans)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = White)
                        ) { flow.value = Flow.AddCity },
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_baseline_add_24),
                            colorFilter = ColorFilter.tint(White),
                            contentDescription = "add"
                        )
                        Text(
                            "Add City",
                            textAlign = TextAlign.Center,
                            color = White
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Item(city: City, padding: Int) {
        Modifier
        Box(
            Modifier
                .padding(bottom = padding.dp)
                .padding(top = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(BlueTrans)
                .combinedClickable(
                    remember { MutableInteractionSource() },
                    rememberRipple(color = White),
                    onClick = {},
                    onLongClick = {
                        vm.deleteCity(city.title)
                        Toast
                            .makeText(this, "Success deleted!", Toast.LENGTH_LONG)
                            .show()
                    }),
        ) {
            Column(
                Modifier
                    .fillMaxWidth(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                Text(
                    city.title,
                    Modifier.padding(20.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    fontSize = 16.sp
                )
            }
        }
    }

    @Composable
    private fun SearchScreen() {
        var text by rememberSaveable { mutableStateOf("") }
        var place by remember { mutableStateOf("") }

        vm.searchPlace.observe(this) {
            place = it
        }

        Column(
            Modifier
                .background(BlackTrans)
                .padding(top = 60.dp)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .background(BlueTrans)
            ) {
                Row {
                    Image(
                        modifier = Modifier.padding(10.dp),
                        painter = painterResource(R.drawable.ic_baseline_search_24),
                        colorFilter = ColorFilter.tint(White),
                        contentDescription = "search"
                    )
                    Column(Modifier.height(40.dp), Arrangement.Center) {
                        BasicTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = text,
                            onValueChange = {
                                text = it
                                vm.searchPlace(it)
                            },
                            textStyle = TextStyle(
                                color = White
                            ),
                            decorationBox = { innerTextField ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    if (text.isEmpty()) {
                                        Text(
                                            text = "Search",
                                            color = White
                                        )
                                    }
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }
            if (place != "") {
                Box(
                    Modifier
                        .padding(top = 30.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(BlueTrans)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = White)
                        ) {
                            vm.addCity(place)
                        },
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Text(
                            place,
                            Modifier.padding(20.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    override fun setupObservers() {
        vm.getCities.observe(this) {
            citiesList.value = it
            if (it.isNotEmpty()) {
                when (flow.value) {
                    Flow.FirstScreen -> flow.value = Flow.CitiesList
                }
            }
        }
        vm.getCities()
        vm.addCity.observe(this) {
            flow.value = Flow.CitiesList
        }
    }
}