package com.example.noted.feature_note.presentation.add_edit_note

import android.graphics.Bitmap
import com.example.noted.feature_note.presentation.model.ViewNote

sealed class AddEditEvent {
    object ToggleColorPaletteVisibility: AddEditEvent()
    data class ChangeNoteColor(val noteColor: Int): AddEditEvent()
    data class AddNoteEvent(val note: ViewNote): AddEditEvent()
    data class EditNoteEvent(val note: ViewNote): AddEditEvent()
    data class StateChangeEvent(val state: AddEditState): AddEditEvent()
    data class CacheImageEvent(val attachedImageUri: String): AddEditEvent()
}