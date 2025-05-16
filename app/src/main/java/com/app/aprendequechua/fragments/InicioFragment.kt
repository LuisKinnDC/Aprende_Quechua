package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

import androidx.fragment.app.FragmentTransaction

class InicioFragment : Fragment() {

    private lateinit var textViewSaludo: TextView
    private lateinit var textViewNombreUsuario: TextView
    private lateinit var layoutDiccionario: FrameLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragment
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Inicializa Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencias a las vistas
        textViewSaludo = view.findViewById(R.id.txtSaludo)
        textViewNombreUsuario = view.findViewById(R.id.txtNombreUsuario)
        layoutDiccionario = view.findViewById(R.id.layoutDiccionario)

        // Configurar el nombre del usuario autenticado
        setupUserName()

        // Configurar el saludo dinámico
        setupDynamicGreeting()

        // Configurar el clic en el ícono del diccionario
        setupDictionaryClickListener()

        return view
    }

    // Configurar el nombre del usuario autenticado
    private fun setupUserName() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            // Leer los datos del usuario desde Firestore
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Recuperar el nombre del usuario desde Firestore
                        val nombreFirestore = document.getString("nombre") ?: "Usuario"
                        textViewNombreUsuario.text = nombreFirestore
                    } else {
                        // Si no hay datos en Firestore, usar el nombre de Firebase Auth
                        val displayName = user.displayName ?: "Usuario"
                        textViewNombreUsuario.text = displayName
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar errores al leer los datos de Firestore
                    println("Error al cargar el nombre del usuario: ${exception.message}")
                    val displayName = user.displayName ?: "Usuario"
                    textViewNombreUsuario.text = displayName
                }
        } else {
            // Si no hay usuario autenticado, mostrar un valor predeterminado
            textViewNombreUsuario.text = "Usuario no autenticado"
        }
    }

    // Configurar el saludo dinámico
    private fun setupDynamicGreeting() {
        // Obtener la hora actual
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        // Determinar el saludo basado en la hora
        val greeting = when {
            hourOfDay in 5..11 -> "Hola👋, ¡Buenos días!"   // 05:00 a.m. – 11:59 a.m.
            hourOfDay in 12..18 -> "Hola👋, ¡Buenas tardes!" // 12:00 p.m. – 6:59 p.m.
            else -> "Hola👋, ¡Buenas noches!"              // 7:00 p.m. – 4:59 a.m.
        }

        // Mostrar el saludo
        textViewSaludo.text = greeting
    }

    // Configurar el clic en el ícono del diccionario
    private fun setupDictionaryClickListener() {
        layoutDiccionario.setOnClickListener {
            // Crear una instancia del fragmento de diccionario
            val diccionarioFragment = DiccionarioFragment()

            // Realizar la transición al fragmento de diccionario
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, diccionarioFragment) // Reemplaza el contenedor principal
                .addToBackStack(null) // Agrega la transacción al back stack
                .commit()
        }
    }
}