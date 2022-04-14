package com.example.noted.feature_note.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.core.ListOfDomainNotesToListOfViewNotes
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.use_case.NoteUseCases
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    val noteUseCases: NoteUseCases
) : ViewModel() {

    //constructor() : this(noteUseCases)

    // observable to hold the state of the UI in the ViewModel
    private val _state = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state

    // caching the deleted note for the Undo command
    var recentlyDeletedNotes: List<Note>? = null

    // to track the coroutine created for GetAllNotesUseCase
    private var getAllNotesJob: Job? = null

    init {
        // for viewing the notes at the first time
        getNotes(NoteOrder.Date(OrderType.Ascending))
    }

    fun onEvent(noteEvent: NoteEvent) {
        when (noteEvent) {
            is NoteEvent.OrderNoteEvent -> {
                // check if the input order is the same as the current order
                if (_state.value.noteOrder::class == noteEvent.noteOrder::class
                    && _state.value.noteOrder.orderType == noteEvent.noteOrder.orderType
                ) return
                getNotes(noteEvent.noteOrder)

            }
            is NoteEvent.DeleteNotesEvent -> {
                viewModelScope.launch {
                    noteUseCases.deleteNotesUseCase(noteEvent.notes)
                    recentlyDeletedNotes = noteEvent.notes
                }
            }
            is NoteEvent.RestoreNoteEvent -> {
                viewModelScope.launch {
                    noteUseCases.addNoteUseCase(recentlyDeletedNotes ?: return@launch)
                    recentlyDeletedNotes = null
                }
            }
            is NoteEvent.ToggleOrderSectionVisibilityEvent -> {
                _state.value =
                    state.value.copy(isOrderSectionVisible = !state.value.isOrderSectionVisible) // toggling
            }

        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        // we need to cancel the coroutine whenever this function returns, if we don't, we will end up having
        // a coroutine for each call to GetAllNotesUseCase
        getAllNotesJob?.cancel()
        getAllNotesJob = noteUseCases.getAllNotesUseCase(noteOrder).onEach { notes ->
            _state.value = state.value.copy(
                notes = ListOfDomainNotesToListOfViewNotes.map(notes),
                noteOrder = noteOrder
            )
        }.launchIn(viewModelScope)
    }
}