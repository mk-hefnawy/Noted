package com.example.noted.feature_note.domain.use_case

import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(
    var repository: NoteRepository
) {

    suspend operator fun invoke(notes: List<Note>){
        repository.deleteNotes(notes)
    }
}