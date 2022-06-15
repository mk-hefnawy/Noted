package com.example.noted.feature_note.domain.repository

import com.example.noted.feature_note.domain.model.Note
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


// abstraction to be consumed by the the data layer
interface NoteRepository {
    fun addNotes(note: Note): Single<Long>
    fun editNote(note: Note): Single<Int>
    fun deleteNotes(notesIds: List<Long>): Completable
    suspend fun getNoteById(noteId: Int): Note
    fun getAllNotes(): Observable<List<Note>>
}