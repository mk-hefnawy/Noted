package com.example.noted.feature_note.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noted.R
import com.example.noted.feature_note.domain.model.NoteCategory
// Model class for the data side // you gotta create a mapper to the business model Note
@Entity
data class RoomNote(
    val title: String,
    val content: String,
    val category: NoteCategory,
    val timeStamp: Long,
    val color: Int
    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

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
