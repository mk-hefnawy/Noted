package com.example.noted.feature_auth.domain.use_cases

import com.example.noted.feature_auth.domain.repository.UserRepository
import javax.inject.Inject

class IsAlreadySignedInUseCase @Inject constructor(
    val repository: UserRepository
) {
    operator fun invoke():Boolean{
        return repository.isAlreadySignedIn()
    }
}