package com.example.noted.feature_note.presentation.add_edit_note

data class AddEditState(
    val noteTitle: String,
    val noteBody: String,
    val isColorPaletteVisible: Boolean,
    val noteColor: Int,
    val attachedImagesUris: MutableList<String>
)
