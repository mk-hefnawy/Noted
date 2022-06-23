package com.example.noted.core.internet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noted.core.Result
import com.example.noted.core.utils.OneTimeEvent
import com.example.noted.feature_auth.domain.use_cases.HasInternetConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class InternetViewModel @Inject constructor(
    private val internetConnectionUseCase: HasInternetConnectionUseCase
) : ViewModel() {

    init {
        observeInternetState() // subscription before the actual work because PublishSubject is Hot
        getInternetState()
    }

    private val _internetState = MutableLiveData<OneTimeEvent<Result<InternetState>>>()
    val internetState: LiveData<OneTimeEvent<Result<InternetState>>> = _internetState

    private lateinit var internetStateDisposable: Disposable
    private fun getInternetState() {
        internetConnectionUseCase()
    }

    private fun observeInternetState() {
        internetStateDisposable = internetConnectionUseCase.internetStateSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _internetState.value = OneTimeEvent(Result.Success(it))
            }, {
                _internetState.value = OneTimeEvent(Result.Failure(it))
                it.printStackTrace()
            })
    }

    fun unSubscribeFromInternetStateSubject(){
        internetStateDisposable.dispose()
    }
}