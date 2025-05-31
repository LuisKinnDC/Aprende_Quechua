package com.app.aprendequechua.data


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class UserProgressRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getUserProgress(
        type: String,
        userId1: String,
        onSuccess: (completedChallenges: Int, challengesCompleted: Boolean, selectedChallenges: List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("user_progress").document(userId1)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val completedChallenges = document.getLong("${type}_completedChallenges")?.toInt() ?: 0
                    val challengesCompleted = document.getBoolean("${type}_challengesCompleted") ?: false
                    val selectedChallenges = document.get("${type}_selectedChallenges") as? List<String> ?: emptyList()
                    onSuccess(completedChallenges, challengesCompleted, selectedChallenges)
                } else {
                    onSuccess(0, false, emptyList())
                }
            }
            .addOnFailureListener {
                onFailure("Error al obtener progreso")
            }
    }

    fun updateUserProgress(
        userId: String,
        type: String,
        completedChallenges: Int,
        challengesCompleted: Boolean,
        onFailure: (String) -> Unit
    ) {
        val data = mapOf(
            "${type}_completedChallenges" to completedChallenges,
            "${type}_challengesCompleted" to challengesCompleted
        )
        db.collection("user_progress").document(userId)
            .set(data, SetOptions.merge())
            .addOnFailureListener {
                onFailure("Error al actualizar progreso")
            }
    }
}

