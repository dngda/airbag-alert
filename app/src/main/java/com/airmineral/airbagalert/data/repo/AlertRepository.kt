package com.airmineral.airbagalert.data.repo

import com.airmineral.airbagalert.data.model.Incident
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AlertRepository {

    private val path = Incident.COLLECTION_NAME
    private val db = Firebase.firestore

    fun saveAlertData(incident: Incident) {
        db.collection(path).document(incident.id).set(incident)
    }

    suspend fun getAlertData(): List<Incident> {
        val incidents = mutableListOf<Incident>()
        db.collection(path).get().addOnSuccessListener { result ->
            incidents.addAll(result.toObjects(Incident::class.java))
        }.await()
        return incidents.reversed()
    }
}