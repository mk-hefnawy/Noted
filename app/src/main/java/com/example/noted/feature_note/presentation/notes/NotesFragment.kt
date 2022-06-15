package com.example.noted.feature_note.presentation.notes

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.noted.R
import com.example.noted.core.Extensions.animateVisibility
import com.example.noted.core.ListOfDomainNotesToListOfViewNotes
import com.example.noted.core.ListOfViewNotesToListOfDomainNotes
import com.example.noted.databinding.FragmentNotesBinding
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import com.example.noted.feature_note.presentation.add_edit_note.AddEditFragment
import com.example.noted.feature_note.presentation.model.ViewNote
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesFragment : Fragment(), NotesFragmentAdapterInterface, View.OnClickListener,
    NotesPresentationController {
    // val notesViewModel: NotesViewModel by viewModels()
    lateinit var notesViewModel: NotesViewModel
    private lateinit var notesBinding: FragmentNotesBinding
    private lateinit var vibrationService: Vibrator
    private var adapter: NotesAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        initBinding()
        return notesBinding.root
    }

    override fun onResume() {
        super.onResume()
        initAdapter()
        notesViewModel.state.value?.let {
            adapter?.setNotes(it.notes) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        notesViewModel.state.value?.let {
            adapter?.setNotes(it.notes)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun init() {
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        getAllNotes()
        //restoreState()
        observeNotesState()
        observeDeleteState()
        setRadioButtonsListener()
        setOnClickListeners()

    }
    private fun initAdapter(){
        adapter = NotesAdapter(this)
        notesBinding.notesRecyclerView.adapter = adapter
        notesBinding.notesRecyclerView.layoutManager = GridLayoutManager(this.context, 2)

    }

    private fun observeDeleteState() {
        notesViewModel.deleteState.observe(viewLifecycleOwner) { res ->
            res?.let {
                if (it) Toast.makeText(
                    requireContext(),
                    "Deletion is Successful",
                    Toast.LENGTH_SHORT
                ).show()
                else Toast.makeText(requireContext(), "Deletion Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun observeNotesState() {
        notesViewModel.state.observe(viewLifecycleOwner) { notesState ->
            notesState?.let {
                if (adapter == null) {
                    initAdapter()
                }
                if (it.notes.isNotEmpty()) {
                    adapter?.setNotes(it.notes)
                }
            }
        }
    }

    private fun setOnClickListeners() {
        setOrderOptionsIconListener()
        notesBinding.fabAddNote.setOnClickListener(this)
        notesBinding.deleteNote.setOnClickListener(this)
        notesBinding.cancelDeleteNote.setOnClickListener(this)
    }

    private fun initBinding() {
        notesBinding =
            FragmentNotesBinding.inflate(layoutInflater) // we got a binding instance holding the views of fragment_notes.xml
    }

    private fun getAllNotes() {
        notesViewModel.state.value?.let {
            notesViewModel.onEvent(NoteEvent.GetAllNotesEvent(it.noteOrder))
            return
        }
        notesViewModel.onEvent(NoteEvent.GetAllNotesEvent(NoteOrder.Date(OrderType.Descending)))
    }

    private fun restoreState() {
        restoreOrderSectionVisibilityState()
        restoreOrderOptionsState()
    }

    private fun restoreOrderSectionVisibilityState() {
        if (notesViewModel.state.value!!.isOrderSectionVisible) {
            notesBinding.orderSectionGroup.visibility = View.VISIBLE
        } else {
            notesBinding.orderSectionGroup.visibility = View.GONE
        }
    }

    private fun restoreOrderOptionsState() {
        when (notesViewModel.state.value!!.noteOrder) {
            is NoteOrder.Title -> notesBinding.orderByRadioGroup.check(R.id.titleRadioButton)
            is NoteOrder.Date -> notesBinding.orderByRadioGroup.check(R.id.dateRadioButton)
            is NoteOrder.Category -> notesBinding.orderByRadioGroup.check(R.id.categoryRadioButton)
        }
    }

    override fun hideNoteOptions() {
        notesBinding.noteOptions.visibility = View.GONE
    }

    override fun onNoteClicked(note: ViewNote) {
        val bundle = Bundle().also { it.putString("note", Gson().toJson(note)) }
        val fragment = AddEditFragment()
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentsContainer, fragment)
            .addToBackStack(null)
            .commit()
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
        if (notesBinding.noteOptions.visibility != View.VISIBLE) {
            Log.d("Here", "Animate")
            notesBinding.noteOptions.animateVisibility()
            //Visibility
            notesBinding.noteOptions.visibility = View.VISIBLE
        }

    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.orderIcon -> onOrderIconClicked()
            R.id.deleteNote -> deleteNotes()
            R.id.cancelDeleteNote -> cancelDeleteNote()
            R.id.fabAddNote -> onAddNoteClicked()
        }
    }

    private fun cancelDeleteNote() {
        notesBinding.noteOptions.visibility = View.GONE
        // remove all selections
        adapter?.removeAllSelections()
    }

    private fun setOrderOptionsIconListener() {
        notesBinding.orderIcon.setOnClickListener(this)
    }

    private fun onAddNoteClicked() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentsContainer, AddEditFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun setRadioButtonsListener() {
        notesBinding.orderByRadioGroup.setOnCheckedChangeListener { _, radioButtonId ->
            onRadioButtonChecked(radioButtonId)
        }

        notesBinding.orderTypeRadioGroup.setOnCheckedChangeListener { _, radioButtonId ->
            onRadioButtonChecked(radioButtonId)
        }
    }

    override fun onRadioButtonChecked(
        radioButtonId: Int
    ) {
        val currentNotes = adapter!!.notes
        val presentationNotes = ListOfViewNotesToListOfDomainNotes.map(currentNotes)
        when (radioButtonId) {
            R.id.titleRadioButton -> {
                notesViewModel.onEvent(
                    NoteEvent.OrderNoteEvent(
                        presentationNotes,
                        NoteOrder.Title(
                            notesViewModel.state.value!!.noteOrder.orderType
                        )
                    )
                )
            }
            R.id.dateRadioButton -> {
                notesViewModel.onEvent(
                    NoteEvent.OrderNoteEvent(
                        presentationNotes,
                        NoteOrder.Date(
                            notesViewModel.state.value!!.noteOrder.orderType
                        )
                    )
                )
            }
            R.id.categoryRadioButton -> {
                notesViewModel.onEvent(
                    NoteEvent.OrderNoteEvent(
                        presentationNotes,
                        NoteOrder.Category(
                            notesViewModel.state.value!!.noteOrder.orderType
                        )
                    )
                )
            }
            R.id.ascendingRadioButton -> {
                notesViewModel.state.value!!.noteOrder.orderType = OrderType.Ascending
                notesViewModel.onEvent(
                    NoteEvent
                        .OrderNoteEvent(
                            presentationNotes,
                            notesViewModel.state.value!!.noteOrder
                        )
                )
            }
            R.id.descendingRadioButton -> {
                notesViewModel.state.value!!.noteOrder.orderType = OrderType.Descending
                notesViewModel.onEvent(
                    NoteEvent
                        .OrderNoteEvent(
                            presentationNotes,
                            notesViewModel.state.value!!.noteOrder
                        )
                )
            }
        }
    }


    private fun deleteNotes() {
        // hide notes options
        notesBinding.noteOptions.visibility = View.GONE


        // get selected notes
        val selectedViewNotes = adapter!!.getSelectedNotes()
        adapter?.removeAllSelections() // because th user took the delete action
        val selectedDomainNotes = ListOfViewNotesToListOfDomainNotes.map(selectedViewNotes)

        adapter?.deleteNotes(selectedViewNotes)

        val message =
            if (selectedViewNotes.size > 1) "Notes have been deleted" else "Note has been deleted"

        val snackBar = Snackbar.make(notesBinding.noteOptions, message, Snackbar.LENGTH_SHORT)
        snackBar.duration = 3000
        snackBar.setAction("Undo") {
            Toast.makeText(requireContext(), "Snack Bar Undo Clicked", Toast.LENGTH_SHORT).show()
            val notesAfterRestoring =
                ListOfViewNotesToListOfDomainNotes.map(adapter?.notes!!) as ArrayList<Note>
            notesAfterRestoring.addAll(selectedDomainNotes)
            notesViewModel.onEvent(
                NoteEvent.OrderNoteEvent(
                    notesAfterRestoring,
                    notesViewModel.state.value!!.noteOrder
                )
            )

        }
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event == DISMISS_EVENT_TIMEOUT) { // normal dismissal
                    notesViewModel.onEvent(NoteEvent.DeleteNotesEvent(selectedDomainNotes))
                }
            }
        })
        snackBar.show()
    }

    private fun onOrderIconClicked() {
        if (notesViewModel.state.value!!.isOrderSectionVisible) {
            notesBinding.orderSectionGroup.visibility = View.GONE
        } else {
            notesBinding.orderSectionGroup.visibility = View.VISIBLE
        }
        notesViewModel.onEvent(NoteEvent.ToggleOrderSectionVisibilityEvent)
    }

}