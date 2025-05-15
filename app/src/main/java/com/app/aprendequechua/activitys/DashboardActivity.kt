package com.app.aprendequechua.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.app.aprendequechua.fragments.InicioFragment
import com.app.aprendequechua.fragments.JuegosFragment
import com.app.aprendequechua.fragments.LeccionesFragment
import com.app.aprendequechua.fragments.PerfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Carga el fragment inicial
        loadFragment(InicioFragment())

        // Configura BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicioFragment -> {
                    loadFragment(InicioFragment())
                    true
                }
                R.id.perfilFragment -> {
                    loadFragment(PerfilFragment())
                    true
                }
                R.id.leccionesFragment -> {
                    loadFragment(LeccionesFragment())
                    true
                }
                R.id.juegosFragment -> {
                    loadFragment(JuegosFragment())
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
}