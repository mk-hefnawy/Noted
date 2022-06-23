package com.example.noted.feature_auth.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.noted.R
import com.example.noted.core.Result
import com.example.noted.core.internet.InternetState
import com.example.noted.core.internet.InternetViewModel
import com.example.noted.core.utils.OneTimeEvent
import com.example.noted.feature_auth.domain.use_cases.HasInternetConnectionUseCase
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    lateinit var internetViewModel: InternetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        //internetViewModel = ViewModelProvider(this).get(InternetViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        //observeInternetState()
    }

    override fun onStop() {
        super.onStop()
        //removeInternetStateObserver()
        //internetViewModel.unSubscribeFromInternetStateSubject()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack()
        else super.onBackPressed()
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
                        }else if (result.value == InternetState.UnAvailable){
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