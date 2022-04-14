package com.example.noted.core

import com.example.noted.feature_note.data.model.RoomNote
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.presentation.model.ViewNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface Mapper<I, O>{
    fun map(input: I): O
}

object RoomNoteToDomainNote: Mapper<RoomNote, Note> {
    override fun map(input: RoomNote): Note{
        return Note(input.title, input.content, input.category, input.timeStamp, input.color)
    }
}

object DomainNoteToRoomNote: Mapper<Note, RoomNote> {
    override fun map(input: Note): RoomNote{
        return RoomNote(input.title, input.content, input.category, input.timeStamp, input.color)
    }
}

object DomainNoteToViewNote:Mapper<Note, ViewNote>{
    override fun map(input: Note): ViewNote {
        return ViewNote(input.title, input.content, input.category, input.timeStamp, input.color, false)
    }
}

object ListOfDomainNotesToListOfViewNotes:Mapper<List<Note>, List<ViewNote>>{
    override fun map(input: List<Note>): List<ViewNote> {
        return input.map { note ->
            ViewNote(note.title, note.content, note.category, note.timeStamp, note.color, false)
        }

    }
}

object ListOfDomainNotesToListOfRoomNotes:Mapper<List<Note>, List<RoomNote>>{
    override fun map(input: List<Note>): List<RoomNote> {
        return input.map { note ->
            RoomNote(note.title, note.content, note.category, note.timeStamp, note.color)
        }

    }
}
object ListOfViewNotesToListOfDomainNotes:Mapper<List<ViewNote>, List<Note>>{
    override fun map(input: List<ViewNote>): List<Note> {
        return input.map { note ->
            Note(note.title, note.content, note.category, note.timeStamp, note.color)
        }

    }
}

object FlowOfListOfRoomNotesToFlowOfListOfDomainNotes: Mapper<Flow<List<RoomNote>>, Flow<List<Note>>>{
    override fun map(input: Flow<List<RoomNote>>): Flow<List<Note>> {
        return input.map { listOfRoomNotes ->
            listOfRoomNotes.map {
                Note(it.title, it.content, it.category, it.timeStamp, it.color)
            }

        }
    }
}
