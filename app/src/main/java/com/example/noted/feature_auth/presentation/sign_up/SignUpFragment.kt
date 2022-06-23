package com.example.noted.feature_auth.presentation.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.noted.R
import com.example.noted.core.Result
import com.example.noted.databinding.FragmentSignUpBinding
import com.example.noted.feature_auth.domain.model.User
import com.example.noted.feature_auth.presentation.sign_in.SignInFragment
import com.example.noted.feature_note.presentation.notes.NotesActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(), View.OnClickListener {
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var signUpFragmentBinding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        signUpFragmentBinding = FragmentSignUpBinding.inflate(layoutInflater)
        return signUpFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIfAlreadySignedIn()
        registerOnClickListeners()
        observeSignUp()
    }

    private fun registerOnClickListeners() {
        signUpFragmentBinding.signUpButtonContainer.setOnClickListener(this)
        signUpFragmentBinding.goToSignInButton.setOnClickListener(this)
    }

    private fun observeSignUp() {
        signUpViewModel.signUpLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is Result.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Sign Up is Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Failure -> {
                        Toast.makeText(requireContext(), "Sign Up Failed", Toast.LENGTH_SHORT)
                            .show()
                        it.throwable.printStackTrace()
                    }
                }
            }

        }
    }

    private fun checkIfAlreadySignedIn() {
        if (signUpViewModel.checkIfAlreadySignedIn()) {
            // go to home
            goToHome()
            Toast.makeText(requireContext(), "Already Signed In", Toast.LENGTH_SHORT).show()
            Log.d("Here", "Already Signed In")
        } else {
            Toast.makeText(requireContext(), "Not Signed In", Toast.LENGTH_SHORT).show()
            Log.d("Here", "Not Signed In")
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            signUpFragmentBinding.signUpButtonContainer -> onSignUpClicked()
            signUpFragmentBinding.goToSignInButton -> onGoToSignInClicked()
        }
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), NotesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun onSignUpClicked() {
        val user = User(
            null, signUpFragmentBinding.userNameET.text.toString(),
            signUpFragmentBinding.emailET.text.toString(),
            signUpFragmentBinding.passwordET.text.toString(),
        )
        signUpViewModel.signUp(user)
    }

    private fun onGoToSignInClicked() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.authFragmentsContainer, SignInFragment())
            .addToBackStack(null)
            .commit()
    }
}