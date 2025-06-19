// ChallengeRepository.kt
package com.app.aprendequechua.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class DailyChallenge(
    val id: String = "",
    val titulo_pregunta: String = "",
    val pregunta: String = "",
    val respuesta_correcta: String = "",
    val pista: String = "",
    val dificultad: String = ""
)

class ChallengeRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val challengesCollection = firestore.collection("daily_challenges")

    suspend fun getRandomChallenge(): DailyChallenge? {
        return try {
            val querySnapshot = challengesCollection.get().await()
            val documents = querySnapshot.documents
            if (documents.isNotEmpty()) {
                val randomIndex = (0 until documents.size).random()
                documents[randomIndex].toObject(DailyChallenge::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
