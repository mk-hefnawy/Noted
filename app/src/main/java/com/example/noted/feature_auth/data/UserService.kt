package com.example.noted.feature_auth.data

import com.example.noted.core.Result
import com.example.noted.feature_auth.domain.model.User
import com.example.noted.feature_auth.domain.repository.UserRepository
import com.example.noted.feature_auth.utils.DatabaseConstants
import com.example.noted.feature_auth.utils.DatabaseConstants.Email
import com.example.noted.feature_auth.utils.DatabaseConstants.USERS
import com.example.noted.feature_auth.utils.DatabaseConstants.UserId
import com.example.noted.feature_auth.utils.DatabaseConstants.UserName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import javax.inject.Inject


class UserService @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : UserRepository {

    override fun signUp(user: User): Single<Result<User>> {
        return Single.create { emitter ->
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener {
                    user.userId = it.user?.uid
                    addUserToFirestore(user, emitter)
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    override fun signIn(email: String, password: String): Single<Result<User>> {
        return Single.create { emitter ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    getUserFromFirestore(it.user?.uid, emitter)
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    private fun addUserToFirestore(user: User, emitter: SingleEmitter<Result<User>>) {
        val hashUser = hashMapOf(
            UserId to user.userId,
            UserName to user.userName,
            Email to user.email
        )
        if (user.userId != null) {
            db.collection(DatabaseConstants.USERS).document(user.userId!!)
                .set(hashUser).addOnSuccessListener {
                    emitter.onSuccess(Result.Success(user))
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        } else {
            emitter.onError(Throwable("Error from Server"))
        }

    }

    private fun getUserFromFirestore(uid: String?, emitter: SingleEmitter<Result<User>>) {
        if (uid != null) {
            db.collection(USERS).document(uid).get()
                .addOnSuccessListener { document ->
                    val user: User? = document.toObject(User::class.java)
                    emitter.onSuccess(Result.Success(user)) // gotta check nullability
                }.addOnFailureListener {
                    emitter.onError(it)
                }
        } else {
            emitter.onError(Throwable("Error from Server"))
        }
    }

    override fun isAlreadySignedIn(): Boolean {
        return auth.currentUser != null
    }
}