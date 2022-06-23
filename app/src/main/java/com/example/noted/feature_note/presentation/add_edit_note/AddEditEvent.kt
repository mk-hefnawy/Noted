package com.example.noted.feature_note.presentation.add_edit_note

import android.content.Context
import android.graphics.Bitmap
import com.example.noted.feature_note.presentation.model.AttachedImage
import com.example.noted.feature_note.presentation.model.ViewNote

sealed class AddEditEvent {
    object ToggleColorPaletteVisibility: AddEditEvent()
    data class AddNoteEvent(val note: ViewNote): AddEditEvent()
    data class EditNoteEvent(val note: ViewNote): AddEditEvent()
    data class StateChangeEvent(val state: AddEditState): AddEditEvent()
    data class SaveAttachedImage(val bitmap: Bitmap, val applicationContext: Context): AddEditEvent()
    data class DeleteAttachedImage(val attachedImage: AttachedImage): AddEditEvent()
}