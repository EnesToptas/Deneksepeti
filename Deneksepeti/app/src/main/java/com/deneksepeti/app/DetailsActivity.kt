package com.deneksepeti.app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.deneksepeti.app.clientSocket.ClientHandler
import com.deneksepeti.app.data.LoginRepository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var postId: String
    private lateinit var ownerUserId: String
    private lateinit var loggedUserId: String
    private lateinit var deney: Deney

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val loginRepository = LoginRepository.getThis(this)
        loggedUserId = loginRepository.user?.userId!!

        putValues()

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.button).setOnClickListener {
            if (!deney.users.contains(loggedUserId)) {

                var response = ClientHandler.applyPost(postId, loggedUserId)

                if(response == "-2")
                    Snackbar.make(it, "You already applied!", Snackbar.LENGTH_LONG).show()
                else if(response == "-1") {
                    Snackbar.make(it, "Quota is full! Sorry!", Snackbar.LENGTH_LONG).show()
                    GlobalScope.launch{
                        deney.quota = "0"
                        findViewById<TextView>(R.id.quotaDetails).text = deney.quota
                    }
                }
                else {
                    Snackbar.make(it, "Applied!", Snackbar.LENGTH_LONG).show()
                    GlobalScope.launch{
                        deney.quota = response
                        findViewById<TextView>(R.id.quotaDetails).text = deney.quota
                    }
                }

            }
            else Snackbar.make(it, "You already applied!", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if( ownerUserId == loggedUserId )
            menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.deleteDetails -> {
                val statusCode = ClientHandler.deletePost(postId)
                if( statusCode == "200" )
                    Toast.makeText(this, "Deleted the deney!", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "Deney is already deleted!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun putValues() {
        val imageView = findViewById<ImageView>(R.id.imageDetail)

        val posts = ClientHandler.getAllPosts()
        postId = intent.getStringExtra("com.deneksepeti.app.deney.postId")!!
        val postObject = JsonParser().parse(posts).asJsonArray.first {
            var s: String = it.asJsonObject.get("postId").asString
            return@first (s == postId)
        }
        deney = GsonBuilder().setDateFormat("dd/MM/yyyy").create().fromJson(postObject, Deney::class.java)

        Glide.with(imageView)
            .load(deney.image)
            .into(imageView)

        findViewById<TextView>(R.id.quotaDetails).text = deney.quota
        findViewById<TextView>(R.id.titleDetail).text = deney.title
        findViewById<TextView>(R.id.descriptionDetail).text = deney.description
        findViewById<TextView>(R.id.requerimentsDetail).text = deney.requirements
        findViewById<TextView>(R.id.deadlineDetail).text = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)
                                                .format(deney.deadline)

        var ownerName = "${deney.displayName}"

        findViewById<TextView>(R.id.contactDetail).text = ownerName

        postId = deney.postId
        ownerUserId = deney.userId
    }
}

