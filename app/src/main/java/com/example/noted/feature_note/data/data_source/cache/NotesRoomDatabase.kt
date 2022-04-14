package com.example.noted.feature_note.data.data_source.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noted.feature_note.data.model.RoomNote

@Database(entities = [RoomNote::class], version = 1)
abstract class NotesRoomDatabase : RoomDatabase(){
    abstract val roomNoteDao: RoomNoteDao

    companion object{
        val DATA_BASE_NAME = "NotesRoomDatabase"
    }
}