package com.example.noted.feature_note.domain.repository

import com.example.noted.feature_note.data.model.RoomNote
import com.example.noted.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

// abstraction to be consumed by the business
interface NoteRepository {
    suspend fun addNotes(notes: List<Note>)
    suspend fun deleteNotes(notes: List<Note>)
    suspend fun getNoteById(noteId: Int): Note
    fun getAllNotes(): Flow<List<Note>>
}