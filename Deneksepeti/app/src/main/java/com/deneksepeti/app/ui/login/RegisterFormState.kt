package com.deneksepeti.app.ui.login

/**
 * Data validation state of the login form.
 */
data class RegisterFormState(val displayNameError: Int? = null,
                             val usernameError: Int? = null,
                             val passwordError: Int? = null,
                             val isDataValid: Boolean = false)