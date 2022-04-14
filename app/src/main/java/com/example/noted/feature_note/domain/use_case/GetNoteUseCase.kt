package com.example.noted.feature_note.domain.use_case

import com.example.noted.feature_note.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    val repository: NoteRepository
) {

    // Observable

   suspend operator fun invoke(id: Int){
        repository.getNoteById(id)
    }
}