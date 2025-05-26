package com.app.aprendequechua.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.app.aprendequechua.R
import com.google.android.material.appbar.MaterialToolbar

class EjerciciosBasicosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ejercicios_basicos, container, false)

        val btnPracticar = view.findViewById<Button>(R.id.btnPracticar)
        btnPracticar.setOnClickListener {
            // Aquí se hace la navegación al fragmento LeccionFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LeccionFragment()) // Usa tu fragmento LeccionFragment
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }


}