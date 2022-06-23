package com.example.noted.core

import com.example.noted.feature_note.data.model.DataNote
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.presentation.model.ViewNote
import io.reactivex.rxjava3.core.Observable

interface Mapper<I, O>{
    fun map(input: I): O
}

object RoomNoteToDomainNote: Mapper<DataNote, Note> {
    override fun map(input: DataNote): Note{
        return Note(input.id!!, input.title, input.content, input.category, input.date, input.color, input.attachedImages)
    }
}

object DomainNoteToRoomNote: Mapper<Note, DataNote> {
    override fun map(input: Note): DataNote{
        return DataNote(input.id, input.title, input.content, input.category, input.date,
            input.color, input.attachedImages)
    }
}

object DomainNoteToViewNote:Mapper<Note, ViewNote>{
    override fun map(input: Note): ViewNote {
        return ViewNote(null, input.title, input.content, input.category, input.date, input.color,
            false, input.attachedImages)
    }
}

object ViewNoteToDomainNote: Mapper<ViewNote, Note>{
    override fun map(input: ViewNote): Note {
        return Note(input.id, input.title, input.content, input.category, input.date, input.color,
            input.attachedImages)
    }
}

object ListOfDomainNotesToListOfViewNotes:Mapper<List<Note>, List<ViewNote>>{
    override fun map(input: List<Note>): List<ViewNote> {
        return input.map { note ->
            ViewNote(note.id, note.title, note.content, note.category, note.date, note.color,
                false, note.attachedImages)
        }
    }
}

object ListOfDomainNotesToListOfRoomNotes:Mapper<List<Note>, List<DataNote>>{
    override fun map(input: List<Note>): List<DataNote> {
        return input.map { note ->
            DataNote(note.id!!, note.title, note.content, note.category, note.date, note.color, note.attachedImages)
        }

    }
}
object ListOfViewNotesToListOfDomainNotes:Mapper<List<ViewNote>, List<Note>>{
    override fun map(input: List<ViewNote>): List<Note> {
        return input.map { note ->
            Note(note.id, note.title, note.content, note.category, note.date, note.color, note.attachedImages)
        }

    }
}

object ObservableOfListOfRoomNotesToObservableOfListOfDomainNotes: Mapper<Observable<List<DataNote>>, Observable<List<Note>>>{
    override fun map(input: Observable<List<DataNote>>): Observable<List<Note>> {
        return input.map { listOfRoomNotes ->
            listOfRoomNotes.map {
                Note(it.id, it.title, it.content, it.category, it.date, it.color, it.attachedImages)
            }

        }
    }
}
