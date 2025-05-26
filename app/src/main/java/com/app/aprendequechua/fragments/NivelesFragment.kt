package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.aprendequechua.R
import com.google.android.material.appbar.MaterialToolbar

class NivelesFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_niveles, container, false)

        // Configurar el clic del botón "Comenzar" en la tarjeta Básico
        val buttonComenzarBasico = view.findViewById<View>(R.id.btn_comenzar_basico)
        buttonComenzarBasico.setOnClickListener {
            // Cargar el fragmento de ejercicios básicos
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, EjerciciosBasicosFragment())
            transaction.addToBackStack(null) // Permite regresar al fragmento anterior
            transaction.commit()
        }
        view.findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        return view
    }
}
