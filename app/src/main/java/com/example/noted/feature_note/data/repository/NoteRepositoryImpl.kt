package com.example.noted.feature_note.data.repository

import com.example.noted.feature_note.data.data_source.cache.RoomNoteDao
import com.example.noted.feature_note.data.data_source.remote.RemoteNoteDao
import com.example.noted.feature_note.data.model.RoomNote
import com.example.noted.feature_note.data.utils.DataSourceType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// A class to decide where the data should come from
// It abstracts the details of data access from the business
@Suppress("JoinDeclarationAndAssignment")
class NoteRepositoryImpl: DataNoteRepository {
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

    override suspend fun addNotes(notes: List<RoomNote>) {
        when(dataSourceType){
            DataSourceType.CACHE -> roomNoteDao.addNotes(notes)
            DataSourceType.REMOTE -> remoteNoteDao.addNote(notes[0])
        }
    }

    override suspend fun deleteNotes(notes: List<RoomNote>) {
        when(dataSourceType){
            DataSourceType.CACHE ->  {
                roomNoteDao.deleteNotes(notes)
            }
            DataSourceType.REMOTE -> {
                remoteNoteDao.deleteNote(notes[0])
            }
        }

    }

    override suspend fun getNoteById(noteId: Int): RoomNote {
        return when(dataSourceType){
            DataSourceType.CACHE -> roomNoteDao.getNoteById(noteId)
            DataSourceType.REMOTE -> remoteNoteDao.getNoteById(noteId)
        }
    }
    override fun getAllNotes(): Flow<List<RoomNote>> {
        return when(dataSourceType){
            DataSourceType.CACHE ->  roomNoteDao.getAllNotes()
            DataSourceType.REMOTE -> remoteNoteDao.getAllNotes()
        }
    }

}