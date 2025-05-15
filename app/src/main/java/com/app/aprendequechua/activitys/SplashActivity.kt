package com.app.aprendequechua.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.app.aprendequechua.R

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // Establecer el tema antes de llamar a super.onCreate
        setTheme(R.style.Theme_AprendeQuechua)

        super.onCreate(savedInstanceState)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Verificar si el usuario ya está autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuario autenticado: Redirigir directamente a DashboardActivity
            startActivity(Intent(this, DashboardActivity::class.java))
            finish() // Finalizar esta actividad para evitar volver atrás
        } else {
            // Usuario no autenticado: Mostrar la pantalla de bienvenida
            setContentView(R.layout.activity_splash)

            // Configurar el botón "Comenzar"
            val txtIniciarAhora = findViewById<TextView>(R.id.txtIniciarAhora)
            txtIniciarAhora.setOnClickListener {
                // Redirigir al usuario a la pantalla de registro/inicio de sesión
                val intent = Intent(this, InicioLoginRegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}