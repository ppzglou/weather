package gr.sppzglou.weather.di.connectivity

/*class ConnectivityLiveData(application: Application, connectivityManager: ConnectivityManager) :
    MutableLiveData<Event<ConnectivityStatus>>() {

    private val connectionMonitor =
        ConnectivityMonitor.getInstance(application.applicationContext, connectivityManager)

    override fun onActive() {
        super.onActive()
        connectionMonitor.startListening(::setConnected)
    }

    override fun onInactive() {
        connectionMonitor.stopListening()
        super.onInactive()
    }

    private fun setConnected(isConnected: Boolean) =
        postValue(
            when {
                isConnected -> Event(ConnectivityStatus.Connected)
                else -> Event(ConnectivityStatus.Disconnected)
            }
        )
}*/

enum class ConnectivityStatus {
    Connected,
    Disconnected
}