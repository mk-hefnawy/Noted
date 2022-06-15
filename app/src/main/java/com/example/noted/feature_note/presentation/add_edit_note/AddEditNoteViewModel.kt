package com.example.noted.feature_note.presentation.add_edit_note

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.R
import com.example.noted.core.ViewNoteToDomainNote
import com.example.noted.feature_note.domain.InvalidNoteException
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.use_case.NoteUseCases
import com.example.noted.feature_note.presentation.model.ViewNote
import com.example.noted.feature_note.presentation.notes.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    val noteUseCases: NoteUseCases
) : ViewModel() {

    // State -> new note's title, new note's content, new note's color
    // Note. EditText state is frequently changing, every char added or removed is a state change
    // if you are using Compose and you combined the states together in one wrapper class, this will lead to having the whole
    // composes (related to the combined state) recomposes for a single char added or removed in one Edit Text.

    /*private val _state = MutableStateFlow(AddEditState("", "", false, R.color.white, mutableListOf()))
    val state: StateFlow<AddEditState> = _state*/

    private val _state = MutableLiveData<AddEditState>()
    val state: LiveData<AddEditState> = _state

    val addNoteSubject = PublishSubject.create<ViewNote>()
    val editNoteSubject = PublishSubject.create<ViewNote>()

    fun onEvent(event: AddEditEvent) {
        when (event) {

          /*  is AddEditEvent.CacheImageEvent -> {
                    val newList =  _state.value?.attachedImagesUris
                newList?.add(event.attachedImageUri)
                    _state.value = _state.value?.copy(attachedImagesUris = newList)
                }*/

            is AddEditEvent.StateChangeEvent -> {
                Log.d("Here", "Attached Images Size: ${event.state.attachedImagesUris.size}")
                _state.value = event.state
            }

            is AddEditEvent.ToggleColorPaletteVisibility -> {
                val isVisible = _state.value?.isColorPaletteVisible?: false
                _state.value =
                    _state.value?.copy(isColorPaletteVisible = !isVisible)
            }

            is AddEditEvent.ChangeNoteColor -> {
                _state.value =
                    _state.value?.copy(noteColor = event.noteColor, isColorPaletteVisible = false)
            }

            is AddEditEvent.EditNoteEvent -> {
                val note: Note = ViewNoteToDomainNote.map(event.note)

                try {
                    noteUseCases.editNoteUseCase(note)
                        .observeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.io()) // the network call work
                        .subscribe(object : SingleObserver<Int> {
                            override fun onSubscribe(d: Disposable) {}

                            override fun onSuccess(t: Int) {
                                editNoteSubject.onNext(event.note)
                            }

                            override fun onError(e: Throwable) {
                                editNoteSubject.onError(e)
                            }
                        })
                }catch (e: InvalidNoteException){
                    Log.d("Here", "Note Exception")
                }
            }

            is AddEditEvent.AddNoteEvent -> {
                val note: Note = ViewNoteToDomainNote.map(event.note)

                    try {
                        noteUseCases.addNoteUseCase(note)
                            .observeOn(Schedulers.newThread())
                            .subscribeOn(Schedulers.io()) // the network call work
                            .subscribe(object : SingleObserver<Long> {
                            override fun onSubscribe(d: Disposable) {}

                            override fun onSuccess(t: Long) {
                                event.note.id = t
                                addNoteSubject.onNext(event.note)
                            }

                            override fun onError(e: Throwable) {
                                addNoteSubject.onError(e)
                            }
                        })
                    }catch (e: InvalidNoteException){
                        Log.d("Here", "Note Exception")
                    }
            }
        }
    }
}