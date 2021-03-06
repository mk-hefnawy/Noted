package com.example.noted.feature_note.domain.use_case

import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(
    var repository: NoteRepository
) {

     operator fun invoke(notesIds: List<Long>): Completable{
        return repository.deleteNotes(notesIds)
    }
}