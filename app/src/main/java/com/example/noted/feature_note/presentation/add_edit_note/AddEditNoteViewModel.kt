package com.example.noted.feature_note.presentation.add_edit_note

import android.util.Log
import android.view.View
import com.example.noted.core.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.noted.core.ViewNoteToDomainNote
import com.example.noted.core.utils.OneTimeEvent
import com.example.noted.feature_note.domain.InvalidNoteException
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.use_case.NoteUseCases
import com.example.noted.feature_note.domain.use_case.attached_image.SaveAttachedImagesUseCase
import com.example.noted.feature_note.presentation.model.AttachedImage
import com.example.noted.feature_note.presentation.model.ViewNote
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
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

    private val _saveAttachedImageLiveData = MutableLiveData<OneTimeEvent<Result<String>>>()
    val saveAttachedImageLiveData: LiveData<OneTimeEvent<Result<String>>> = _saveAttachedImageLiveData

    private val _deleteAttachedImageLiveData = MutableLiveData<OneTimeEvent<AttachedImage>>()
    val deleteAttachedImageLiveData: LiveData<OneTimeEvent<AttachedImage>> = _deleteAttachedImageLiveData

    private val _editNoteLiveData = MutableLiveData<OneTimeEvent<Result<ViewNote>>>()
    val editNoteLiveData: LiveData<OneTimeEvent<Result<ViewNote>>> = _editNoteLiveData

    private val _addNoteLiveData = MutableLiveData<OneTimeEvent<Result<ViewNote>>>()
    val addNoteLiveData: LiveData<OneTimeEvent<Result<ViewNote>>> = _addNoteLiveData

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.StateChangeEvent -> {
                _state.value = event.state
                Log.d("Here", "State Change: Attached Images: ${_state.value?.attachedImages?.size}")
            }

            is AddEditEvent.ToggleColorPaletteVisibility -> {
                val isVisible = _state.value?.isColorPaletteVisible?: false
                _state.value =
                    _state.value?.copy(isColorPaletteVisible = !isVisible)
            }
            is AddEditEvent.SaveAttachedImage -> {
                SaveAttachedImagesUseCase(event.bitmap, event.applicationContext)
                    .invoke().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _saveAttachedImageLiveData.value = OneTimeEvent(Result.Success(it))
                    }, {
                        _saveAttachedImageLiveData.value = OneTimeEvent(Result.Failure(it))
                    })
            }

            is AddEditEvent.DeleteAttachedImage -> {
                Log.d("Here", "View Model Delete Image Event")
                _deleteAttachedImageLiveData.value = OneTimeEvent(event.attachedImage)
            }

            is AddEditEvent.EditNoteEvent -> {
                val note: Note = ViewNoteToDomainNote.map(event.note)
                try {
                    noteUseCases.editNoteUseCase(note)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io()) // the network call work
                        .subscribe(object : SingleObserver<Int> {
                            override fun onSubscribe(d: Disposable) {}

                            override fun onSuccess(t: Int) {
                                _editNoteLiveData.value = OneTimeEvent(Result.Success(event.note))
                            }

                            override fun onError(e: Throwable) {
                                _editNoteLiveData.value = OneTimeEvent(Result.Failure(e))
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
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io()) // the network call work
                            .subscribe(object : SingleObserver<Long> {
                            override fun onSubscribe(d: Disposable) {}

                            override fun onSuccess(t: Long) {
                                event.note.id = t
                                _addNoteLiveData.value = OneTimeEvent(Result.Success(event.note))
                            }

                            override fun onError(e: Throwable) {
                                _addNoteLiveData.value = OneTimeEvent(Result.Failure(e))
                            }
                        })
                    }catch (e: InvalidNoteException){
                        Log.d("Here", "Note Exception")
                    }
            }
        }
    }
}