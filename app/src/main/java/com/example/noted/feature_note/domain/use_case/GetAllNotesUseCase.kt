package com.example.noted.feature_note.domain.use_case

import com.example.noted.feature_note.data.model.RoomNote
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// business logic for showing the notes with some sort of ordering
class GetAllNotesUseCase @Inject constructor(
    var repository: NoteRepository
) {
    // you may create an execute() function
    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Ascending)
    ): Flow<List<Note>>{
        return repository.getAllNotes().map { notes ->
            when(noteOrder.orderType){
                is OrderType.Ascending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> notes.sortedBy { it.title.lowercase() }
                        is NoteOrder.Date ->  notes.sortedBy { it.timeStamp }
                        is NoteOrder.Category -> notes.sortedBy { it.color }
                    }
                }
                is OrderType.Descending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> notes.sortedByDescending { it.title.lowercase() }
                        is NoteOrder.Date ->  notes.sortedByDescending { it.timeStamp }
                        is NoteOrder.Category -> notes.sortedByDescending { it.color }
                    }
                }

            }
        }
    }
}