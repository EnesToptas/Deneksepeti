package com.deneksepeti.app.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.deneksepeti.app.data.model.LoggedInUser
import com.deneksepeti.app.ui.login.LoginViewModel

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource, val context: Context) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        val prefs = context.getSharedPreferences("local_info",Context.MODE_PRIVATE)
        var _userId = prefs.getString("userId", null)
        var _displayName = prefs.getString("displayName", null)
        user = if (_userId != null && _displayName != null) LoggedInUser(_userId, _displayName) else null
    }

    fun logout() {
        user = null
        val prefs = context.getSharedPreferences("local_info",Context.MODE_PRIVATE)
        prefs.edit().remove("userId").remove("displayName").apply()
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    fun register(displayName: String, username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.register(displayName, username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser

        val prefs = context.getSharedPreferences("local_info",Context.MODE_PRIVATE)
        prefs.edit()
                .putString("userId", loggedInUser.userId)
                .putString("displayName", loggedInUser.displayName)
                .apply()
    }

    companion object {

        private var a: LoginRepository? = null

        fun getThis(context: Context): LoginRepository {

            if( a == null) a = LoginRepository(LoginDataSource(), context)

            return a as LoginRepository
        }
    }
}