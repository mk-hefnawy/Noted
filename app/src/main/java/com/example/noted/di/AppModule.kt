package com.example.noted.di

import android.content.Context
import androidx.room.Room
import com.example.noted.core.NoteRepositoryInterMediator
import com.example.noted.core.internet.InternetState
import com.example.noted.feature_auth.domain.use_cases.HasInternetConnectionUseCase
import com.example.noted.feature_note.data.data_source.cache.NotesRoomDatabase
import com.example.noted.feature_note.data.repository.NotesService
import com.example.noted.feature_note.domain.repository.NoteRepository
import com.example.noted.feature_note.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesRoomDatabase(@ApplicationContext appContext: Context): NotesRoomDatabase {
        return Room.databaseBuilder(appContext,
            NotesRoomDatabase::class.java,
            NotesRoomDatabase.DATA_BASE_NAME).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideNotesRepositoryWithCache(notesService: NotesService): NoteRepository{
            return NoteRepositoryInterMediator(notesService)
    }

    @Provides
    @Singleton
    fun provideNoteRepositoryImplWithCache(notesRoomDatabase: NotesRoomDatabase): NotesService{
        return NotesService(notesRoomDatabase.roomNoteDao)
    }

   /* @Provides
    @Singleton
    fun provideNotesRepositoryWithRemote(remoteNoteDao: RemoteNoteDao): NoteRepository{
        return NoteRepositoryImpl(remoteNoteDao)
    }*/

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases{
        return NoteUseCases(
            getAllNotesUseCase = GetAllNotesUseCase(repository),
            deleteNotesUseCase= DeleteNotesUseCase(repository),
            addNoteUseCase = AddNotesUseCase(repository),
            editNoteUseCase = EditNoteUseCase(repository),
            orderNotesUseCase = OrderNotesUseCase()
        )
    }

    @Provides
    @Singleton
    fun provideInternetUseCase(@ApplicationContext context: Context): HasInternetConnectionUseCase{
        return HasInternetConnectionUseCase(context)
    }
}