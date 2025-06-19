package com.app.aprendequechua.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.app.aprendequechua.fragments.InicioFragment
import com.app.aprendequechua.fragments.JuegosFragment
import com.app.aprendequechua.fragments.NivelesFragment
import com.app.aprendequechua.fragments.PerfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    // Fragmentos únicos
    private val inicioFragment = InicioFragment()
    private val perfilFragment = PerfilFragment()
    private val nivelesFragment = NivelesFragment()
    private val juegosFragment = JuegosFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Carga el fragment inicial
        loadFragment(inicioFragment)

        // Configura BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicioFragment -> {
                    loadFragment(inicioFragment)
                    true
                }
                R.id.perfilFragment -> {
                    loadFragment(perfilFragment)
                    true
                }
                R.id.leccionesFragment -> {
                    loadFragment(nivelesFragment)
                    true
                }
                R.id.juegosFragment -> {
                    loadFragment(juegosFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun selectProfileMenuItem() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.perfilFragment
    }

    fun selectGameMenuItem(){
        val  bottonNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottonNavigationView.selectedItemId = R.id.juegosFragment
    }

}