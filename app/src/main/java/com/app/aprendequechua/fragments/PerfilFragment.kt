package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.app.aprendequechua.activitys.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class PerfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var txtNameProfile: TextView
    private lateinit var emailProfile: TextView
    private lateinit var txtProvider: TextView
    private lateinit var txtFechaCumple: TextView
    private lateinit var txtGenero: TextView
    private lateinit var imgEdit: ImageView
    private lateinit var imgDelete: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return view
        }

        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                redirectToLogin()
            } else {
                user.getIdToken(true).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(requireContext(), "Error al renovar el token", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        txtNameProfile = view.findViewById(R.id.txtNameProfile)
        emailProfile = view.findViewById(R.id.emailProfile)
        txtProvider = view.findViewById(R.id.textView13)
        txtFechaCumple = view.findViewById(R.id.txtFechaCumple)
        txtGenero = view.findViewById(R.id.txtGenero)
        val btnCerrarSesion = view.findViewById<Button>(R.id.button6)
        imgEdit = view.findViewById(R.id.imgEdit)
        imgDelete = view.findViewById(R.id.imgDelete)

        setupUserProfile()
        loadUserProfile()

        btnCerrarSesion.setOnClickListener { showLogoutConfirmationDialog() }
        imgEdit.setOnClickListener { showEditProfileDialog() }
        imgDelete.setOnClickListener { deleteAccount() }

        txtFechaCumple.setOnClickListener {
            showDatePickerDialog(txtFechaCumple)
        }

        return view
    }

    private fun redirectToLogin() {
        Toast.makeText(requireContext(), "Por favor, inicia sesión", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun setupUserProfile() {
        val currentTime = System.currentTimeMillis()
        if (currentTime < 1700000000000L) {
            Toast.makeText(requireContext(), "Por favor, ajusta la hora de tu dispositivo", Toast.LENGTH_LONG).show()
            return
        }

        val user = auth.currentUser ?: return redirectToLogin()
        var displayName = user.displayName ?: "Usuario"
        for (profile in user.providerData) {
            displayName = profile.displayName ?: displayName
            if (displayName != "Usuario") break
        }
        txtNameProfile.text = displayName
        emailProfile.text = user.email ?: "Correo no disponible"

        var provider = "OTRO"
        for (profile in user.providerData) {
            provider = when (profile.providerId) {
                "google.com" -> "GOOGLE"
                "facebook.com" -> "FACEBOOK"
                "password" -> "EMAIL"
                else -> provider
            }
        }
        txtProvider.text = provider
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return redirectToLogin()
        user.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                db.collection("usuarios").document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            txtNameProfile.text = document.getString("nombre") ?: "Nombre no disponible"
                            emailProfile.text = document.getString("email") ?: "Correo no disponible"
                            txtGenero.text = document.getString("genero") ?: "Género no disponible"
                            txtFechaCumple.text = document.getString("cumpleaños") ?: "Cumpleaños no disponible"
                        } else {
                            Toast.makeText(requireContext(), "Datos no disponibles", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Error al renovar el token", Toast.LENGTH_SHORT).show()
                redirectToLogin()
            }
        }
    }

    private fun showEditProfileDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Editar Perfil")
        val view = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        builder.setView(view)

        val editTextName = view.findViewById<EditText>(R.id.editTextName)
        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
        val editTextBirthday = view.findViewById<TextInputEditText>(R.id.txtFechaCumple)
        val spinnerGender = view.findViewById<Spinner>(R.id.spinnerGender)

        editTextName.setText(txtNameProfile.text.toString())
        editTextEmail.setText(emailProfile.text.toString())
        editTextBirthday.setText(txtFechaCumple.text.toString())

        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.generos,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        val currentGender = txtGenero.text.toString()
        val genderPosition = genderAdapter.getPosition(currentGender)
        if (genderPosition >= 0) {
            spinnerGender.setSelection(genderPosition)
        }

        editTextBirthday.setOnClickListener {
            showDatePickerDialog(editTextBirthday)
        }

        builder.setPositiveButton("Guardar") { _, _ ->
            saveEditedProfile(
                editTextName.text.toString(),
                editTextEmail.text.toString(),
                spinnerGender.selectedItem.toString(),
                editTextBirthday.text.toString()
            )
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun saveEditedProfile(name: String, email: String, gender: String, birthday: String) {
        if (name.isBlank() || email.isBlank()) {
            Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (birthday.isBlank()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Campo obligatorio")
                .setMessage("Por favor, selecciona tu fecha de cumpleaños.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        if (gender == "Seleccionar género") {
            AlertDialog.Builder(requireContext())
                .setTitle("Campo obligatorio")
                .setMessage("Por favor, selecciona un género válido.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val user = auth.currentUser ?: return
        val updates = mapOf(
            "nombre" to name,
            "email" to email,
            "genero" to gender,
            "cumpleaños" to birthday
        )

        db.collection("usuarios").document(user.uid)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                txtNameProfile.text = name
                emailProfile.text = email
                txtGenero.text = gender
                txtFechaCumple.text = birthday
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al actualizar el perfil: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                textView.text = formattedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            auth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            googleSignInClient.signOut().addOnCompleteListener {
                redirectToLogin()
            }
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser ?: return redirectToLogin()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Cuenta")
        builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
        builder.setPositiveButton("Sí") { _, _ ->
            db.collection("usuarios").document(user.uid)
                .delete()
                .addOnSuccessListener {
                    user.delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                            redirectToLogin()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error al eliminar la cuenta: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al eliminar los datos del usuario: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}