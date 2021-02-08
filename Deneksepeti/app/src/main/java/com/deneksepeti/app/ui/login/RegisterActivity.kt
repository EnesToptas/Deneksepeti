package com.deneksepeti.app.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.deneksepeti.app.MainActivity

import com.deneksepeti.app.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val displayName = findViewById<EditText>(R.id.displayname)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val join = findViewById<TextView>(R.id.go_button)
        val cancel = findViewById<TextView>(R.id.cancel_button)
        val loading = findViewById<ProgressBar>(R.id.loading)

        join.isEnabled = false

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this))
                .get(LoginViewModel::class.java)

        loginViewModel.registerFormState.observe(this@RegisterActivity, Observer {
            val registerState = it ?: return@Observer

            // disable login button unless all displayname / username / password is valid
            join.isEnabled = registerState.isDataValid

            if (registerState.displayNameError != null) {
                displayName.error = getString(registerState.displayNameError)
            }
            if (registerState.usernameError != null) {
                username.error = getString(registerState.usernameError)
            }
            if (registerState.passwordError != null) {
                password.error = getString(registerState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@RegisterActivity, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
                loading.visibility = View.GONE
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                setResult(Activity.RESULT_OK)
                finish()
            }

        })

        username.afterTextChanged {
            loginViewModel.registerDataChanged(
                    displayName.text.toString(),
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.registerDataChanged(
                        displayName.text.toString(),
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        loading.visibility = View.VISIBLE
                        GlobalScope.launch {
                            loginViewModel.register(
                                    displayName.text.toString(),
                                    username.text.toString(),
                                    password.text.toString()
                            )
                        }
                    }
                }
                false
            }

            join.setOnClickListener {
                loading.visibility = View.VISIBLE
                GlobalScope.launch {
                    loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                    )
                }
            }

            cancel.setOnClickListener{
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
