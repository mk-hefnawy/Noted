package com.example.noted.feature_note.data.data_source.remote

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noted.feature_note.data.model.RemoteNote
import com.example.noted.feature_note.data.model.RoomNote
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
// has to know only RemoteNote
interface RemoteNoteDao {

    suspend fun addNote(note: RoomNote)

    fun getAllNotes(): Observable<List<RoomNote>>

    suspend fun getNoteById(noteId: Int): RoomNote

    suspend fun deleteNote(note: RoomNote)
}