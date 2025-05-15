package com.app.aprendequechua.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.app.aprendequechua.R

class InicioLoginRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_login_register)

        // Referencias a los botones
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val btnCrear = findViewById<Button>(R.id.btnCrear)

        // Configurar el botón "Iniciar Sesión"
        btnEntrar.setOnClickListener {
            // Redirigir al usuario a la pantalla de inicio de sesión
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Configurar el botón "Crear Cuenta"
        btnCrear.setOnClickListener {
            // Redirigir al usuario a la pantalla de registro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}