package com.example.noted.feature_auth.domain.use_cases

import com.example.noted.core.Result
import com.example.noted.feature_auth.domain.model.User
import com.example.noted.feature_auth.domain.repository.UserRepository
import io.reactivex.rxjava3.core.Single
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    val repository: UserRepository
) {
    operator fun invoke(user: User): Single<Result<User>>{
        if (user.userName == "") throw IllegalArgumentException()
        if (user.email == "") throw IllegalArgumentException()
        if (user.password == "") throw IllegalArgumentException()

        return repository.signUp(user)
    }
}