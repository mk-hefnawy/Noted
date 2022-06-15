package com.example.noted.feature_note.domain.model

import com.example.noted.R
import java.util.*

data class Note(
    val id : Long?,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val date: Date,
    val color: Int
){
    companion object {
        val noteColors = listOf(
            R.color.yellow,
            R.color.green,
            R.color.blueGrey,
            R.color.deepOrange,
            R.color.orange
        )
    }
}
