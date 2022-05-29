package gr.sppzglou.weather

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import gr.sppzglou.weather.base.BaseActivity
import gr.sppzglou.weather.framework.City
import gr.sppzglou.weather.framework.Hourly
import gr.sppzglou.weather.framework.Weather
import gr.sppzglou.weather.framework.WeatherResponse
import gr.sppzglou.weather.ui.theme.BlackTrans
import gr.sppzglou.weather.ui.theme.BlueTrans
import org.joda.time.DateTime
import java.text.DateFormatSymbols

@AndroidEntryPoint
class DashboardActivity : BaseActivity<DashboardVM>(DashboardVM::class.java) {
    private var flow = mutableStateOf(Flow.FirstScreen)
    private var bg = mutableStateOf(R.mipmap.bg)

    @SuppressLint("MutableCollectionMutableState")
    private val citiesList = mutableStateOf<MutableList<City>?>(null)
    private val weather = mutableStateOf<WeatherResponse?>(null)

    enum class Flow {
        FirstScreen, CitiesList, AddCity, Pager
    }

    override fun onBackPressed() {
        when (flow.value) {
            Flow.FirstScreen, Flow.Pager -> super.onBackPressed()
            Flow.CitiesList -> flow.value = Flow.Pager
            Flow.AddCity -> if (citiesList.value.isNullOrEmpty())
                flow.value = Flow.FirstScreen
            else flow.value = Flow.CitiesList
        }
    }

