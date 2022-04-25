package com.example.noted.feature_note.presentation.add_edit_note

import androidx.lifecycle.ViewModel
import com.example.noted.R
import com.example.noted.feature_note.domain.use_case.NoteUseCases
import com.example.noted.feature_note.presentation.notes.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    val noteUseCases: NoteUseCases
) : ViewModel() {

    // State -> new note's title, new note's content, new note's color
    // Note. EditText state is frequently changing, every char added or removed is a state change
    // if you are using Compose and you combined the states together in one wrapper class, this will lead to having the whole
    // composes (related to the combined state) recomposes for a single char added or removed in one Edit Text.

    private val _state = MutableStateFlow(AddEditState(false, R.color.white))
    val state: StateFlow<AddEditState> = _state

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.ToggleColorPaletteVisibility -> {
                _state.value =
                    _state.value.copy(isColorPaletteVisible = !_state.value.isColorPaletteVisible)
            }

            is AddEditEvent.ChangeNoteColor -> {
                _state.value =
                    _state.value.copy(noteColor = event.noteColor)
            }
        }
    }
}