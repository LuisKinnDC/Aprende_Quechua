package com.app.aprendequechua.activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.aprendequechua.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si el usuario ya está autenticado
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("AuthDebug", "Usuario autenticado: ${currentUser.uid}")
            redirectToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        // Configurar los botones de inicio de sesión
        setupLoginButtons()

        val linearLayoutIniciarFacebook = findViewById<LinearLayout>(R.id.linearLayoutIniciarFacebook)
        linearLayoutIniciarFacebook.setOnClickListener {
            Toast.makeText(this, "Iniaciar Sesion con Facebook aún no está implementada", Toast.LENGTH_SHORT).show()
        }

        // Botón "Volver"
        val layoutVolver = findViewById<LinearLayout>(R.id.layoutVolver)
        layoutVolver.setOnClickListener {
            startActivity(Intent(this, InicioLoginRegisterActivity::class.java))
            finish()
        }
        // Botón "¿Olvidaste tu contraseña?"
        val txtForgotPassword = findViewById<TextView>(R.id.txtForgotPassword)
        txtForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun setupLoginButtons() {
        val inputLayoutEmail = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputLayoutEmail)
        val inputLayoutPassword = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputLayoutContraseña)
        val editTextEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTxtName)
        val editTextPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTxtContrasena)

        val buttonIniciarSesion = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.buttonIniciarSesion)
        buttonIniciarSesion.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Resetear errores
            inputLayoutEmail.error = null
            inputLayoutPassword.error = null

            var isValid = true

            if (email.isEmpty()) {
                inputLayoutEmail.error = "Este campo es obligatorio"
                isValid = false
            } else if (!isValidEmail(email)) {
                inputLayoutEmail.error = "Correo inválido"
                isValid = false
            }

            if (password.isEmpty()) {
                inputLayoutPassword.error = "Este campo es obligatorio"
                isValid = false
            } else if (password.length < 6) {
                inputLayoutPassword.error = "Mínimo 6 caracteres"
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            signInWithEmail(email, password)
        }

        val layoutIniciarGoogle = findViewById<LinearLayout>(R.id.layoutIniciarGoogle)
        layoutIniciarGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    redirectToDashboard()
                } else {
                    Log.e("AuthDebug", "Error al iniciar sesión", task.exception)
                    showErrorDialog("Error de autenticación", task.exception?.localizedMessage ?: "Inténtalo de nuevo más tarde.")
                }
            }
    }


    private fun signInWithGoogle() {
        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Lanza la actividad de inicio de sesión de Google
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("AuthDebug", "Cuenta de Google obtenida: ${account?.email}")
                firebaseAuthWithGoogle(account?.idToken!!)
            } catch (e: ApiException) {
                Log.e("AuthDebug", "Error en Google Sign-In: ${e.message}")
                when (e.statusCode) {
                    12501 -> Toast.makeText(this, "Inicio de sesión cancelado por el usuario", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("AuthDebug", "Inicio de sesión con Google exitoso")
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    redirectToDashboard()
                } else {
                    Log.e("AuthDebug", "Error al autenticar con Firebase: ${task.exception?.message}")
                    Toast.makeText(this, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun redirectToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showForgotPasswordDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Recuperar Contraseña")

        // Inflar el diseño personalizado del diálogo
        val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        builder.setView(view)

        val editTextEmail = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextEmail)

        builder.setPositiveButton("Enviar") { dialog, _ ->
            val email = editTextEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Por favor, ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            sendPasswordResetEmail(email)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun showErrorDialog(title: String, message: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_error, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnAccept = dialogView.findViewById<Button>(R.id.btnDialogAccept)

        tvTitle.text = title
        tvMessage.text = message

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnAccept.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthDebug", "Correo de recuperación enviado a $email")
                    Toast.makeText(this, "Se ha enviado un correo de recuperación a $email", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("AuthDebug", "Error al enviar correo de recuperación: ${task.exception?.message}")
                    Toast.makeText(this, "Error al enviar correo de recuperación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}