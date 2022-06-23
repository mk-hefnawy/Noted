package com.example.noted.feature_note.domain.model

import com.example.noted.feature_note.presentation.model.AttachedImage
import java.util.*

data class Note(
    val id : Long?,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val date: Date,
    val color: Int,
    val attachedImages: List<AttachedImage>
)
