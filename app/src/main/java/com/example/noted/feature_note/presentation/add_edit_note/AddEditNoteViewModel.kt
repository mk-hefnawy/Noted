package com.example.noted.feature_note.presentation.add_edit_note

import androidx.lifecycle.ViewModel
import com.example.noted.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
): ViewModel() {

    // State -> new note's title, new note's content, new note's color
    // Note. EditText state is frequently changing, every char added or removed is a state change
    // if you are using Compose and you combined the states together in one wrapper class, this will lead to having the whole
    // composes (related to the combined state) recomposes for a single char added or removed in one Edit Text.


}