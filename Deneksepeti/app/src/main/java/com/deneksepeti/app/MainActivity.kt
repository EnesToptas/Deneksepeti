package com.deneksepeti.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deneksepeti.app.clientSocket.ClientHandler
import com.deneksepeti.app.data.LoginRepository
import com.deneksepeti.app.ui.login.LoginActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var recyclerAdapter: RecyclerAdapter
    private var list: ArrayList<Deney> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var posts = intent.getStringExtra("com.deneksepeti.app.deney.allPosts") ?: ClientHandler.getAllPosts()
        var postArray = JsonParser().parse(posts).asJsonArray
        for (post: JsonElement in postArray) {
            var d = GsonBuilder().setDateFormat("dd/MM/yyyy").create().fromJson(post, Deney::class.java)
            list.add( d as Deney)
        }

        var cardList = list.map { it.toCard() } as ArrayList<Card>
        recyclerAdapter = RecyclerAdapter(cardList, this, this)

        var recyclerView = findViewById<RecyclerView>(R.id.recycler)
        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                as RecyclerView.LayoutManager

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = recyclerAdapter

        recyclerAdapter.notifyDataSetChanged()

        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        val swipeContainer = findViewById<SwipeRefreshLayout>(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener{
            list.clear()
            posts = ClientHandler.getAllPosts()
            postArray = JsonParser().parse(posts).asJsonArray
            for (post: JsonElement in postArray) {
                var d = GsonBuilder().setDateFormat("dd/MM/yyyy").create().fromJson(post, Deney::class.java)
                list.add( d as Deney)
            }

            cardList = list.map { it.toCard() } as ArrayList<Card>

            recyclerAdapter.cards = cardList
            recyclerAdapter.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.logout -> {
                LoginRepository.getThis(this).logout()
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View, position: Int) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("com.deneksepeti.app.deney.postId", list[position].postId)
        startActivity(intent)
    }
}

