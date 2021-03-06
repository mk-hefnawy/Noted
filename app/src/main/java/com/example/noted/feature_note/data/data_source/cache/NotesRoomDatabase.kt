package com.example.noted.feature_note.data.data_source.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.noted.feature_note.data.model.DataNote

@Database(entities = [DataNote::class], version = 8)
@TypeConverters(Converters::class)
abstract class NotesRoomDatabase : RoomDatabase(){
    abstract val roomNoteDao: RoomNoteDao

    companion object{
        val DATA_BASE_NAME = "NotesRoomDatabase"
    }
}