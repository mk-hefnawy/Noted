package com.example.noted.feature_auth.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.noted.R
import com.example.noted.core.Result
import com.example.noted.databinding.FragmentSignInBinding
import com.example.noted.feature_note.presentation.notes.NotesActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(), View.OnClickListener {
    lateinit var signInBinding: FragmentSignInBinding
    lateinit var signInViewModel: SignInViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInBinding = FragmentSignInBinding.inflate(layoutInflater)
        return signInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerClickListeners()
        observeSignInLivedata()
    }

    private fun registerClickListeners(){
        signInBinding.signInButtonContainer.setOnClickListener(this)
    }

    private fun observeSignInLivedata() {
        signInViewModel.signInLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        goToHome()
                    }

                    is Result.Failure -> {
                        Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            signInBinding.signInButtonContainer -> onSignInClicked()
        }
    }

    private fun onSignInClicked() {
        signInViewModel.signIn(
            signInBinding.emailET.text.toString(),
            signInBinding.passwordET.text.toString()
        )
    }

    private fun goToHome(){
        val intent = Intent(requireContext(), NotesActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}