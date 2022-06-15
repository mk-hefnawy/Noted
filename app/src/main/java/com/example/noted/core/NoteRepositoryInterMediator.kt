package com.example.noted.core

import com.example.noted.feature_note.data.repository.NoteRepositoryImpl
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryInterMediator @Inject constructor(
    private val noteRepositoryImpl: NoteRepositoryImpl
) : NoteRepository {

    override fun addNotes(note: Note): Single<Long> {
        val roomNote = DomainNoteToRoomNote.map(note)
        return noteRepositoryImpl.addNotes(roomNote)
    }

    override fun editNote(note: Note): Single<Int> {
        val roomNote = DomainNoteToRoomNote.map(note)
        return noteRepositoryImpl.editNote(roomNote)
    }

    override fun deleteNotes(notesIds: List<Long>): Completable {
        return noteRepositoryImpl.deleteNotes(notesIds)
    }

    override suspend fun getNoteById(noteId: Int): Note {
        return noteRepositoryImpl.getNoteById(noteId).let { roomNote ->  RoomNoteToDomainNote.map(roomNote) }
    }

    override fun getAllNotes(): Observable<List<Note>> {
        return noteRepositoryImpl.getAllNotes().let {
                flowOfListOfRoomNotes -> ObservableOfListOfRoomNotesToObservableOfListOfDomainNotes.map(flowOfListOfRoomNotes) }
    }
}