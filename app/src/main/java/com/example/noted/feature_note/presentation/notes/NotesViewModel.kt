package com.example.noted.feature_note.presentation.notes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.core.ListOfDomainNotesToListOfViewNotes
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.use_case.NoteUseCases
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    val noteUseCases: NoteUseCases
) : ViewModel() {
    private val _state = MutableLiveData<NotesState>()
    val state: LiveData<NotesState> = _state

    private val _deleteState = MutableLiveData<Boolean>()
    val deleteState: LiveData<Boolean> = _deleteState

    init {
        _state.value = NotesState()
    }

    fun onEvent(noteEvent: NoteEvent) {
        when (noteEvent) {
            is NoteEvent.GetAllNotesEvent -> {
                getNotes(noteEvent.noteOrder)
            }

            is NoteEvent.OrderNoteEvent -> {
                Log.d("Here", "Note Order Type: " + noteEvent.noteOrder.orderType)
                noteUseCases.orderNotesUseCase.invoke(noteEvent.notes, noteEvent.noteOrder)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<List<Note>> {
                        override fun onSubscribe(d: Disposable) {}

                        override fun onSuccess(sortedNotes: List<Note>) {
                            val sortedViewNotes =
                                ListOfDomainNotesToListOfViewNotes.map(sortedNotes)
                            _state.value = state.value?.copy(notes = sortedViewNotes)
                            Log.d("Here", state.value.toString())
                        }

                        override fun onError(e: Throwable) {}

                    })
            }

            is NoteEvent.DeleteNotesEvent -> {
                noteUseCases.deleteNotesUseCase(noteEvent.notes.map { it.id!!.toLong()})
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                    object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {}

                        override fun onComplete() {
                            _deleteState.value = true
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            _deleteState.value = false
                        }

                    })
                }

            is NoteEvent.RestoreNoteEvent -> {
                /*viewModelScope.launch {
                    noteUseCases.addNoteUseCase(recentlyDeletedNotes ?: return@launch)
                    recentlyDeletedNotes = null
                }*/
            }
            is NoteEvent.ToggleOrderSectionVisibilityEvent -> {
                _state.value =
                    state.value?.copy(isOrderSectionVisible = !state.value!!.isOrderSectionVisible) // toggling
            }

        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        noteUseCases.getAllNotesUseCase(noteOrder)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<Note>> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(notes: List<Note>) {
                    val viewNotes = ListOfDomainNotesToListOfViewNotes.map(notes)
                    _state.value = NotesState(notes = viewNotes)
                    Log.d("Here", "getNotes NotesViewModel onNext")
                }

                override fun onError(e: Throwable) {
                    Log.d("Here", "getNotes NotesViewModel onError")
                }

                override fun onComplete() {}
            })
    }
}