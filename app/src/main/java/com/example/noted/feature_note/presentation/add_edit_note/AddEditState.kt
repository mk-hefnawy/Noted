package com.example.noted.feature_note.presentation.add_edit_note

import android.graphics.Bitmap
import android.util.Log
import com.example.noted.R
import com.example.noted.feature_note.domain.model.NoteCategory
import com.example.noted.feature_note.presentation.model.AttachedImage

data class AddEditState(
    var noteId: Long? = null,
    var noteTitle: String,
    var noteBody: String,
    var noteCategory: NoteCategory,
    val isColorPaletteVisible: Boolean,
    val noteColor: Int,
    val attachedImages: MutableList<AttachedImage>,

){
    companion object{
        fun default(title: String, body: String): AddEditState{
            Log.d("Here", "default")
        return AddEditState(
            null,
            title,
            body,
            NoteCategory.default,
            false, R.color.white,
            attachedImages = mutableListOf()

        )}
    }
}
