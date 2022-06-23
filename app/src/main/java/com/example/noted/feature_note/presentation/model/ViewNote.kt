package com.example.noted.feature_note.presentation.model

import com.example.noted.feature_note.domain.model.NoteCategory
import java.util.*

data class ViewNote(

    var id: Long? = null,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val date: Date,
    val color: Int,
    var isSelected: Boolean,
    val attachedImages: List<AttachedImage> = listOf()
)