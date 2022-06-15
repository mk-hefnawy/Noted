package com.example.noted.feature_note.presentation.model

import android.graphics.Bitmap
import com.example.noted.R
import com.example.noted.feature_note.domain.model.NoteCategory
import java.time.LocalDateTime
import java.util.*

data class ViewNote(

    var id: Long? = null,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val date: Date,
    val color: Int,
    var isSelected: Boolean,
    val imagesUris: MutableList<String>? = null
)