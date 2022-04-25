package com.example.noted.feature_note.presentation.notes

import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.noted.R
import com.example.noted.core.ListOfViewNotesToListOfDomainNotes
import com.example.noted.databinding.ActivityNotesBinding
import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType
import com.example.noted.feature_note.presentation.add_edit_note.AddEditFragment
import com.example.noted.feature_note.presentation.model.ViewNote
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotesActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var notesBinding: ActivityNotesBinding
    private val addEditFragment = AddEditFragment()
    private val notesFragment = NotesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }
    private fun init(){
        notesBinding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(notesBinding.root)
        setOnClickListeners()
    }

   /* private fun observeStateChanges() {
        lifecycleScope.launch {
            notesViewModel.state.collect { notesState ->
                val notes = notesState.notes
                adapter.setNotes(notes) // kda 7atta lw note wa7da etdafet,
            }
        }

    }*/

    private fun setOnClickListeners(){
        notesBinding.fabAddSaveNote.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            notesBinding.fabAddSaveNote.id -> onAddSaveNoteClicked()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStack()
        }
        else{
            super.onBackPressed()
        }
    }
    private fun onAddSaveNoteClicked(){
        val currentFragment = supportFragmentManager.findFragmentById(R.id.addEditNotesFragmentContainer)
        // Adding Note
        if (currentFragment is NotesFragment){
            Log.d("Here", "Add Note")
            supportFragmentManager.beginTransaction()
                .replace(notesBinding.addEditNotesFragmentContainer.id, addEditFragment)
                .addToBackStack(null)
                .commit()

            // change the icon of the FAB
            notesBinding.fabAddSaveNote.setImageResource(R.drawable.baseline_save_white_48)
        }
        else{
            // Saving Note

        }

    }
}