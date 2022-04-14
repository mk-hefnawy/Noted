package com.example.noted.core

import com.example.noted.feature_note.data.repository.NoteRepositoryImpl
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryInterMediator @Inject constructor(
    private val noteRepositoryImpl: NoteRepositoryImpl
) : NoteRepository {

    override suspend fun addNotes(notes: List<Note>) {
        val roomNotes = ListOfDomainNotesToListOfRoomNotes.map(notes)
        noteRepositoryImpl.addNotes(roomNotes)
    }

    override suspend fun deleteNotes(notes: List<Note>) {
        val roomNotes = ListOfDomainNotesToListOfRoomNotes.map(notes)
        noteRepositoryImpl.deleteNotes(roomNotes)
    }

    override suspend fun getNoteById(noteId: Int): Note {
        return noteRepositoryImpl.getNoteById(noteId).let { roomNote ->  RoomNoteToDomainNote.map(roomNote) }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return noteRepositoryImpl.getAllNotes().let {
                flowOfListOfRoomNotes -> FlowOfListOfRoomNotesToFlowOfListOfDomainNotes.map(flowOfListOfRoomNotes) }
    }
}