package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.google.android.material.appbar.MaterialToolbar

class NivelesFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_niveles, container, false)

        val btnComenzarBasico = view.findViewById<View>(R.id.btn_comenzar_basico)
        btnComenzarBasico.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LeccionFragment())
                .addToBackStack(null)
                .commit()
        }
        val btnComenzarIntermedio = view.findViewById<View>(R.id.btn_comenzar_intermedio)
        btnComenzarIntermedio.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LeccionIntermedioFragment())
                .addToBackStack(null)
                .commit()
        }
        val btnComenzarAvanzado = view.findViewById<View>(R.id.btn_comenzar_avanzado)
        btnComenzarAvanzado.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LeccionAvanzadoFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }
}



