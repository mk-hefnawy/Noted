package com.example.noted.feature_note.data.repository

import com.example.noted.feature_note.data.data_source.cache.RoomNoteDao
import com.example.noted.feature_note.data.data_source.remote.RemoteNoteDao
import com.example.noted.feature_note.data.model.DataNote
import com.example.noted.feature_note.data.utils.DataSourceType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

// A class to decide where the data should come from
// It abstracts the details of data access from the business
@Suppress("JoinDeclarationAndAssignment")
class NotesService: DataNoteRepository {
    private lateinit var roomNoteDao: RoomNoteDao
    private lateinit var remoteNoteDao: RemoteNoteDao
    private lateinit var dataSourceType: DataSourceType

    @Inject constructor(roomNoteDao: RoomNoteDao){
        this.roomNoteDao = roomNoteDao
        dataSourceType = DataSourceType.CACHE
    }

    constructor(remoteNoteDao: RemoteNoteDao){
        this.remoteNoteDao = remoteNoteDao
        dataSourceType = DataSourceType.REMOTE
    }

    override fun addNotes(note: DataNote): Single<Long> {
        when(dataSourceType){
            DataSourceType.CACHE -> return roomNoteDao.addNotes(note)
            DataSourceType.REMOTE -> { TODO()} //remoteNoteDao.addNote(notes[0])
        }
    }

    override fun editNote(note: DataNote): Single<Int> {
        when(dataSourceType){
            DataSourceType.CACHE -> return roomNoteDao.editNote(note.id!!, note.title, note.content,
                note.category, note.date, note.color, note.attachedImages)
            DataSourceType.REMOTE -> { TODO()} //remoteNoteDao.addNote(notes[0])
        }
    }

    override fun deleteNotes(notesIds: List<Long>): Completable {
        when(dataSourceType){
            DataSourceType.CACHE ->  {
                return roomNoteDao.deleteNotes(notesIds)
            }
            DataSourceType.REMOTE -> {
                TODO()
               // remoteNoteDao.deleteNote(notes[0])
            }

        }
    }
    override suspend fun getNoteById(noteId: Int): DataNote {
        return when(dataSourceType){
            DataSourceType.CACHE -> roomNoteDao.getNoteById(noteId)
            DataSourceType.REMOTE -> remoteNoteDao.getNoteById(noteId)
        }
    }

    override fun getAllNotes(): Observable<List<DataNote>> {
        when(dataSourceType){
            DataSourceType.CACHE ->  return roomNoteDao.getAllNotes()
            DataSourceType.REMOTE -> { TODO()}//remoteNoteDao.getAllNotes()
        }
    }

}