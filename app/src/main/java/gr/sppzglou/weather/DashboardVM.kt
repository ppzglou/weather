package gr.sppzglou.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.sppzglou.weather.base.BaseViewModel
import gr.sppzglou.weather.framework.City
import gr.sppzglou.weather.framework.Repository
import javax.inject.Inject

@HiltViewModel
class DashboardVM @Inject constructor(
    private val repo: Repository
) : BaseViewModel() {

    private val _searchPlace = MutableLiveData<String>()
    val searchPlace: LiveData<String> = _searchPlace

    private val _getCities = MutableLiveData<MutableList<City>>()
    val getCities: LiveData<MutableList<City>> = _getCities

    private val _addCity = MutableLiveData<Boolean>()
    val addCity: LiveData<Boolean> = _addCity


    fun searchPlace(place: String) {
        launch {
            _searchPlace.value = repo.searchPlace(place).data.request?.first()?.query ?: ""
        }
    }

    fun addCity(city: String) {
        launch {
            repo.insertCity(City(city))
            getCities()
            _addCity.value = true
        }
    }

    fun deleteCity(city: String) {
        launch {
            repo.deleteCity(city)
            getCities()
        }
    }

    fun getCities() {
        launch {
            _getCities.value = repo.getCities()
        }
    }
}
