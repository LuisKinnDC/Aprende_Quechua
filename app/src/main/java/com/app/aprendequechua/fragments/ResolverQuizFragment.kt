package com.app.aprendequechua.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore

class ResolverQuizFragment : Fragment() {

    // Referencias a las vistas
    private lateinit var quizContainer: LinearLayout
    private lateinit var completionScreen: LinearLayout
    private lateinit var txtResumenPuntaje: TextView
    private lateinit var btnReintentar: Button

    private lateinit var txtMostrarTotalQuizzes: TextView
    private lateinit var chipDificultad: Chip
    private lateinit var textPregunta: TextView
    private lateinit var layoutPista: LinearLayout
    private lateinit var textPista: TextView
    private lateinit var radioOpcion1: RadioButton
    private lateinit var radioOpcion2: RadioButton
    private lateinit var radioOpcion3: RadioButton
    private lateinit var radioOpcion4: RadioButton
    private lateinit var btnConfirmar: Button
    private lateinit var btnPista: ImageView
    private lateinit var textProgreso: TextView
    private lateinit var progressBar: LinearProgressIndicator

    private lateinit var db: FirebaseFirestore

    // Variables de estado
    private var currentQuizIndex = 0
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private val quizzes = mutableListOf<QuizModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resolver_quiz, container, false)

        // Referencias a las vistas
        quizContainer = view.findViewById(R.id.quizContainer)
        completionScreen = view.findViewById(R.id.completionScreen)
        txtResumenPuntaje = view.findViewById(R.id.text_final_score)
        btnReintentar = view.findViewById(R.id.btnReturn)

        txtMostrarTotalQuizzes = view.findViewById(R.id.txtMostrarTotalQuizzes)
        chipDificultad = view.findViewById(R.id.chip_dificultad)
        textPregunta = view.findViewById(R.id.text_pregunta)
        layoutPista = view.findViewById(R.id.layout_pista)
        textPista = view.findViewById(R.id.text_pista)
        radioOpcion1 = view.findViewById(R.id.radio_opcion1)
        radioOpcion2 = view.findViewById(R.id.radio_opcion2)
        radioOpcion3 = view.findViewById(R.id.radio_opcion3)
        radioOpcion4 = view.findViewById(R.id.radio_opcion4)
        btnConfirmar = view.findViewById(R.id.btn_confirmar)
        btnPista = view.findViewById(R.id.btn_pista)
        textProgreso = view.findViewById(R.id.text_progreso)
        progressBar = view.findViewById(R.id.progress_bar)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configurar el botón de pista
        btnPista.setOnClickListener {
            if (layoutPista.visibility == View.GONE) {
                layoutPista.visibility = View.VISIBLE
            } else {
                layoutPista.visibility = View.GONE
            }
        }

        // Configurar el botón de confirmar
        btnConfirmar.setOnClickListener {
            validarRespuesta()
        }

        // Configurar el botón de reintentar
        btnReintentar.setOnClickListener {
            reiniciarQuiz()
        }

        // Cargar los quizzes desde Firestore
        loadQuizzes()

        return view
    }

    private fun loadQuizzes() {
        db.collection("quizzes_basico")
            .get()
            .addOnSuccessListener { querySnapshot ->
                quizzes.clear()
                for (document in querySnapshot.documents) {
                    val quiz = document.toObject(QuizModel::class.java)
                    if (quiz != null) {
                        quizzes.add(quiz)
                    }
                }

                // Mostrar el total de quizzes disponibles
                txtMostrarTotalQuizzes.text = "${quizzes.size} preguntas disponibles"

                if (quizzes.isNotEmpty()) {
                    currentQuizIndex = 0
                    correctAnswers = 0
                    incorrectAnswers = 0
                    completionScreen.visibility = View.GONE
                    quizContainer.visibility = View.VISIBLE
                    showCurrentQuiz()
                } else {
                    Toast.makeText(context, "No hay quizzes disponibles.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar quizzes: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showCurrentQuiz() {
        if (currentQuizIndex < quizzes.size) {
            val quiz = quizzes[currentQuizIndex]

            // Mostrar los datos del quiz actual
            chipDificultad.text = quiz.dificultad
            textPregunta.text = quiz.pregunta
            radioOpcion1.text = quiz.opcion1
            radioOpcion2.text = quiz.opcion2
            radioOpcion3.text = quiz.opcion3
            radioOpcion4.text = quiz.opcion4
            textPista.text = quiz.pista // Asignar la pista desde la BD
            layoutPista.visibility = View.GONE // Ocultar la pista inicialmente

            // Limpiar la selección previa
            radioOpcion1.isChecked = false
            radioOpcion2.isChecked = false
            radioOpcion3.isChecked = false
            radioOpcion4.isChecked = false

            // Actualizar el progreso
            textProgreso.text = "${currentQuizIndex + 1}/${quizzes.size}"
            progressBar.max = quizzes.size
            progressBar.setProgress(currentQuizIndex + 1, true)
        } else {
            // Mostrar pantalla de finalización si no hay más quizzes
            mostrarPantallaFinal()
        }
    }

    private fun validarRespuesta() {
        if (currentQuizIndex >= quizzes.size) return

        val quiz = quizzes[currentQuizIndex]

        val selectedRadioButtonId = when {
            radioOpcion1.isChecked -> R.id.radio_opcion1
            radioOpcion2.isChecked -> R.id.radio_opcion2
            radioOpcion3.isChecked -> R.id.radio_opcion3
            radioOpcion4.isChecked -> R.id.radio_opcion4
            else -> -1
        }

        if (selectedRadioButtonId == -1) {
            Toast.makeText(context, "Por favor, selecciona una opción.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedAnswer = when (selectedRadioButtonId) {
            R.id.radio_opcion1 -> quiz.opcion1
            R.id.radio_opcion2 -> quiz.opcion2
            R.id.radio_opcion3 -> quiz.opcion3
            R.id.radio_opcion4 -> quiz.opcion4
            else -> ""
        }

        if (selectedAnswer.trim().equals(quiz.respuesta_correcta.trim(), ignoreCase = true)) {
            correctAnswers++
            Toast.makeText(context, "✅ ¡Respuesta correcta!", Toast.LENGTH_SHORT).show()
        } else {
            incorrectAnswers++
            Toast.makeText(
                context,
                "❌Respuesta incorrecta. La respuesta era: ${quiz.respuesta_correcta}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Avanzar automáticamente después de 2 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            currentQuizIndex++

            if (currentQuizIndex < quizzes.size) {
                showCurrentQuiz()
            } else {
                mostrarPantallaFinal()
            }
        }, 1000)
    }

    private fun mostrarPantallaFinal() {
        // Ocultar el contenedor del quiz y mostrar la pantalla de finalización
        quizContainer.visibility = View.GONE
        completionScreen.visibility = View.VISIBLE

        // Mostrar el resumen de puntaje
        txtResumenPuntaje.text =
            "🏆 ¡Felicidades, completaste el quiz!\n\nCorrectas: $correctAnswers\nIncorrectas: $incorrectAnswers"
    }

    private fun reiniciarQuiz() {
        currentQuizIndex = 0
        correctAnswers = 0
        incorrectAnswers = 0

        completionScreen.visibility = View.GONE
        quizContainer.visibility = View.VISIBLE
        showCurrentQuiz()
    }
}

// Modelo de datos
data class QuizModel(
    val pregunta: String = "",
    val opcion1: String = "",
    val opcion2: String = "",
    val opcion3: String = "",
    val opcion4: String = "",
    val dificultad: String = "",
    val respuesta_correcta: String = "",
    val pista: String = ""
)