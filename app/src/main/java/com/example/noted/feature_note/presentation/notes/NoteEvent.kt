package com.example.noted.feature_note.presentation.notes

import com.example.noted.core.internet.InternetState
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.utils.NoteOrder

// a class to hold the all possible events that the user may start
sealed class NoteEvent{
    data class OrderNoteEvent(val notes: List<Note>, val noteOrder: NoteOrder): NoteEvent()
    data class GetAllNotesEvent( val noteOrder: NoteOrder, val hasInternet: InternetState): NoteEvent()
    data class DeleteNotesEvent( val notes: List<Note>): NoteEvent()
    object RestoreNoteEvent: NoteEvent()
    object ToggleOrderSectionVisibilityEvent: NoteEvent()
}
