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
        loadQuizCount { quizCount ->
            if (quizCount != null) {
                IrQuizBasico.text = "$quizCount quizzes disponibles"
            } else {
                IrQuizBasico.text = "No hay quizzes disponibles"
            }
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

    private fun loadQuizCount(onSuccess: (Int?) -> Unit) {
        db.collection("quizzes_basico") // Asegúrate de que esta sea la colección correcta
            .get()
            .addOnSuccessListener { querySnapshot ->
                val quizCount = querySnapshot.size()
                onSuccess(quizCount)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar quizzes: ${exception.message}", Toast.LENGTH_SHORT).show()
                onSuccess(null)
            }
    }
}