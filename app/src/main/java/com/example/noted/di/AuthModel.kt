package com.example.noted.di

import android.content.Context
import androidx.room.Room
import com.example.noted.feature_auth.data.UserService
import com.example.noted.feature_auth.domain.repository.UserRepository
import com.example.noted.feature_note.data.data_source.cache.NotesRoomDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModel {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth, db: FirebaseFirestore): UserRepository {
        return UserService(auth, db)
    }
}