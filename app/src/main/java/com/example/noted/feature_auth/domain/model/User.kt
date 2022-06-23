package com.example.noted.feature_auth.domain.model

data class User(
    var userId: String?,
    val userName: String,
    val email: String,
    val password: String,
){
    constructor(): this(null, "", "", "")
}
