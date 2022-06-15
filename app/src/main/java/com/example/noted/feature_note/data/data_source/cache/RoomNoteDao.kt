package com.example.noted.feature_note.data.data_source.cache

import androidx.room.*
import com.example.noted.feature_note.data.model.RoomNote
import com.example.noted.feature_note.domain.model.NoteCategory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface RoomNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNotes(note: RoomNote): Single<Long>

    @Query("UPDATE RoomNote SET title = :title, content = :content, category = :category, date = :date, " +
            "color = :color WHERE id = :id")
    fun editNote(id:Long, title:String, content:String, category:NoteCategory, date: Date, color:Int): Single<Int>

    @Query("SELECT * FROM RoomNote")
    fun getAllNotes(): Observable<List<RoomNote>>

    @Query("SELECT * FROM RoomNote WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): RoomNote

    @Query("DELETE from RoomNote WHERE id IN (:notesIds)")
    fun deleteNotes(notesIds: List<Long>): Completable
}