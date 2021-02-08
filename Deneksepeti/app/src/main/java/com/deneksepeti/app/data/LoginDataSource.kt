package com.deneksepeti.app.data

import com.deneksepeti.app.clientSocket.ClientHandler
import com.deneksepeti.app.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val fakeUser = ClientHandler.login(username, password)
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun register(displayName: String, username: String, password: String): Result<LoggedInUser> {
        try {
            val fakeUser = ClientHandler.register(displayName, username, password)
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error signing in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}