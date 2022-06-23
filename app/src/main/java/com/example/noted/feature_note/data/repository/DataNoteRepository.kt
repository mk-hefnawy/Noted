package com.example.noted.feature_note.data.repository

import com.example.noted.feature_note.data.model.DataNote
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DataNoteRepository {
    fun addNotes(notes: DataNote): Single<Long>
    fun editNote(note: DataNote): Single<Int>
    fun deleteNotes(notesIds: List<Long>): Completable
    suspend fun getNoteById(noteId: Int): DataNote
    fun getAllNotes(): Observable<List<DataNote>>
}