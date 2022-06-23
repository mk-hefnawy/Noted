package com.example.noted.feature_auth.domain.use_cases

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.*
import android.os.Build
import android.util.Log
import com.example.noted.core.internet.InternetState
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.UnicastSubject
import javax.inject.Inject

class HasInternetConnectionUseCase @Inject constructor(
    applicationContext: Context
) {
    val internetStateSubject: PublishSubject<InternetState> = PublishSubject.create()
    val connectivityManager: ConnectivityManager =
        applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    operator fun invoke() {
        val isThereANetwork = checkIfThereIsNetworkAtFirstPlace()
        if (!isThereANetwork) {
            internetStateSubject.onNext(InternetState.UnAvailable)
        }

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                internetStateSubject.onNext(InternetState.Available)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                internetStateSubject.onNext(InternetState.Lost)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                internetStateSubject.onNext(InternetState.UnAvailable)
            }


        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    }


    private fun checkIfThereIsNetworkAtFirstPlace(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // checking for network
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetworkCapabilities: NetworkCapabilities = connectivityManager
                .getNetworkCapabilities(network) ?: return false

            // checking if that network has internet
            return when {
                activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}