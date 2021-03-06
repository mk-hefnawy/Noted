package com.example.noted.feature_note.domain.use_case

import com.example.noted.core.internet.InternetState
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

// business logic for showing the notes with some sort of ordering
class GetAllNotesUseCase @Inject constructor(
    var repository: NoteRepository
) {
    // you may create an execute() function
    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Ascending),
        hasInternet: InternetState
    ): Observable<List<Note>>{
        return repository.getAllNotes(hasInternet).map { notes ->
            when(noteOrder.orderType){
                is OrderType.Ascending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> notes.sortedBy { it.title.lowercase() }
                        is NoteOrder.Date ->  notes.sortedBy { it.date }
                        is NoteOrder.Category -> notes.sortedBy { it.color }
                    }
                }
                is OrderType.Descending -> {
                    when(noteOrder){
                        is NoteOrder.Title -> notes.sortedByDescending { it.title.lowercase() }
                        is NoteOrder.Date ->  notes.sortedByDescending { it.date }
                        is NoteOrder.Category -> notes.sortedByDescending { it.color }
                    }
                }

            }
        }
    }
}