package com.example.noted.feature_auth.domain.use_cases

import com.example.noted.core.Result
import com.example.noted.feature_auth.domain.model.User
import com.example.noted.feature_auth.domain.repository.UserRepository
import io.reactivex.rxjava3.core.Single
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(email: String, password: String): Single<Result<User>> {
        if (email == "") throw IllegalArgumentException()
        if (password == "") throw IllegalArgumentException()

        return repository.signIn(email, password)
    }
}