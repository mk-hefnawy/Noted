package com.example.noted.feature_note.domain.model

import com.example.noted.R

data class Note(
    val title: String,
    val content: String,
    val category: NoteCategory,
    val timeStamp: Long,
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
