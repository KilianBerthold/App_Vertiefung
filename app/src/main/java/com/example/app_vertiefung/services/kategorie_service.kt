package com.example.app_vertiefung.services

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import models.KategorieModel

class KategorieService {

    //Lädt alle Kategorien aus der Datenbank und speichert sie als Liste
    suspend fun getAllKategorien(): MutableList<KategorieModel> {
        val db = Firebase.firestore

        val result: QuerySnapshot = try {
            db.collection("Kategorien").orderBy("Name").get().await()
        } catch (exception: Exception) {
            print(exception.stackTrace)
            print(exception.message)
        } as QuerySnapshot

        if (!result.isEmpty) {
            val kategorienList: MutableList<KategorieModel> =
                emptyList<KategorieModel>().toMutableList()
            for (document in result.documents) {
                if (document != null) {
                    val kategorie = KategorieModel(
                        id = document.id,
                        name = document.get("Name") as String,
                        beschreibung = document.get("Beschreibung") as String
                    )
                    kategorienList.add(kategorie)
                }
            }
            return kategorienList
        } else {
            return emptyList<KategorieModel>().toMutableList()
        }
    }

    //Lädt die Namen aller Kategorien aus der Datenbank
    suspend fun getAllKategorieNames(): List<String> {
        val db = Firebase.firestore

        val result: QuerySnapshot = try {
            db.collection("Kategorien").get().await()
        } catch (exception: Exception) {
            print(exception.stackTrace)
            print(exception.message)
        } as QuerySnapshot

        return if (!result.isEmpty) {
            val kategorienList: MutableList<String> =
                emptyList<String>().toMutableList()
            for (document in result.documents) {
                if (document != null) {
                    kategorienList.add(document.get("Name") as String)
                }
            }
            kategorienList.toList()
        } else {
            emptyList()
        }
    }

    suspend fun updateKategorie(kategorie: KategorieModel) {
        val db = Firebase.firestore
        val mutableMapKategorie = kategorieModelToMap(kategorie)

        try {
            db.collection("Kategorien").document(kategorie.id).set(mutableMapKategorie).await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    suspend fun insertKategorie(kategorie: KategorieModel) {
        val db = Firebase.firestore
        val mutableMapKategorie = kategorieModelToMap(kategorie)

        try {
            db.collection("Kategorien").add(mutableMapKategorie).await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    suspend fun deleteKategorie(documentID: String, name: String) {
        val db = Firebase.firestore

        try {
            db.collection("Kategorien").document(documentID).delete().await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    //Nachdem der Name einer Kategorie geändert wurde, muss er auch in den Rezepten geändert werden in der sie verlinkt ist
    suspend fun updateRezeptKategorien(newKategorie: String, oldKategorie: String) {
        val db = Firebase.firestore

        try {
            val ids = db.collection("Rezepte").whereArrayContains("Kategorie", oldKategorie).get().await()
            for (document in ids.documents)
            {
                db.collection("Rezepte").document(document.id).update("Kategorie", FieldValue.arrayRemove(oldKategorie)).await()
                db.collection("Rezepte").document(document.id).update("Kategorie", FieldValue.arrayUnion(newKategorie)).await()
            }
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    //Nachdem eine Kategorie gelöscht wurde, muss sie aus den Rezepten gelöscht werden in der sie verlinkt war
    suspend fun cleanUpDeletedRezeptKategorien(kategorie: String) {
        val db = Firebase.firestore

        try {
            val ids = db.collection("Rezepte").whereArrayContains("Kategorie", kategorie).get().await()
            for (document in ids.documents)
            {
                db.collection("Rezepte").document(document.id).update("Kategorie", FieldValue.arrayRemove(kategorie)).await()
            }
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    private fun kategorieModelToMap(kategorie: KategorieModel): Map<String, Any> {
        return mapOf(
            "Beschreibung" to kategorie.beschreibung, "Name" to kategorie.name
        )

    }

}