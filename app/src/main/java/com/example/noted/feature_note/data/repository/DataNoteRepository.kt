package com.example.noted.feature_note.data.repository

import com.example.noted.feature_note.data.model.RoomNote
import kotlinx.coroutines.flow.Flow

interface DataNoteRepository {
    suspend fun addNotes(notes: List<RoomNote>)
    suspend fun deleteNotes(notes: List<RoomNote>)
    suspend fun getNoteById(noteId: Int): RoomNote
    fun getAllNotes(): Flow<List<RoomNote>>
}