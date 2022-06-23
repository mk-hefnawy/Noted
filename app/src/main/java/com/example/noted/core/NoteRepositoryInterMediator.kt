package com.example.noted.core

import android.util.Log
import com.example.noted.core.internet.InternetState
import com.example.noted.feature_note.data.repository.NotesService
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteRepositoryInterMediator @Inject constructor(
    private val notesService: NotesService
) : NoteRepository {

    override fun addNotes(note: Note): Single<Long> {
        val roomNote = DomainNoteToRoomNote.map(note)
        return notesService.addNotes(roomNote)
    }

    override fun editNote(note: Note): Single<Int> {
        val roomNote = DomainNoteToRoomNote.map(note)
        return notesService.editNote(roomNote)
    }

    override fun deleteNotes(notesIds: List<Long>): Completable {
        return notesService.deleteNotes(notesIds)
    }

    override suspend fun getNoteById(noteId: Int): Note {
        return notesService.getNoteById(noteId).let { roomNote ->  RoomNoteToDomainNote.map(roomNote) }
    }

    override fun getAllNotes(hasInternet: InternetState): Observable<List<Note>> {
        if (hasInternet == InternetState.Available){
            Log.d("Here", "Repository Here, We got Connection")
        }else{
            Log.d("Here", "Repository Here, We don't have Connection, Sir")
        }
        return notesService.getAllNotes().let {
                flowOfListOfRoomNotes -> ObservableOfListOfRoomNotesToObservableOfListOfDomainNotes.map(flowOfListOfRoomNotes) }
    }
}