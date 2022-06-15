package com.example.noted.feature_note.domain.use_case

import com.example.noted.feature_note.domain.InvalidNoteException
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddNotesUseCase @Inject constructor(
    var repository: NoteRepository
) {
    @Throws(InvalidNoteException::class)
    /*
    * From Uncle Bob talk, the use case should take a RequestModel and return a ResultModel so it's easy to "test".
    *
    * so I think the invoke method should not be suspend, it actually should get the Flow from the repo method, and
    * exposes the new data in an exposed observable for the viewmodel to collect.
    * */
    operator fun invoke(note: Note): Single<Long> {
        // validation goes here in use case because validation is part of the business logic

        if (note.title.isBlank()) throw InvalidNoteException("Title cannot be empty")
        if (note.content.isBlank()) throw InvalidNoteException("Note body cannot be empty")


        return repository.addNotes(note)
    }
}