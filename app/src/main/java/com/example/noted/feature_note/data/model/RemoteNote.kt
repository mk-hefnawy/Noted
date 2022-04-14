package com.example.noted.feature_note.data.model

import com.example.noted.R
import com.example.noted.feature_note.domain.model.NoteCategory

data class RemoteNote(
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
