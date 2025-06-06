package com.app.aprendequechua.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.app.aprendequechua.data.UserProgressRepository
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class InicioFragment : Fragment() {

    private lateinit var profileImage: de.hdodenhof.circleimageview.CircleImageView


    // Constante para el tipo de desafío
    private val CHALLENGE_TYPE = "daily_challenges"

    // Referencias a las vistas
    private lateinit var textViewSaludo: TextView
    private lateinit var textViewNombreUsuario: TextView
    private lateinit var layoutDiccionario: FrameLayout
    private lateinit var layoutCuentos: FrameLayout
    private lateinit var layoutAdivinanzas: FrameLayout

    // Desafío Diario
    private lateinit var txtTituloPregunta: TextView
    private lateinit var txtPreguntaDesafio: TextView
    private lateinit var inputRespuesta: TextInputEditText
    private lateinit var btnVerificarRespuesta: Button

    // Progreso
    private lateinit var contadorProgreso: TextView
    private lateinit var progresoDiario: LinearProgressIndicator
    private lateinit var progressBar: ProgressBar

    // Firebase y repositorio
    private lateinit var db: FirebaseFirestore
    private lateinit var userProgressRepository: UserProgressRepository

    private var currentChallengeIndex = 0
    private var challenges: List<DailyChallenge> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        db = FirebaseFirestore.getInstance()
        userProgressRepository = UserProgressRepository()
        profileImage = view.findViewById(R.id.profileImage)


        textViewSaludo = view.findViewById(R.id.txtSaludo)
        textViewNombreUsuario = view.findViewById(R.id.txtNombreUsuario)
        layoutDiccionario = view.findViewById(R.id.cardDictionary)
        layoutCuentos = view.findViewById(R.id.cardCuentos)
        layoutAdivinanzas = view.findViewById(R.id.cardAdivinanzas)
        txtTituloPregunta = view.findViewById(R.id.txtTituloPregunta)
        txtPreguntaDesafio = view.findViewById(R.id.txtPreguntaDesafio)
        inputRespuesta = view.findViewById(R.id.inputRespuesta)
        btnVerificarRespuesta = view.findViewById(R.id.btnVerificarRespuesta)
        contadorProgreso = view.findViewById(R.id.contadorProgeso)
        progresoDiario = view.findViewById(R.id.progresoDiario)
        progressBar = view.findViewById(R.id.progressBar)

        setupUserName()
        setupDynamicGreeting()
        setupDictionaryClickListener()
        setupCuentosClickListener()
        setupAdivinanzasClickListener()
        loadProfileImage()


        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserProgress()
    }

    private fun loadProfileImage() {
        val user = FirebaseAuth.getInstance().currentUser
        val photoUrl = user?.photoUrl

        if (photoUrl != null && isAdded) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_defect_profile)
                .error(R.drawable.ic_defect_profile)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.ic_defect_profile)
        }
    }


    private fun setupUserName() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nombreFirestore = document.getString("nombre") ?: "Usuario"
                        textViewNombreUsuario.text = nombreFirestore
                    } else {
                        val displayName = user.displayName ?: "Usuario"
                        textViewNombreUsuario.text = displayName
                    }
                }
                .addOnFailureListener {
                    val displayName = user.displayName ?: "Usuario"
                    textViewNombreUsuario.text = displayName
                }
        } else {
            textViewNombreUsuario.text = "Usuario no autenticado"
        }
    }

    private fun setupDynamicGreeting() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hourOfDay in 5..11 -> "Hola👋, ¡Buenos días!"
            hourOfDay in 12..18 -> "Hola👋, ¡Buenas tardes!"
            else -> "Hola👋, ¡Buenas noches!"
        }
        textViewSaludo.text = greeting
    }

    private fun setupDictionaryClickListener() {
        layoutDiccionario.setOnClickListener {
            val diccionarioFragment = DiccionarioFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, diccionarioFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupCuentosClickListener() {
        layoutCuentos.setOnClickListener {
            val cuentosFragment = CuentosFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, cuentosFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupAdivinanzasClickListener() {
        layoutAdivinanzas.setOnClickListener {
            val adivinanzasFragment = AdivinanzasFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, adivinanzasFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadUserProgress() {
        progressBar.visibility = View.VISIBLE

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId == null) {
            progressBar.visibility = View.GONE
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        userProgressRepository.getUserProgress(
            type = CHALLENGE_TYPE,
            userId1 = userId,
            onSuccess = { completedChallenges, challengesCompleted, selectedChallenges ->
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

    private fun loadChallenges() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
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
                                    txtTituloPregunta.text = "No hay desafíos disponibles."
                                    txtPreguntaDesafio.text = ""
                                }
                            }
                            .addOnFailureListener {
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
                                    txtTituloPregunta.text = "No hay desafíos disponibles."
                                    txtPreguntaDesafio.text = ""
                                }
                            }
                            .addOnFailureListener {
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
                                txtTituloPregunta.text = "No hay desafíos disponibles."
                                txtPreguntaDesafio.text = ""
                            }
                        }
                        .addOnFailureListener {
                            txtTituloPregunta.text = "Error al cargar los desafíos."
                            txtPreguntaDesafio.text = ""
                        }
                }
            }
            .addOnFailureListener {
                txtTituloPregunta.text = "Error al cargar los desafíos."
                txtPreguntaDesafio.text = ""
            }
    }

    private fun showCurrentChallenge() {
        if (currentChallengeIndex < challenges.size) {
            val challenge = challenges[currentChallengeIndex]
            txtTituloPregunta.text = challenge.titulo_pregunta
            txtPreguntaDesafio.text = challenge.pregunta
            inputRespuesta.setText("")
            inputRespuesta.isEnabled = true
            btnVerificarRespuesta.visibility = View.VISIBLE
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

    private fun verifyAnswer(userAnswer: String, correctAnswer: String?) {
        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
            Toast.makeText(context, "¡Respuesta correcta!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Respuesta incorrecta. La respuesta era: $correctAnswer", Toast.LENGTH_SHORT).show()
        }
        currentChallengeIndex++
        updateUserProgress()
        showCurrentChallenge()
    }

    private fun showCompletionMessage() {
        txtTituloPregunta.text = "¡Has completado todos los desafíos!"
        txtPreguntaDesafio.text = ""
        inputRespuesta.setText("")
        inputRespuesta.isEnabled = false
        btnVerificarRespuesta.visibility = View.GONE
        updateProgress()
    }

    private fun updateProgress() {
        val completedChallenges = currentChallengeIndex
        val totalChallenges = challenges.size
        val remainingChallenges = (totalChallenges - completedChallenges).coerceAtLeast(0)
        contadorProgreso.text = if (remainingChallenges > 0) {
            "Tienes $remainingChallenges desafío${if (remainingChallenges > 1) "s" else ""} hoy"
        } else {
            "Ya completó todos los desafíos de hoy"
        }
        val progressPercentage = if (totalChallenges > 0) {
            (completedChallenges.toFloat() / totalChallenges) * 100
        } else {
            0f
        }
        progresoDiario.progress = progressPercentage.toInt()
    }

    private fun updateUserProgress() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userProgressRepository.updateUserProgress(
            type = CHALLENGE_TYPE,
            completedChallenges = currentChallengeIndex,
            challengesCompleted = currentChallengeIndex >= challenges.size,
            onFailure = { message -> println(message) },
            userId = userId
        )
    }

    private fun markChallengesAsCompleted() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userProgressRepository.updateUserProgress(
            userId = userId,
            type = CHALLENGE_TYPE,
            completedChallenges = challenges.size,
            challengesCompleted = true,
            onFailure = { message -> println(message) }
        )
    }
}

data class DailyChallenge(
    val titulo_pregunta: String = "",
    val pregunta: String = "",
    val respuesta_correcta: String = ""
)
