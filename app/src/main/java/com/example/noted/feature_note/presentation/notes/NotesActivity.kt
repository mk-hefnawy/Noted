package com.example.noted.feature_note.presentation.notes

import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.noted.R
import com.example.noted.core.ListOfViewNotesToListOfDomainNotes
import com.example.noted.core.Result
import com.example.noted.core.internet.InternetState
import com.example.noted.core.internet.InternetViewModel
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
    private lateinit var internetViewModel: InternetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        internetViewModel = ViewModelProvider(this).get(InternetViewModel::class.java)
        init()

    }

    override fun onResume() {
        super.onResume()
        observeInternetState()
    }

    override fun onStop() {
        super.onStop()
        removeInternetStateObserver()
        internetViewModel.unSubscribeFromInternetStateSubject()
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

    private fun observeInternetState() {
        internetViewModel.internetState.observe(this) {
            it.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.value == InternetState.Available) {
                            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show()
                            Log.d("Here", "Connected")
                        } else if (result.value == InternetState.Lost) {
                            Toast.makeText(this, "Connection Lost", Toast.LENGTH_LONG).show()
                            Log.d("Here", "Connection Lost")
                        }else{
                            Toast.makeText(this, "Not Connected", Toast.LENGTH_LONG).show()
                            Log.d("Here", "Not Connected")
                        }
                    }

                    is Result.Failure -> {
                        Toast.makeText(this, "Error Getting Connection", Toast.LENGTH_LONG).show()
                        Log.d("Here", "Error Getting Connection")
                    }
                }
            }
        }
    }

    private fun removeInternetStateObserver(){
        internetViewModel.internetState.removeObservers(this)
    }
}