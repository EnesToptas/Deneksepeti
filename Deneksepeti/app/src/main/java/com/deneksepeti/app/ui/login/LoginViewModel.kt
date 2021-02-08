package com.deneksepeti.app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.deneksepeti.app.data.LoginRepository
import com.deneksepeti.app.data.Result

import com.deneksepeti.app.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = result.data.displayName)))
        } else {
            _loginResult.postValue(LoginResult(error = R.string.login_failed))
        }
    }


    fun register(displayName: String, username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.register(displayName, username, password)

        if (result is Result.Success) {
            _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = result.data.displayName)))
        } else if (result is Result.Error) {
            _loginResult.postValue(LoginResult(error = R.string.register_failed))
        }
    }


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun registerDataChanged(displayName: String, username: String, password: String) {
        if(!isDisplayNameValid(displayName)) {
            _registerForm.value = RegisterFormState(displayNameError = R.string.invalid_displayname)
        } else if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    private fun isDisplayNameValid(displayName: String): Boolean {
        return displayName.trim().isNotEmpty()
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            false
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }
}