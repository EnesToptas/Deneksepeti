package com.deneksepeti.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.deneksepeti.app.clientSocket.ClientHandler
import com.deneksepeti.app.data.LoginRepository
import com.deneksepeti.app.ui.login.LoginActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        val loginRepository = LoginRepository.getThis(this)
        var intent: Intent
        if (loginRepository.isLoggedIn) {
            val posts = ClientHandler.getAllPosts()
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("com.deneksepeti.app.deney.allPosts", posts)
        }
        else intent = Intent(this, LoginActivity::class.java)
        Handler().postDelayed(
            {
                startActivity(intent)
                finish()
            },
            splashScreenDuration
        )
    }
    private fun getSplashScreenDuration() = 2000L
}