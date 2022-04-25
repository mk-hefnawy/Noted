package com.example.noted.feature_note.presentation.add_edit_note

sealed class AddEditEvent {
    object ToggleColorPaletteVisibility: AddEditEvent()
    data class ChangeNoteColor(val noteColor: Int): AddEditEvent()
}