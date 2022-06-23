package com.example.noted.feature_note.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noted.R
import com.example.noted.feature_note.domain.model.NoteCategory
import com.example.noted.feature_note.presentation.model.AttachedImage
import java.util.*

// Model class for the data side // you gotta create a mapper to the business model Note
@Entity
data class DataNote(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val date: Date,
    val color: Int,
    val attachedImages: List<AttachedImage>
    )
