package com.example.noted.feature_auth.presentation.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noted.core.Result
import com.example.noted.core.utils.OneTimeEvent
import com.example.noted.feature_auth.domain.model.User
import com.example.noted.feature_auth.domain.use_cases.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
): ViewModel() {

    private val _signInLiveData = MutableLiveData<OneTimeEvent<Result<User>>>()
    val signInLiveData: LiveData<OneTimeEvent<Result<User>>> = _signInLiveData

    fun signIn(email: String, password: String){
        signInUseCase.invoke(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                       _signInLiveData.value = OneTimeEvent(it)
            }, {
                _signInLiveData.value = OneTimeEvent(Result.Failure(it))
            })
    }
}