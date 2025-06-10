package com.app.aprendequechua.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.aprendequechua.R

class JuegosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_juegos, container, false)

        val txtVerJuegoQuiz = view.findViewById<View>(R.id.txtVerJuegoQuiz)
        val txtVerJuegoMemoramas = view.findViewById<View>(R.id.txtVerJuegoMemoramas)

        txtVerJuegoQuiz.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, QuizFragment())
                .addToBackStack(null) // Para que pueda regresar con el botón atrás
                .commit()
        }

        txtVerJuegoMemoramas.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MemoramasFragment())
                .addToBackStack(null) // Para que pueda regresar con el botón atrás
                .commit()
        }

        return view
    }
}
