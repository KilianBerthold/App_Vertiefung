package com.example.app_vertiefung.services

import com.google.firebase.firestore.FirebaseFirestore
import models.KategorieModel
import models.RezeptModel

class KategorieService {

    //Lädt alle Kategorien aus der Datenbank und speichert sie als Liste
    //TODO: launch {} in der Funktion, die dieses Get aufruft! Coroutine
    suspend fun getAllKategorien(): MutableList<KategorieModel> {
        val db = FirebaseFirestore.getInstance();
        var kategorienList: MutableList<KategorieModel> = emptyList<KategorieModel>().toMutableList();

        db.collection("Kategorien")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (document in result.documents) {
                        if (document != null) {
                            val kategorie = KategorieModel(
                                name = document.get("Name") as String,
                                beschreibung = document.get("Beschreibung") as String
                            )
                            kategorienList.add(kategorie)
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                print(exception.message)
                print(exception.stackTrace)
            }
        return kategorienList
    }
}