package com.example.noted.feature_note.domain.use_case

import com.example.noted.feature_note.domain.InvalidNoteException
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class EditNoteUseCase  @Inject constructor(
    var repository: NoteRepository
){
    @Throws(InvalidNoteException::class)
    operator fun invoke(note: Note): Single<Int> {
        // validation goes here in use case because validation is part of the business logic

        if (note.title.isBlank()) throw InvalidNoteException("Title cannot be empty")
        if (note.content.isBlank()) throw InvalidNoteException("Note body cannot be empty")

        return repository.editNote(note)
    }
}