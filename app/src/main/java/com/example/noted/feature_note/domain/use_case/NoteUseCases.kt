package com.example.noted.feature_note.domain.use_case

// a class for wrapping all the use cases related to the NotesViewModel so the dependency injection is easier
data class NoteUseCases(
    val getAllNotesUseCase: GetAllNotesUseCase,
    val deleteNotesUseCase: DeleteNotesUseCase,
    val addNoteUseCase: AddNotesUseCase,
    val editNoteUseCase: EditNoteUseCase,
    val orderNotesUseCase: OrderNotesUseCase
)