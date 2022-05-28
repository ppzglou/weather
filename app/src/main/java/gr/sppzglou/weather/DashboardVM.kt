package gr.sppzglou.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.sppzglou.weather.base.BaseViewModel
import gr.sppzglou.weather.framework.Repository
import gr.sppzglou.weather.framework.SearchPlaceResponse
import javax.inject.Inject

@HiltViewModel
class DashboardVM @Inject constructor(
    private val repo: Repository
) : BaseViewModel() {

    private val _searchPlace = MutableLiveData<SearchPlaceResponse>()
    val searchPlace: LiveData<SearchPlaceResponse> = _searchPlace


    fun searchPlace(place: String) {
        launch {
            _searchPlace.value = repo.searchPlace(place)
        }
    }
}
