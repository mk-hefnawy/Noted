package com.example.noted.feature_note.presentation.notes

import android.os.*
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.noted.R
import com.example.noted.core.Extensions.animateVisibility
import com.example.noted.core.ListOfViewNotesToListOfDomainNotes
import com.example.noted.databinding.ActivityNotesBinding
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import com.example.noted.feature_note.presentation.model.ViewNote
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotesActivity : AppCompatActivity(), PresentationController, View.OnClickListener,
    NotesActivityAdapterInterface {
    private lateinit var notesBinding: ActivityNotesBinding
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var vibrationService: Vibrator
    private lateinit var adapter: NotesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        init()
    }

    private fun init() {
        initAdapter()
        initActivityBinding()
        restoreState()
        observeStateChanges()
        setRadioButtonsListener()
        setOrderOptionsIconListener()
    }

    private fun observeStateChanges() {
        lifecycleScope.launch {
            notesViewModel.state.collect { notesState ->
                val notes = notesState.notes
                adapter.setNotes(notes) // kda 7atta lw note wa7da etdafet,
            }
        }

    }

    private fun restoreState() {
        restoreOrderSectionVisibilityState()
        restoreOrderOptionsState()
    }

    private fun restoreOrderSectionVisibilityState() {
        if (notesViewModel.state.value.isOrderSectionVisible) {
            notesBinding.orderSectionGroup.visibility = View.VISIBLE
        } else {
            notesBinding.orderSectionGroup.visibility = View.GONE
        }
    }

    private fun restoreOrderOptionsState() {
        when (notesViewModel.state.value.noteOrder) {
            is NoteOrder.Title -> notesBinding.orderByRadioGroup.check(R.id.titleRadioButton)
            is NoteOrder.Date -> notesBinding.orderByRadioGroup.check(R.id.dateRadioButton)
            is NoteOrder.Category -> notesBinding.orderByRadioGroup.check(R.id.categoryRadioButton)
        }
    }

    private fun initActivityBinding() {
        notesBinding =
            ActivityNotesBinding.inflate(layoutInflater) // we got a binding instance holding the views of activity_notes.xml
        setContentView(notesBinding.root)
    }

    private fun initAdapter() {
        adapter = NotesAdapter(this)
        lifecycleScope.launch {
            notesViewModel.onEvent(NoteEvent.OrderNoteEvent(notesViewModel.state.value.noteOrder))
        }
    }

    private fun setOrderOptionsIconListener() {
        notesBinding.orderIcon.setOnClickListener(this)
    }

    private fun setRadioButtonsListener() {
        val stateOrder = notesViewModel.state.value.noteOrder
        val stateOrderType = notesViewModel.state.value.noteOrder.orderType

        notesBinding.orderByRadioGroup.setOnCheckedChangeListener { _, radioButtonId ->
            onRadioButtonChecked(radioButtonId, stateOrder, stateOrderType)
        }

        notesBinding.orderTypeRadioGroup.setOnCheckedChangeListener { _, radioButtonId ->
            onRadioButtonChecked(radioButtonId, stateOrder, stateOrderType)
        }
    }

    override fun onRadioButtonChecked(
        radioButtonId: Int,
        stateOrder: NoteOrder,
        stateOrderType: OrderType
    ) {
        when (radioButtonId) {
            R.id.titleRadioButton -> {
                notesViewModel.onEvent(
                    NoteEvent.OrderNoteEvent(
                        NoteOrder.Title(
                            stateOrderType
                        )
                    )
                )
            }
            R.id.dateRadioButton -> {
                notesViewModel.onEvent(
                    NoteEvent.OrderNoteEvent(
                        NoteOrder.Date(
                            stateOrderType
                        )
                    )
                )
            }
            R.id.categoryRadioButton -> {
                notesViewModel.onEvent(
                    NoteEvent.OrderNoteEvent(
                        NoteOrder.Category(
                            stateOrderType
                        )
                    )
                )
            }
            R.id.ascendingRadioButton -> {
                notesViewModel.onEvent(NoteEvent.OrderNoteEvent(stateOrder.copy(OrderType.Ascending)))

            }
            R.id.descendingRadioButton -> {
                notesViewModel.onEvent(NoteEvent.OrderNoteEvent(stateOrder.copy(OrderType.Descending)))
            }
        }
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.orderIcon -> onOrderIconClicked()
            R.id.noteOptions -> deleteNotes()
            R.id.fabAddNote -> addNote()
        }
    }

    private fun addNote(){

    }

    private fun deleteNotes() {
        // get selected notes
        val selectedNotes = ListOfViewNotesToListOfDomainNotes.map(adapter.getSelectedNotes())
        notesViewModel.onEvent(NoteEvent.DeleteNotesEvent(selectedNotes))
        val message = if (selectedNotes.size > 1) "Notes have been deleted" else "Note has been deleted"


        val snackBar = Snackbar.make(notesBinding.noteOptions, message, Snackbar.LENGTH_SHORT)
        snackBar.duration = 3000
        snackBar.setAction("Undo"){
            notesViewModel.onEvent(NoteEvent.RestoreNoteEvent)
        }
        snackBar.show()

    }

    private fun onOrderIconClicked() {
        if (notesViewModel.state.value.isOrderSectionVisible) {
            notesBinding.orderSectionGroup.visibility = View.GONE
        } else {
            notesBinding.orderSectionGroup.visibility = View.VISIBLE
        }

        notesViewModel.onEvent(NoteEvent.ToggleOrderSectionVisibilityEvent)
    }

    override fun onNoteLongClicked(note: ViewNote) {
        // Make Vibration
        vibrationService = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(500, 1)
            vibrationService.vibrate(vibrationEffect)
        } else {
            vibrationService.vibrate(500)
        }

        // Show a Top Sheet with the word Delete
        //Animation
        notesBinding.noteOptions.animateVisibility()
        //Visibility
        notesBinding.noteOptions.visibility = View.VISIBLE
    }
}