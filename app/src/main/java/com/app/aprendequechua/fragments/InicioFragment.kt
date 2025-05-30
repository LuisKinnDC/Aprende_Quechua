package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.app.aprendequechua.data.UserProgressRepository
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InicioFragment : Fragment() {

    // Referencias a las vistas
    private lateinit var textViewSaludo: TextView
    private lateinit var textViewNombreUsuario: TextView
    private lateinit var layoutDiccionario: FrameLayout

    // Referencias para el Desafío Diario
    private lateinit var txtTituloPregunta: TextView
    private lateinit var txtPreguntaDesafio: TextView
    private lateinit var inputRespuesta: TextInputEditText
    private lateinit var btnVerificarRespuesta: Button

    // Referencias para el Progreso
    private lateinit var contadorProgreso: TextView
    private lateinit var progresoDiario: LinearProgressIndicator
    private lateinit var progressBar: ProgressBar

    // Firebase y Repositorio
    private lateinit var db: FirebaseFirestore
    private lateinit var userProgressRepository: UserProgressRepository

    // Variables de estado
    private var currentChallengeIndex = 0
    private var challenges: List<DailyChallenge> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Inicializa Firestore y el repositorio de progreso
        db = FirebaseFirestore.getInstance()
        userProgressRepository = UserProgressRepository()

        // Referencias a las vistas
        textViewSaludo = view.findViewById(R.id.txtSaludo)
        textViewNombreUsuario = view.findViewById(R.id.txtNombreUsuario)
        layoutDiccionario = view.findViewById(R.id.cardDictionary)
        txtTituloPregunta = view.findViewById(R.id.txtTituloPregunta)
        txtPreguntaDesafio = view.findViewById(R.id.txtPreguntaDesafio)
        inputRespuesta = view.findViewById(R.id.inputRespuesta)
        btnVerificarRespuesta = view.findViewById(R.id.btnVerificarRespuesta)
        contadorProgreso = view.findViewById(R.id.contadorProgeso)
        progresoDiario = view.findViewById(R.id.progresoDiario)
        progressBar = view.findViewById(R.id.progressBar)

        // Configurar el nombre del usuario autenticado
        setupUserName()
        // Configurar el saludo dinámico
        setupDynamicGreeting()
        // Configurar el clic en el ícono del diccionario
        setupDictionaryClickListener()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserProgress()
    }


    // Configurar el nombre del usuario autenticado
    private fun setupUserName() {
        val user = FirebaseAuth.getInstance().currentUser
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

    // Cargar el progreso del usuario desde Firestore
    private fun loadUserProgress() {
        progressBar.visibility = View.VISIBLE
        userProgressRepository.getUserProgress(
            onSuccess = { completedChallenges, challengesCompleted ->
                progressBar.visibility = View.GONE
                currentChallengeIndex = completedChallenges
                if (challengesCompleted) {
                    showCompletionMessage()
                } else {
                    loadChallenges()
                }
                updateProgress()
            },
            onFailure = { message ->
                progressBar.visibility = View.GONE
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Cargar los desafíos desde Firestore
    private fun loadChallenges() {
        val userId = userProgressRepository.getUserId()
        if (userId == null) {
            println("Usuario no autenticado")
            return
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        db.collection("user_progress").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val lastAccessDate = document.getString("lastAccessDate") ?: ""
                    val selectedChallenges = document.get("selectedChallenges") as? List<String> ?: emptyList()

                    if (lastAccessDate == today && selectedChallenges.isNotEmpty()) {
                        // Cargar los desafíos almacenados
                        val challengesCollection = db.collection("daily_challenges")
                        challengesCollection
                            .whereIn("titulo_pregunta", selectedChallenges)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    challenges = querySnapshot.documents.mapNotNull { it.toObject(DailyChallenge::class.java) }
                                    showCurrentChallenge()
                                } else {
                                    // No hay desafíos disponibles
                                    txtTituloPregunta.text = "No hay desafíos disponibles."
                                    txtPreguntaDesafio.text = ""
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Error al cargar los desafíos almacenados: ${exception.message}")
                                txtTituloPregunta.text = "Error al cargar los desafíos."
                                txtPreguntaDesafio.text = ""
                            }
                    } else {
                        // Cargar desafíos aleatorios
                        val challengesCollection = db.collection("daily_challenges")
                        challengesCollection
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    val allChallenges = querySnapshot.documents.mapNotNull { it.toObject(DailyChallenge::class.java) }
                                    challenges = allChallenges.shuffled().take(3)
                                    showCurrentChallenge()
                                } else {
                                    // No hay desafíos disponibles
                                    txtTituloPregunta.text = "No hay desafíos disponibles."
                                    txtPreguntaDesafio.text = ""
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Error al cargar los desafíos diarios: ${exception.message}")
                                txtTituloPregunta.text = "Error al cargar los desafíos."
                                txtPreguntaDesafio.text = ""
                            }
                    }
                } else {
                    // No hay datos en Firestore, cargar desafíos aleatorios
                    val challengesCollection = db.collection("daily_challenges")
                    challengesCollection
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val allChallenges = querySnapshot.documents.mapNotNull { it.toObject(DailyChallenge::class.java) }
                                challenges = allChallenges.shuffled().take(3)
                                showCurrentChallenge()
                            } else {
                                // No hay desafíos disponibles
                                txtTituloPregunta.text = "No hay desafíos disponibles."
                                txtPreguntaDesafio.text = ""
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Error al cargar los desafíos diarios: ${exception.message}")
                            txtTituloPregunta.text = "Error al cargar los desafíos."
                            txtPreguntaDesafio.text = ""
                        }
                }
            }
            .addOnFailureListener { exception ->
                println("Error al cargar el progreso del usuario: ${exception.message}")
                txtTituloPregunta.text = "Error al cargar los desafíos."
                txtPreguntaDesafio.text = ""
            }
    }

    // Mostrar el desafío actual
    private fun showCurrentChallenge() {
        if (currentChallengeIndex < challenges.size) {
            val challenge = challenges[currentChallengeIndex]
            txtTituloPregunta.text = challenge.titulo_pregunta
            txtPreguntaDesafio.text = challenge.pregunta
            inputRespuesta.setText("")
            updateProgress()

            btnVerificarRespuesta.setOnClickListener {
                val userAnswer = inputRespuesta.text.toString().trim()
                verifyAnswer(userAnswer, challenge.respuesta_correcta)
            }
        } else {
            markChallengesAsCompleted()
            showCompletionMessage()
        }
    }

    // Verificar la respuesta del usuario
    private fun verifyAnswer(userAnswer: String, correctAnswer: String?) {
        if (userAnswer == correctAnswer) {
            Toast.makeText(context, "¡Respuesta correcta!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Respuesta incorrecta. La respuesta era: $correctAnswer",
                Toast.LENGTH_SHORT
            ).show()
        }
        currentChallengeIndex++
        updateUserProgress()
        showCurrentChallenge()
    }

    // Marcar los desafíos como completados
    private fun markChallengesAsCompleted() {
        userProgressRepository.updateUserProgress(
            completedChallenges = challenges.size,
            challengesCompleted = true,
            onFailure = { message ->
                println(message)
            }
        )
    }

    // Mostrar el mensaje de finalización
    private fun showCompletionMessage() {
        txtTituloPregunta.text = "¡Has completado todos los desafíos!"
        txtPreguntaDesafio.text = ""
        inputRespuesta.setText("")
        inputRespuesta.isEnabled = false
        btnVerificarRespuesta.visibility = View.GONE
        updateProgress()
    }

    // Actualizar el contador y la barra de progreso
    private fun updateProgress() {
        val completedChallenges = currentChallengeIndex
        val totalChallenges = challenges.size

        val remainingChallenges = if (totalChallenges > completedChallenges) {
            totalChallenges - completedChallenges
        } else {
            0
        }

        if (remainingChallenges > 0) {
            contadorProgreso.text = "Tienes $remainingChallenges desafío${if (remainingChallenges > 1) "s" else ""} hoy"
        } else {
            contadorProgreso.text = "Ya completó todos los desafíos de hoy"
        }

        val progressPercentage = if (totalChallenges > 0) {
            (completedChallenges.toFloat() / totalChallenges) * 100
        } else {
            0f
        }

        progresoDiario.progress = progressPercentage.toInt()
    }

    // Actualizar el progreso del usuario en Firestore
    private fun updateUserProgress() {
        userProgressRepository.updateUserProgress(
            completedChallenges = currentChallengeIndex,
            challengesCompleted = currentChallengeIndex >= challenges.size,
            onFailure = { message ->
                println(message)
            }
        )
    }
}

data class DailyChallenge(
    val titulo_pregunta: String = "",
    val pregunta: String = "",
    val respuesta_correcta: String = ""
)