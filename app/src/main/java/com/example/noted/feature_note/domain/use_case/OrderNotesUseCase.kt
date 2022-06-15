package com.example.noted.feature_note.domain.use_case

import android.util.Log
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import io.reactivex.rxjava3.core.Single
import java.util.*

class OrderNotesUseCase {

    operator fun invoke(notes: List<Note>, noteOrder: NoteOrder): Single<List<Note>> {
        var sortedNotes = mutableListOf<Note>()
        when (noteOrder.orderType) {
            is OrderType.Ascending -> {
                when (noteOrder) {
                    is NoteOrder.Title -> {
                        sortedNotes = notes as MutableList<Note>
                        sortedNotes.sortWith { item1, item2 ->
                            item1.title.lowercase().compareTo(item2.title.lowercase())
                        }
                    }
                    is NoteOrder.Date -> sortedNotes =
                        notes.sortedBy { it.date } as MutableList<Note>
                    is NoteOrder.Category -> sortedNotes =
                        notes.sortedBy { it.color } as MutableList<Note>
                }
            }
            is OrderType.Descending -> {
                Log.d("Here", "Desc")
                when (noteOrder) {
                    is NoteOrder.Title -> {
                        sortedNotes = notes as MutableList<Note>
                        Log.d("Here", "Desc ${sortedNotes.get(0).title}" )
                        sortedNotes.sortWith { item1, item2 ->
                            item2.title.lowercase().compareTo(item1.title.lowercase())
                        }
                        Log.d("Here", "Desc After Sorting ${sortedNotes.get(0).title}" )
                    }

                    is NoteOrder.Date -> sortedNotes =
                        notes.sortedByDescending { it.date } as MutableList<Note>
                    is NoteOrder.Category -> sortedNotes =
                        notes.sortedByDescending { it.color } as MutableList<Note>
                }
            }
        }

        return Single.just(sortedNotes)
    }
}
