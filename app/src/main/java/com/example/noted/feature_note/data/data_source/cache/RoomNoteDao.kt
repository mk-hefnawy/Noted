package com.example.noted.feature_note.data.data_source.cache

import androidx.room.*
import com.example.noted.feature_note.data.model.RoomNote
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotes(notes: List<RoomNote>)

    @Query("SELECT * FROM RoomNote")
    fun getAllNotes(): Flow<List<RoomNote>>

    @Query("SELECT * FROM RoomNote WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): RoomNote

    @Delete
    suspend fun deleteNotes(notes: List<RoomNote>)
}