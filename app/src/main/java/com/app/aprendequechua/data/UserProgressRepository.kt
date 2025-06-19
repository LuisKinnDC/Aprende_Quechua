package com.app.aprendequechua.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UserProgressRepository (

    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    // Obtener el ID del usuario actual
    internal fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    // Obtener el progreso del usuario desde Firestore
    fun getUserProgress(
        onSuccess: (completedChallenges: Int, challengesCompleted: Boolean) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        val userId = getUserId()
        if (userId == null) {
            onFailure("Usuario no autenticado")
            return
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        db.collection("user_progress").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val lastAccessDate = document.getString("lastAccessDate") ?: ""
                    val completedChallenges = document.getLong("completedChallenges")?.toInt() ?: 0
                    val challengesCompleted = document.getBoolean("challengesCompleted") ?: false

                    // Si es un nuevo día, resetear el progreso
                    if (lastAccessDate != today) {
                        resetUserProgress(userId, today)
                        onSuccess(0, false)
                    } else {
                        // Si es el mismo día, recuperar el progreso existente
                        onSuccess(completedChallenges, challengesCompleted)
                    }
                } else {
                    // Es la primera vez que el usuario accede
                    resetUserProgress(userId, today)
                    onSuccess(0, false)
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Error al cargar el progreso del usuario: ${exception.message}")
            }
    }

    // Actualizar el progreso del usuario en Firestore
    fun updateUserProgress(
        completedChallenges: Int,
        challengesCompleted: Boolean,
        onFailure: (message: String) -> Unit
    ) {
        val userId = getUserId()
        if (userId == null) {
            onFailure("Usuario no autenticado")
            return
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        db.collection("user_progress").document(userId)
            .update(
                mapOf(
                    "lastAccessDate" to today,
                    "completedChallenges" to completedChallenges,
                    "challengesCompleted" to challengesCompleted
                )
            )
            .addOnFailureListener { exception ->
                onFailure("Error al actualizar el progreso del usuario: ${exception.message}")
            }
    }

    // Resetear el progreso del usuario para un nuevo día
    private fun resetUserProgress(userId: String, today: String) {
        val challengesCollection = db.collection("daily_challenges")
        challengesCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Convertir todos los documentos en objetos DailyChallenge
                    val allChallenges = querySnapshot.documents.mapNotNull { it.toObject(DailyChallenge::class.java) }

                    // Seleccionar 3 desafíos aleatorios
                    val selectedChallenges = allChallenges.shuffled().take(3)

                    // Guardar los desafíos seleccionados en Firestore
                    db.collection("user_progress").document(userId)
                        .set(
                            mapOf(
                                "lastAccessDate" to today,
                                "completedChallenges" to 0,
                                "challengesCompleted" to false,
                                "selectedChallenges" to selectedChallenges.map { it.titulo_pregunta } // Guardar solo los títulos
                            )
                        )
                        .addOnFailureListener { exception ->
                            println("Error al resetear el progreso del usuario: ${exception.message}")
                        }
                }
            }
            .addOnFailureListener { exception ->
                println("Error al cargar los desafíos diarios: ${exception.message}")
            }
    }
}