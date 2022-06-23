package com.example.noted.feature_note.data.data_source.remote

import com.example.noted.feature_note.data.model.DataNote
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface RemoteNoteDao {

    fun addNote(note: DataNote): Completable

    fun getAllNotes(): Observable<List<DataNote>>

    fun getNoteById(noteId: Int): DataNote

    fun deleteNote(note: DataNote)
}