package gr.sppzglou.weather.di.modules

import gr.sppzglou.weather.framework.exception.NoInternetException
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptor(private val connectivityHelper: ConnectivityHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivityHelper.checkInternetConnection()) {
            throw NoInternetException()
        }
        return chain.proceed(chain.request())
    }
}