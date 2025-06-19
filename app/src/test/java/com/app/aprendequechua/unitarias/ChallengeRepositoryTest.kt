package com.app.aprendequechua.unitarias

import com.app.aprendequechua.data.ChallengeRepository
import com.app.aprendequechua.data.DailyChallenge
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ChallengeRepositoryTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collectionRef: CollectionReference
    private lateinit var querySnapshot: QuerySnapshot
    private lateinit var documentSnapshot: DocumentSnapshot

    @Before
    fun setUp() {
        firestore = mock()
        collectionRef = mock()
        querySnapshot = mock()
        documentSnapshot = mock()

        `when`(firestore.collection("daily_challenges")).thenReturn(collectionRef)
    }

    @Test
    fun `getRandomChallenge returns a challenge when documents exist`() = runTest {
        val task: Task<QuerySnapshot> = Tasks.forResult(querySnapshot)

        `when`(collectionRef.get()).thenReturn(task)
        `when`(querySnapshot.documents).thenReturn(listOf(documentSnapshot))
        `when`(documentSnapshot.toObject(DailyChallenge::class.java)).thenReturn(
            DailyChallenge(
                id = "1",
                pregunta = "¿Imaynallan kachkanki?",
                respuesta_correcta = "Allinmi"
            )
        )

        val repository = ChallengeRepository(firestore)
        val result = repository.getRandomChallenge()

        assertNotNull(result)
    }

    @Test
    fun `getRandomChallenge returns null when no documents exist`() = runTest {
        val task: Task<QuerySnapshot> = Tasks.forResult(querySnapshot)

        `when`(collectionRef.get()).thenReturn(task)
        `when`(querySnapshot.documents).thenReturn(emptyList())

        val repository = ChallengeRepository(firestore)
        val result = repository.getRandomChallenge()

        assertNull(result)
    }
}