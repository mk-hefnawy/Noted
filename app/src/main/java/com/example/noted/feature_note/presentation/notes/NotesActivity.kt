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
class NotesActivity : AppCompatActivity() {
    private lateinit var notesBinding: ActivityNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        //addNotesFragment()
    }

    private fun init() {
        notesBinding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(notesBinding.root)
    }

    private fun addNotesFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.fragmentsContainer, NotesFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            Log.d("Here", "Greater than zero")
        }
        else{
            super.onBackPressed()
            Log.d("Here", "Else")
        }
    }
}