    @Composable
    override fun SetupCompose() {
        val foreground by animateColorAsState(
            getBgForeground(bg.value), tween(1000)
        )

        val matrix = ColorMatrix()
        matrix.setToSaturation(0F)
        GlideImage(
            bg.value,
            Modifier.fillMaxSize(),
            colorFilter = ColorFilter.colorMatrix(matrix)
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(foreground))
        when (flow.value) {
            Flow.FirstScreen -> FirstScreen(flow)
            Flow.CitiesList -> CitiesList()
            Flow.AddCity -> SearchScreen()
            Flow.Pager -> PagerScreen()
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun PagerScreen() {
        val pagerState = rememberPagerState()

        HorizontalPager(count = citiesList.value?.size ?: 0, state = pagerState) { position ->
            PagerItem()
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { position ->
                if (citiesList.value != null && citiesList.value!!.isNotEmpty())
                    vm.getWeather(citiesList.value!![position].title)
                else if (citiesList.value != null && citiesList.value!!.isEmpty()) {
                    flow.value = Flow.FirstScreen
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            Arrangement.End
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = White)
                    ) { flow.value = Flow.CitiesList }
            ) {
                Text(
                    "My Cities",
                    Modifier.padding(10.dp),
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    private fun PagerItem() {
        val data = weather.value
        val current = data?.data?.current_condition?.first()
        val weather = data?.data?.weather
        bg.value = getBg(current?.weatherCode ?: "")
        LazyColumn {
            item {
                Column(
                    Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
                ) {
                    Text(
                        data?.data?.request?.first()?.query.tStr(),
                        Modifier
                            .padding(top = 100.dp)
                            .padding(horizontal = 80.dp),
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        " ${current?.temp_C.tStr()}°",
                        color = White,
                        fontSize = 100.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "${weather?.first()?.mintempC.tStr()}° / ${weather?.first()?.maxtempC.tStr()}°",
                        color = White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        current?.weatherDesc?.first()?.value.tStr(),
                        color = White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.padding(top = 50.dp))
                    Text(
                        "Observation Time: ${current?.observation_time.tStr()}",
                        color = White,
                    )
                    if (weather != null) {
                        LazyRow {
                            val list = mutableListOf<Hourly>()
                            weather.first().hourly?.let { list.addAll(it) }
                            if (weather.size > 1) weather[1].hourly?.let { list.addAll(it) }
                            items(list.size) { position ->
                                HourlyItem(list[position])
                            }
                        }
                        weather.forEachIndexed { i, day ->
                            if (i != 0) {
                                DailyItem(day)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun HourlyItem(h: Hourly) {
        Column(
            Modifier.padding(10.dp), Arrangement.Center, Alignment.CenterHorizontally
        ) {
            Text("${formatTime(h.time ?: "")}", color = White)
            Spacer(Modifier.padding(top = 10.dp))
            Image(
                painter = painterResource(getIcon(h.weatherCode ?: "")),
                colorFilter = ColorFilter.tint(White),
                contentDescription = "weather"
            )
            Spacer(Modifier.padding(top = 10.dp))
            Text("${h.tempC}°", color = White)
        }
    }

    @Composable
    private fun DailyItem(day: Weather) {
        val date = DateTime(day.date)
        Column {
            Box(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(White)
            )
            Spacer(Modifier.padding(top = 5.dp))
            Box {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .padding(horizontal = 10.dp),
                    Arrangement.Start,
                    Alignment.CenterVertically
                ) {
                    Text(
                        "${getDay(date.dayOfWeek)}, ${date.dayOfMonth} ${getMonthName(date.monthOfYear)}",
                        color = White
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .padding(horizontal = 10.dp),
                    Arrangement.Center,
                    Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(getIcon(day.hourly?.first()?.weatherCode ?: "")),
                        colorFilter = ColorFilter.tint(White),
                        contentDescription = "weather"
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .padding(horizontal = 10.dp),
                    Arrangement.End,
                    Alignment.CenterVertically
                ) {
                    Text("${day.mintempC}° / ${day.maxtempC}°", color = White)
                }
            }
        }
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
            Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(BlackTrans)
                    .border(1.dp, White, RoundedCornerShape(100))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = White)
                    ) { flow.value = Flow.AddCity },
            ) {
                Column(
                    Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
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
                        CityItemList(
                            citiesList.value!![position],
                            if (position == citiesList.value!!.size - 1) 100 else 0
                        )
                    }
                }
            }
            Column(
                Modifier.fillMaxSize(), Arrangement.Bottom, Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(BlackTrans)
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
                            "Add City", textAlign = TextAlign.Center, color = White
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun CityItemList(city: City, padding: Int) {
        Modifier
        Box(
            Modifier
                .padding(bottom = padding.dp)
                .padding(top = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(BlackTrans)
                .combinedClickable(remember { MutableInteractionSource() },
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
                Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterHorizontally
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
                    .background(BlackTrans)
            ) {
                Row {
                    Image(
                        modifier = Modifier.padding(10.dp),
                        painter = painterResource(R.drawable.ic_baseline_search_24),
                        colorFilter = ColorFilter.tint(White),
                        contentDescription = "search"
                    )
                    Column(Modifier.height(40.dp), Arrangement.Center) {
                        BasicTextField(modifier = Modifier.fillMaxWidth(),
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
                                            text = "Search", color = White
                                        )
                                    }
                                }
                                innerTextField()
                            })
                    }
                }
            }
            if (place != "") {
                Box(
                    Modifier
                        .padding(top = 30.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(BlackTrans)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = White)
                        ) {
                            vm.addCity(place)
                        },
                ) {
                    Column(
                        Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterHorizontally
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
                    Flow.FirstScreen -> flow.value = Flow.Pager
                }
            }
        }
        vm.getWeather.observe(this) {
            weather.value = it
        }
        vm.getCities()
        vm.addCity.observe(this) {
            flow.value = Flow.CitiesList
        }
    }

    private fun formatTime(s: String): String {
        return when (s.length) {
            1 -> "00:00"
            3 -> "0${s[0]}:${s[1]}${s[2]}"
            4 -> "${s[0]}${s[1]}:${s[2]}${s[3]}"
            else -> s
        }
    }


    private fun getMonthName(month: Int): String {
        return DateFormatSymbols().months[month - 1] ?: ""
    }

    private fun getDay(day: Int): String {
        return DateFormatSymbols().shortWeekdays[day + 1] ?: ""
    }

    private fun getIcon(code: String): Int {
        return when (code) {
            "395" -> R.drawable.w1
            "392" -> R.drawable.w2
            "389" -> R.drawable.w3
            "386" -> R.drawable.w4
            "377" -> R.drawable.w5
            "374" -> R.drawable.w6
            "371" -> R.drawable.w7
            "368" -> R.drawable.w8
            "365" -> R.drawable.w9
            "362" -> R.drawable.w10
            "359" -> R.drawable.w11
            "356" -> R.drawable.w12
            "353" -> R.drawable.w13
            "350" -> R.drawable.w14
            "338" -> R.drawable.w15
            "335" -> R.drawable.w16
            "332" -> R.drawable.w17
            "329" -> R.drawable.w18
            "326" -> R.drawable.w19
            "323" -> R.drawable.w20
            "320" -> R.drawable.w21
            "317" -> R.drawable.w22
            "314" -> R.drawable.w23
            "311" -> R.drawable.w24
            "308" -> R.drawable.w25
            "305" -> R.drawable.w26
            "302" -> R.drawable.w27
            "299" -> R.drawable.w28
            "296" -> R.drawable.w29
            "293" -> R.drawable.w30
            "284" -> R.drawable.w31
            "281" -> R.drawable.w32
            "266" -> R.drawable.w33
            "263" -> R.drawable.w34
            "260" -> R.drawable.w35
            "248" -> R.drawable.w36
            "230" -> R.drawable.w37
            "227" -> R.drawable.w38
            "200" -> R.drawable.w39
            "185" -> R.drawable.w40
            "182" -> R.drawable.w41
            "179" -> R.drawable.w42
            "176" -> R.drawable.w43
            "143" -> R.drawable.w44
            "122" -> R.drawable.w45
            "119" -> R.drawable.w46
            "116" -> R.drawable.w47
            else -> R.drawable.w48
        }
    }

    private fun getBg(code: String): Int {
        return when (code) {
            "395", "392", "371", "368", "338", "335", "329",
            "326", "323", "227", "179", "377", "374", "350",
            "332", "320", "317", "284", "185", "182" -> R.mipmap.bg1
            "260", "248", "230", "143" -> R.mipmap.bg4
            "119", "116", "122" -> R.mipmap.bg
            "389", "386", "200" -> R.mipmap.bg3
            "359", "356", "353", "314", "308", "305", "302",
            "299", "296", "293", "176", "365", "362", "263", "281", "266", "311" -> R.mipmap.bg2
            else -> R.mipmap.bg5
        }
    }

    private fun getBgForeground(bg: Int): Color {
        return when (bg) {
            R.mipmap.bg1 -> BlackTrans
            R.mipmap.bg2 -> Color(0x79002AFF)
            R.mipmap.bg3 -> Color(0x00000000)
            R.mipmap.bg4 -> Color(0x00000000)
            R.mipmap.bg5 -> Color(0x90FF5000)
            else -> BlueTrans
        }
    }

    private fun Any?.tStr(old: String = "", new: String = "", nil: String = "") =
        this?.toString()?.replace(old, new) ?: nil
}