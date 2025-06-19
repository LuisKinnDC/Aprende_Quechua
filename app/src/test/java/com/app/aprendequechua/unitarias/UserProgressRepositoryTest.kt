package com.app.aprendequechua.unitarias

import com.app.aprendequechua.data.UserProgressRepository
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals
class UserProgressRepositoryTest {

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockDocumentRef: DocumentReference
    private lateinit var mockTask: Task<DocumentSnapshot>
    private lateinit var repository: UserProgressRepository

    @Before
    fun setup() {
        mockAuth = mock()
        mockFirestore = mock()
        mockUser = mock()
        mockDocumentRef = mock()
        mockTask = mock()

        whenever(mockAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.uid).thenReturn("user123")
        whenever(mockFirestore.collection("user_progress")).thenReturn(mock())
        whenever(mockFirestore.collection("user_progress").document("user123")).thenReturn(mockDocumentRef)

        repository = UserProgressRepository(mockAuth, mockFirestore)
    }

    @Test
    fun `getUserProgress - success with today's date`() {
        val documentSnapshot = mock<DocumentSnapshot> {
            on { exists() } doReturn true
            on { getString("lastAccessDate") } doReturn "2025-06-17"
            on { getLong("completedChallenges") } doReturn 2L
            on { getBoolean("challengesCompleted") } doReturn true
        }

        // Simula addOnSuccessListener
        whenever(mockTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as OnSuccessListener<DocumentSnapshot>
            listener.onSuccess(documentSnapshot)
            mockTask
        }
        whenever(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

        var completed = -1
        var completedFlag = false

        repository.getUserProgress(
            onSuccess = { challenges, isCompleted ->
                completed = challenges
                completedFlag = isCompleted
            },
            onFailure = {
                assert(false) { "onFailure no debería ser llamado en este caso" }
            }
        )

        assertEquals(2, completed)
        assertEquals(true, completedFlag)
    }
}

