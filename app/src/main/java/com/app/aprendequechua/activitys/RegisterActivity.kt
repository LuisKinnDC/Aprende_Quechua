package com.app.aprendequechua.activitys

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.aprendequechua.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // Instancias de Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Manejo de márgenes para pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón "Volver"
        val layoutVolver = findViewById<LinearLayout>(R.id.layoutVolver)
        layoutVolver.setOnClickListener {
            val intent = Intent(this, InicioLoginRegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Referencias a los campos de entrada
        val editTextName = findViewById<TextInputEditText>(R.id.editTextName)
        val editTextEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val editTextContrasena = findViewById<TextInputEditText>(R.id.editTextContrasena)
        val editTextConfContrasena =
            findViewById<TextInputEditText>(R.id.editTextConfContrasena)

        // Botón "Registrarse"
        val buttonRegistro = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.buttonRegistro)
        buttonRegistro.setOnClickListener {
            // Obtener los valores de los campos
            val nombre = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val contrasena = editTextContrasena.text.toString().trim()
            val confirmarContrasena = editTextConfContrasena.text.toString().trim()

            // Validar los campos
            if (validarCampos(nombre, email, contrasena, confirmarContrasena)) {
                registrarUsuario(email, contrasena, nombre)
            }
        }
    }

    // Función para validar los campos del formulario
    private fun validarCampos(
        nombre: String,
        email: String,
        contrasena: String,
        confirmarContrasena: String
    ): Boolean {
        if (nombre.isEmpty()) {
            showToast("El nombre no puede estar vacío")
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Por favor, ingresa un email válido")
            return false
        }
        if (contrasena.isEmpty() || contrasena.length < 6) {
            showToast("La contraseña debe tener al menos 6 caracteres")
            return false
        }
        if (contrasena != confirmarContrasena) {
            showToast("Las contraseñas no coinciden")
            return false
        }
        return true
    }

    // Función para registrar al usuario en Firebase Authentication
    private fun registrarUsuario(email: String, contrasena: String, nombre: String) {
        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            showToast("Registro exitoso. Por favor, verifica tu email.")
                            guardarDatosEnFirestore(user.uid, nombre, email)
                        } else {
                            showToast("Error al enviar el correo de verificación")
                        }
                    }
                } else {
                    // Error en el registro
                    showToast("Error al registrar usuario: ${task.exception?.message}")
                }
            }
    }

    // Función para guardar los datos adicionales del usuario en Firestore
    private fun guardarDatosEnFirestore(userId: String, nombre: String, email: String) {
        val usuario = hashMapOf(
            "nombre" to nombre,
            "email" to email
        )

        db.collection("usuarios").document(userId)
            .set(usuario)
            .addOnSuccessListener {
                showToast("Datos guardados correctamente")
                // Redirigir al usuario a la pantalla de inicio o login
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                showToast("Error al guardar datos: ${e.message}")
            }
    }

    // Función para mostrar mensajes Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}