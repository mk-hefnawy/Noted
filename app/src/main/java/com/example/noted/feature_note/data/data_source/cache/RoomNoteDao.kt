package com.example.noted.feature_note.data.data_source.cache

import androidx.room.*
import com.example.noted.feature_note.data.model.DataNote
import com.example.noted.feature_note.domain.model.NoteCategory
import com.example.noted.feature_note.presentation.model.AttachedImage
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*

@Dao
interface RoomNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNotes(note: DataNote): Single<Long>

    @Query(
        "UPDATE DataNote SET title = :title, content = :content, category = :category, date = :date, " +
            "color = :color, attachedImages = :attachedImages WHERE id = :id"
    )
    fun editNote(id:Long, title:String, content:String, category:NoteCategory, date: Date, color:Int,
                 attachedImages: List<AttachedImage>): Single<Int>

    @Query("SELECT * FROM DataNote")
    fun getAllNotes(): Observable<List<DataNote>>

    @Query("SELECT * FROM DataNote WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): DataNote

    @Query("DELETE from DataNote WHERE id IN (:notesIds)")
    fun deleteNotes(notesIds: List<Long>): Completable
}