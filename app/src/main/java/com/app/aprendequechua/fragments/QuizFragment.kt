package com.app.aprendequechua.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.aprendequechua.R
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore

class QuizFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val IrQuizBasico = view.findViewById<Chip>(R.id.IrQuizBasico)
        val IrQuizIntermedio = view.findViewById<Chip>(R.id.IrQuizIntermedio)
        val IrQuizAvanzado = view.findViewById<Chip>(R.id.IrQuizAvanzado)

        db = FirebaseFirestore.getInstance()

        // Consultar la cantidad de quizzes disponibles
        loadQuizCountFromCollection("quizzes_basico") { count ->
            IrQuizBasico.text = count?.let { "$it quizzes disponibles" } ?: "No hay quizzes disponibles"
        }

        loadQuizCountFromCollection("quizzes_intermedio") { count ->
            IrQuizIntermedio.text = count?.let { "$it quizzes disponibles" } ?: "No hay quizzes disponibles"
        }

        loadQuizCountFromCollection("quizzes_avanzado") { count ->
            IrQuizAvanzado.text = count?.let { "$it quizzes disponibles" } ?: "No hay quizzes disponibles"
        }



        // Configurar el clic en el Chip
        IrQuizBasico.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ResolverQuizFragment())
                .addToBackStack(null) // Para que pueda regresar con el botón atrás
                .commit()
        }
        // Configurar el clic en el Chip
        IrQuizIntermedio.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ResolverQuizIntermedioFragment())
                .addToBackStack(null) // Para que pueda regresar con el botón atrás
                .commit()
        }
        // Configurar el clic en el Chip
        IrQuizAvanzado.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ResolverQuizAvanzadoFragment())
                .addToBackStack(null) // Para que pueda regresar con el botón atrás
                .commit()
        }
    }

    // Función para cargar la cantidad de quizzes disponibles
    private fun loadQuizCountFromCollection(
        collectionName: String,
        onResult: (Int?) -> Unit
    ) {
        db.collection(collectionName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(querySnapshot.size())
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar quizzes de $collectionName: ${exception.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
    }



}