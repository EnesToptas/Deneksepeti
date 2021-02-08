package com.deneksepeti.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.animation.AnimationUtils
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import com.deneksepeti.app.clientSocket.ClientHandler
import com.deneksepeti.app.data.LoginRepository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class CreateActivity : AppCompatActivity() {

    lateinit var loggedUserId: String
    lateinit var displayName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val loginRepository = LoginRepository.getThis(this)
        loggedUserId = loginRepository.user?.userId!!
        displayName = loginRepository.user?.displayName!!

        findViewById<Button>(R.id.submit_button).setOnClickListener {

            val imageInput = findViewById<EditText>(R.id.imageInput)
            if(!isImageInputValid(imageInput.text.toString())) {
                shakeInput(imageInput)
                imageInput.setError("Enter a valid URL!")
                return@setOnClickListener
            }

            val titleInput = findViewById<EditText>(R.id.titleInput)
            if (titleInput.text.toString().length == 0) {
                shakeInput(titleInput)
                titleInput.setError("Enter a valid title!")
                return@setOnClickListener
            }

            val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
            if (descriptionInput.text.toString().length == 0) {
                shakeInput(descriptionInput)
                descriptionInput.setError("Enter a valid description!")
                return@setOnClickListener
            }

            val requirementsInput = findViewById<EditText>(R.id.requirementsInput)
            if (requirementsInput.text.toString().length == 0) {
                shakeInput(requirementsInput)
                requirementsInput.setError("Enter requirements!")
                return@setOnClickListener
            }

            val quotaInput = findViewById<EditText>(R.id.quotaInput)
            if (quotaInput.text.toString().toIntOrNull() == null ||  quotaInput.text.toString().toIntOrNull()!! <= 0 ) {
                shakeInput(quotaInput)
                quotaInput.setError("Enter a valid quota!")
                return@setOnClickListener
            }

            val dateInput = findViewById<EditText>(R.id.dateInput)
            if (!isDateInputValid(dateInput.text.toString())) {
                shakeInput(dateInput)
                dateInput.setError("Enter a valid date!")
                return@setOnClickListener
            }
            val dateToSend: Date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)
                .parse(dateInput.text.toString())!!

            val deney = Deney(imageInput.text.toString(), titleInput.text.toString(), displayName, descriptionInput.text.toString(), quotaInput.text.toString(),
                dateToSend, "", arrayListOf(), loggedUserId, requirementsInput.text.toString()
            )

            var gsonObject = GsonBuilder().setDateFormat("dd/MM/yyyy").create()
                    .toJsonTree(deney).asJsonObject
            gsonObject.addProperty("quota", quotaInput.text.toString().toInt())

            val dataToSend: String = Gson().toJson(gsonObject)
            val newPostId = ClientHandler.createPost(dataToSend)

            Snackbar.make( it, "Created the Deney!", Snackbar.LENGTH_LONG ).show()

            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("com.deneksepeti.app.deney.postId", newPostId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.cancel_button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isDateInputValid(text: String): Boolean {
        try {
            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            formatter.parse(text)
            if(Date().after(DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE).parse(text)!!))
                return false
            return true
        } catch (e: DateTimeParseException) {
            return false
        }
    }

    private fun isImageInputValid(text: String): Boolean {
        if(text.length == 0) return false
        return URLUtil.isValidUrl(text)
    }

    fun shakeInput(editText: EditText) {
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        editText.startAnimation(shake)
    }

}