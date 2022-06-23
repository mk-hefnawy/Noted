package com.example.noted.feature_auth.presentation.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noted.core.Result
import com.example.noted.core.utils.OneTimeEvent
import com.example.noted.feature_auth.domain.model.User
import com.example.noted.feature_auth.domain.use_cases.IsAlreadySignedInUseCase
import com.example.noted.feature_auth.domain.use_cases.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private var isAlreadySignedInUseCase: IsAlreadySignedInUseCase
) : ViewModel() {

    private val _signUpLiveData = MutableLiveData<OneTimeEvent<Result<User>>>()
    val signUpLiveData: LiveData<OneTimeEvent<Result<User>>> = _signUpLiveData

    fun signUp(user: User) {
        signUpUseCase.invoke(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _signUpLiveData.value = OneTimeEvent(it)
            }, {
                _signUpLiveData.value = OneTimeEvent(Result.Failure(it))
            })

    }

    fun checkIfAlreadySignedIn(): Boolean{
        return isAlreadySignedInUseCase()
    }
}