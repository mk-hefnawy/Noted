package com.example.noted.feature_auth.domain.repository

import com.example.noted.feature_auth.domain.model.User
import io.reactivex.rxjava3.core.Single

interface UserRepository {

    fun signUp(user: User): Single<com.example.noted.core.Result<User>>
    fun signIn(email: String, password: String): Single<com.example.noted.core.Result<User>>
    fun isAlreadySignedIn(): Boolean
}