package com.airmineral.airbagalert.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.airbagalert.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    data class SignInResult(
        val user: FirebaseUser? = null,
        val isNewUser: Boolean,
        val exception: Exception? = null
    )

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val path = User.COLLECTION_NAME

    suspend fun signIn(email: String, password: String): SignInResult {
        val result = MutableLiveData<SignInResult>()
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            result.value = SignInResult(
                authResult.user,
                authResult.additionalUserInfo?.isNewUser ?: false,
                null
            )
        } catch (e: Exception) {
            if (e.message?.contains("no user record") == true) {
                result.value = signUp(email, password)
            } else {
                result.value = SignInResult(null, false, e)
            }
        }
        return result.value!!
    }

    private suspend fun signUp(email: String, password: String): SignInResult {
        val result = MutableLiveData<SignInResult>()
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            result.value = SignInResult(
                authResult.user,
                authResult.additionalUserInfo?.isNewUser ?: false,
                null
            )
        } catch (e: Exception) {
            result.value = SignInResult(null, false, e)
        }
        return result.value!!
    }

    fun signOut() {
        auth.signOut()
    }

    fun getUserData(currentUserId: String?): LiveData<User?> {
        val user = MutableLiveData<User>()

        if (currentUserId != null) {
            db.collection(path).document(currentUserId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user.value = document.toObject(User::class.java)
                    }
                }
        }
        return user
    }

    fun resetPassword(email: String): Boolean {
        return auth.sendPasswordResetEmail(email).isSuccessful
    }

    fun register(user: User): Boolean {
        val result = db.collection(path).document(user.id).set(user).addOnFailureListener {
            throw it
        }
        return result.isSuccessful
    }
}