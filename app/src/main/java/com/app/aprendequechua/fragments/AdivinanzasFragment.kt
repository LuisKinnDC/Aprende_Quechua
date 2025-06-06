package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.app.aprendequechua.data.UserProgressRepository
import com.app.aprendequechua.models.Adivinanza
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class AdivinanzasFragment : Fragment() {

    private lateinit var txtAdivinanza: TextView
    private lateinit var txtHint: TextView
    private lateinit var btnHint: ImageButton
    private lateinit var cardOption1: CardView
    private lateinit var cardOption2: CardView
    private lateinit var cardOption3: CardView
    private lateinit var cardOption4: CardView
    private lateinit var completionScreen: LinearLayout
    private lateinit var txtDifficulty: Chip


    private val db = FirebaseFirestore.getInstance()
    private lateinit var userProgressRepository: UserProgressRepository

    private var currentRiddleIndex = 0
    private var dailyRiddles = mutableListOf<Adivinanza>()
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adivinanzas, container, false)

        txtAdivinanza = view.findViewById(R.id.txtAdivinanza)
        txtHint = view.findViewById(R.id.txtHint)
        btnHint = view.findViewById(R.id.btnHint)
        cardOption1 = view.findViewById(R.id.cardOption1)
        cardOption2 = view.findViewById(R.id.cardOption2)
        cardOption3 = view.findViewById(R.id.cardOption3)
        cardOption4 = view.findViewById(R.id.cardOption4)
        completionScreen = view.findViewById(R.id.completionScreen)
        txtDifficulty = view.findViewById(R.id.txtDifficulty)


        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userId = user.uid
            userProgressRepository = UserProgressRepository()
        } else {
            Toast.makeText(context, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            return view
        }

        setupClickListeners()
        loadDailyRiddles()

        return view
    }

    private fun loadDailyRiddles() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        userProgressRepository.getUserProgress(
            type = "adivinanzas",
            userId1 = userId,
            onSuccess = { completedChallenges, challengesCompleted, _ ->
                currentRiddleIndex = completedChallenges
                if (challengesCompleted) {
                    showCompletionMessage()
                } else {
                    fetchAllRiddles()
                }
            },
            onFailure = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun fetchAllRiddles() {
        db.collection("adivinanzas")
            .get()
            .addOnSuccessListener { result ->
                val allRiddles = result.toObjects(Adivinanza::class.java)
                selectDailyRiddles(allRiddles)
                showCurrentRiddle()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar las adivinanzas.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectDailyRiddles(allRiddles: List<Adivinanza>) {
        dailyRiddles = allRiddles.shuffled(SecureRandom()).take(5).toMutableList()
    }

    private fun showCurrentRiddle() {
        if (currentRiddleIndex >= dailyRiddles.size) {
            markChallengesAsCompleted()
            showCompletionMessage()
            return
        }

        val riddle = dailyRiddles[currentRiddleIndex]

        txtAdivinanza.text = riddle.pregunta
        txtHint.text = "Pista: ${riddle.pista}"
        txtHint.visibility = View.GONE
        btnHint.visibility = View.VISIBLE

        // Filtrar opciones válidas (no nulas, no vacías y distintas a la respuesta correcta)
        val opcionesDisponibles = listOf(riddle.opcion1, riddle.opcion2, riddle.opcion3, riddle.opcion4)
            .filter { !it.isNullOrBlank() && it != riddle.respuesta_correcta }

        // Crear conjunto para evitar duplicados y asegurar que respuesta correcta esté
        val opcionesFinales = mutableSetOf<String>()
        opcionesFinales.add(riddle.respuesta_correcta)
        opcionesFinales.addAll(opcionesDisponibles)

        // Si faltan opciones para llegar a 4, rellena con placeholders
        while (opcionesFinales.size < 4) {
            opcionesFinales.add("...")
        }

        // Mezclar y tomar sólo 4
        val opcionesMezcladas = opcionesFinales.shuffled().take(4)

        // Lista de cards para asignar
        val cards = listOf(cardOption1, cardOption2, cardOption3, cardOption4)

        // Asignar texto y visibilidad a cada tarjeta
        for (i in 0 until 4) {
            val texto = opcionesMezcladas[i]
            val card = cards[i]
            val tv = card.getChildAt(0) as TextView

            if (texto == "...") {
                card.visibility = View.GONE
            } else {
                card.visibility = View.VISIBLE
                tv.text = texto
            }

            card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            card.isSelected = false
        }

        // Mostrar nivel de dificultad en el Chip
        val dificultad = riddle.dificultad?.lowercase(Locale.getDefault())
        when {
            dificultad.equals("fácil", ignoreCase = true) || dificultad.equals("facil", ignoreCase = true) -> {
                txtDifficulty.text = "Fácil"
                txtDifficulty.setChipBackgroundColorResource(R.color.color_green_light)
                txtDifficulty.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_green_dark))
                txtDifficulty.visibility = View.VISIBLE
            }
            dificultad.equals("media", ignoreCase = true) -> {
                txtDifficulty.text = "Media"
                txtDifficulty.setChipBackgroundColorResource(R.color.color_blue_light)
                txtDifficulty.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_blue_dark))
                txtDifficulty.visibility = View.VISIBLE
            }
            dificultad.equals("difícil", ignoreCase = true) || dificultad.equals("dificil", ignoreCase = true) -> {
                txtDifficulty.text = "Difícil"
                txtDifficulty.setChipBackgroundColorResource(R.color.color_red_light)
                txtDifficulty.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_red_dark))
                txtDifficulty.visibility = View.VISIBLE
            }
            else -> {
                txtDifficulty.visibility = View.GONE
            }
        }


        // Actualiza progreso
        userProgressRepository.updateUserProgress(
            userId = userId,
            type = "adivinanzas",
            completedChallenges = currentRiddleIndex,
            challengesCompleted = false,
            onFailure = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }



    private fun verifyAnswer(selectedAnswer: String) {
        val correctAnswer = dailyRiddles[currentRiddleIndex].respuesta_correcta

        val options = listOf(cardOption1, cardOption2, cardOption3, cardOption4)
        val selectedCard = options.firstOrNull {
            (it.getChildAt(0) as TextView).text.toString() == selectedAnswer
        }

        if (selectedAnswer == correctAnswer) {
            selectedCard?.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            Toast.makeText(context, "¡Respuesta correcta!", Toast.LENGTH_SHORT).show()
        } else {
            selectedCard?.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            options.firstOrNull {
                (it.getChildAt(0) as TextView).text.toString() == correctAnswer
            }
                ?.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            Toast.makeText(
                context,
                "Respuesta incorrecta. La respuesta era: $correctAnswer",
                Toast.LENGTH_SHORT
            ).show()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            currentRiddleIndex++
            showCurrentRiddle()
        }, 2000)
    }

    private fun markChallengesAsCompleted() {
        userProgressRepository.updateUserProgress(
            userId = userId,
            type = "adivinanzas",
            completedChallenges = dailyRiddles.size,
            challengesCompleted = true,
            onFailure = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showCompletionMessage() {
        txtAdivinanza.text = "¡Felicidades, has completado todas las adivinanzas de hoy!"
        txtHint.visibility = View.GONE
        btnHint.visibility = View.GONE
        listOf(cardOption1, cardOption2, cardOption3, cardOption4).forEach {
            it.visibility = View.GONE
        }
        completionScreen.visibility = View.VISIBLE

        val btnReturn = view?.findViewById<Button>(R.id.btnReturn)
        btnReturn?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, InicioFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun resetOptionCards() {
        listOf(cardOption1, cardOption2, cardOption3, cardOption4).forEach {
            it.isSelected = false
            it.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    @SuppressLint("ResourceType")
    private fun setupClickListeners() {
        btnHint.setOnClickListener {
            txtHint.visibility = View.VISIBLE
            txtHint.startAnimation(AnimationUtils.loadAnimation(context, R.animator.fade_in))
            btnHint.visibility = View.GONE
        }

        val options = listOf(cardOption1, cardOption2, cardOption3, cardOption4)
        for (option in options) {
            option.setOnClickListener {
                val selectedAnswer = (option.getChildAt(0) as TextView).text.toString()
                verifyAnswer(selectedAnswer)
            }
        }
    }
}
