package gr.sppzglou.weather.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gr.sppzglou.weather.utils.Event
import gr.sppzglou.weather.utils.SingleLiveEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val _load = MutableLiveData<Event<Boolean>>()
    val load: LiveData<Event<Boolean>> = _load

    val error = SingleLiveEvent<Exception>()


    fun launch(delayTime: Int = 0, function: suspend () -> Unit) {
        viewModelScope.launch {
            _load.value = Event(true)
            delay(delayTime.toLong())
            try {
                function.invoke()
            } catch (e: Exception) {
                error.value = e
            }
        }.invokeOnCompletion {
            _load.value = Event(false)
        }
    }
}