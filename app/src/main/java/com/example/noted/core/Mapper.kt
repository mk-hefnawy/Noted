package com.example.noted.core

import com.example.noted.feature_note.data.model.RoomNote
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.presentation.model.ViewNote
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface Mapper<I, O>{
    fun map(input: I): O
}

object RoomNoteToDomainNote: Mapper<RoomNote, Note> {
    override fun map(input: RoomNote): Note{
        return Note(input.id!!, input.title, input.content, input.category, input.date, input.color)
    }
}

object DomainNoteToRoomNote: Mapper<Note, RoomNote> {
    override fun map(input: Note): RoomNote{
        return RoomNote(input.id, input.title, input.content, input.category, input.date, input.color)
    }
}

object DomainNoteToViewNote:Mapper<Note, ViewNote>{
    override fun map(input: Note): ViewNote {
        return ViewNote(null, input.title, input.content, input.category, input.date, input.color, false)
    }
}

object ViewNoteToDomainNote: Mapper<ViewNote, Note>{
    override fun map(input: ViewNote): Note {
        return Note(input.id, input.title, input.content, input.category, input.date, input.color)
    }
}

object ListOfDomainNotesToListOfViewNotes:Mapper<List<Note>, List<ViewNote>>{
    override fun map(input: List<Note>): List<ViewNote> {
        return input.map { note ->
            ViewNote(note.id, note.title, note.content, note.category, note.date, note.color, false)
        }

    }
}

object ListOfDomainNotesToListOfRoomNotes:Mapper<List<Note>, List<RoomNote>>{
    override fun map(input: List<Note>): List<RoomNote> {
        return input.map { note ->
            RoomNote(note.id!!, note.title, note.content, note.category, note.date, note.color)
        }

    }
}
object ListOfViewNotesToListOfDomainNotes:Mapper<List<ViewNote>, List<Note>>{
    override fun map(input: List<ViewNote>): List<Note> {
        return input.map { note ->
            Note(note.id, note.title, note.content, note.category, note.date, note.color)
        }

    }
}

object ObservableOfListOfRoomNotesToObservableOfListOfDomainNotes: Mapper<Observable<List<RoomNote>>, Observable<List<Note>>>{
    override fun map(input: Observable<List<RoomNote>>): Observable<List<Note>> {
        return input.map { listOfRoomNotes ->
            listOfRoomNotes.map {
                Note(it.id, it.title, it.content, it.category, it.date, it.color)
            }

        }
    }
}
