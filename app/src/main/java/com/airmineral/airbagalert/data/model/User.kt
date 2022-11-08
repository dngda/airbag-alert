package com.airmineral.airbagalert.data.model

import com.google.firebase.firestore.DocumentId

@Suppress("unused")
data class User(
    @DocumentId
    var id: String,
    var agencyName: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
) {
    constructor() : this("", "", "", "")

    companion object {
        const val COLLECTION_NAME = "users"
        val EMPTY = User("", "", "", "")
    }
}