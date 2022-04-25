package com.example.noted.feature_note.presentation.notes

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.noted.R
import com.example.noted.core.Extensions.animateVisibility
import com.example.noted.core.ListOfViewNotesToListOfDomainNotes
import com.example.noted.databinding.FragmentNotesBinding
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import com.example.noted.feature_note.presentation.model.ViewNote
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotesFragment : Fragment(), NotesFragmentAdapterInterface, View.OnClickListener, NotesPresentationController {
    private val notesViewModel: NotesViewModel by viewModels()
    private lateinit var notesBinding: FragmentNotesBinding
    private lateinit var vibrationService: Vibrator
    private lateinit var adapter: NotesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        return notesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initAdapter()
        restoreState()
        // observeStateChanges()
        setRadioButtonsListener()
        setOnClickListeners()

    }
    private fun setOnClickListeners(){
        setOrderOptionsIconListener()
    }

    private fun initBinding() {
        notesBinding =
            FragmentNotesBinding.inflate(layoutInflater) // we got a binding instance holding the views of fragment_notes.xml
    }
    private fun initAdapter() {
        adapter = NotesAdapter(this)
        lifecycleScope.launch {
            notesViewModel.onEvent(NoteEvent.OrderNoteEvent(notesViewModel.state.value.noteOrder))
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
    override fun onNoteLongClicked(note: ViewNote) {
        // Make Vibration
        vibrationService = context?.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
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
    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.orderIcon -> onOrderIconClicked()
            R.id.noteOptions -> deleteNotes()
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

}