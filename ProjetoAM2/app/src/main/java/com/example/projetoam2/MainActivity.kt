package com.example.projetoam2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
<<<<<<< HEAD
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
=======
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
>>>>>>> Rui
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
<<<<<<< HEAD
=======
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetoam2.Fragments.CalendarioFragment
import com.example.projetoam2.Fragments.HomeFragment
import com.example.projetoam2.Fragments.PerfilFragment
import com.example.projetoam2.Model.User
>>>>>>> Rui
import com.example.projetoam2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
<<<<<<< HEAD
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityMainBinding

    private lateinit var buttonListarperfis : Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
=======
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //variaveis
    private lateinit var auth: FirebaseAuth

    //variaveis fragment
    private val homeFragment = HomeFragment()
    private val calendarioFragment = CalendarioFragment()
    private val perfilFragment = PerfilFragment()

    //firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
>>>>>>> Rui

        // Initialize Firebase Auth
        auth = Firebase.auth

<<<<<<< HEAD
        val uid = FirebaseAuth.getInstance().uid
        database =
            FirebaseDatabase.getInstance("https://projetoam2-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("/Usuarios/$uid")


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

=======
        //fragment function
        replaceFragment(homeFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_calendario -> replaceFragment(calendarioFragment)
                R.id.ic_perfil -> replaceFragment(perfilFragment)
            }
            true
        }
    }

    private fun replaceFragment (fragment: Fragment){

        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }


>>>>>>> Rui
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout) {
            //write the login for logout
            val intent = Intent(this, LoginActivity::class.java)
            auth.signOut()
            startActivity(intent)
            finish()
            return true
        }
<<<<<<< HEAD
        if(item.itemId == R.id.sendMsg){
            val intent = Intent(this@MainActivity, ListarPerfis::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return true
    }


=======
        return true
    }
>>>>>>> Rui
}