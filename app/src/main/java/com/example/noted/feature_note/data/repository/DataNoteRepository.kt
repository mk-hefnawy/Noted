package com.example.noted.feature_note.data.repository

import com.example.noted.feature_note.data.model.RoomNote
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface DataNoteRepository {
    fun addNotes(notes: RoomNote): Single<Long>
    fun editNote(note: RoomNote): Single<Int>
    fun deleteNotes(notesIds: List<Long>): Completable
    suspend fun getNoteById(noteId: Int): RoomNote
    fun getAllNotes(): Observable<List<RoomNote>>
}