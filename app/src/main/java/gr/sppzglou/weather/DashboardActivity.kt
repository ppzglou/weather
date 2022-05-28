package gr.sppzglou.weather

import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import gr.sppzglou.weather.base.BaseActivity

@AndroidEntryPoint
class DashboardActivity : BaseActivity<DashboardVM>(DashboardVM::class.java) {

    @Composable
    override fun SetupCompose() {

    }

    override fun setupObservers() {}
}