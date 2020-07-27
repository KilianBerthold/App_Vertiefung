package com.example.app_vertiefung.services

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.example.app_vertiefung.models.RezeptModel
import com.example.app_vertiefung.models.SlimRezeptModel
import com.example.app_vertiefung.models.SuchKriterienModel
import com.google.firebase.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList

class RezeptService {

    //Lädt alle ausgewählte Daten aller Dokumente aus der Collection "Rezepte"
    suspend fun getAllSlimRezepte(): MutableList<SlimRezeptModel> {
        val db = Firebase.firestore;

        val result: QuerySnapshot = try {
            db.collection("Rezepte").orderBy("Name").get().await()
        } catch (exception: Exception) {
            print(exception.stackTrace);
            print(exception.message)
        } as QuerySnapshot

        if (!result.isEmpty) {
            val rezepteList: MutableList<SlimRezeptModel> = emptyList<SlimRezeptModel>().toMutableList();
            for (document in result.documents) {
                if (document != null) {
                    var kategorien: MutableList<String> = emptyList<String>().toMutableList()
                    if (document.get("Kategorie") != null) {
                        kategorien = (document.get("Kategorie") as? ArrayList<*>)?.filterIsInstance<String>() as MutableList<String>
                    }
                    val slimRezept = SlimRezeptModel(
                        id = document.id,
                        anzahlGekocht = document.get("AnzahlGekocht") as Long,
                        kalorien = document.get("Kalorien") as Long,
                        kategorien = kategorien,
                        name = document.get("Name") as String,
                        zuletztGekochtAm = timeStampToDate(document.get("ZuletztGekochtAm") as Timestamp)
                    )
                    rezepteList.add(slimRezept)
                }
            }
            rezepteList.sortBy { it.name }
            return rezepteList
        } else {
            return emptyList<SlimRezeptModel>().toMutableList()
        }

    }

    //Lädt alle Daten eines bestimmten Rezepts aus der Datenbank
    suspend fun getRezept(ID: String): RezeptModel {
        val db = Firebase.firestore

        val result: DocumentSnapshot = try {
            db.collection("Rezepte").document(ID).get().await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        } as DocumentSnapshot

        if (result.exists()) {
            var kategorien: MutableList<String> =
                emptyList<String>().toMutableList()
            if (result.get("Kategorie") != null) {
                kategorien =
                    (result.get("Kategorie") as ArrayList<*>).filterIsInstance<String>() as MutableList<String>
            }
            return RezeptModel(
                rezeptID = result.id,
                rezeptAnzahlGekocht = result.get("AnzahlGekocht") as Long,
                rezeptKalorien = result.get("Kalorien") as Long,
                rezeptKategorien = kategorien,
                rezeptName = result.get("Name") as String,
                rezeptSchritte = (result.get("RezeptSchritte") as? ArrayList<*>)?.filterIsInstance<String>() as MutableList<String>,
                rezeptZuletztGekocht = timeStampToDate(result.get("ZuletztGekochtAm") as Timestamp),
                rezeptZutaten = anyMapToStringMap(result.get("Zutaten") as MutableMap<*, *>)
            )
        } else {
            return RezeptModel()
        }
    }

    suspend fun getRandomRezept(suchKriterien: SuchKriterienModel): RezeptModel {
        // Es ist möglich, 1-3 Suchparameter anzugeben, deshalb wird die Query Stück für Stück aufgebaut.
        // Ich habe keine Möglichkeit gefunden, innerhalb einer Map im Dokument zu filtern, weshalb die Zutaten im Backend und nicht in der Datenbank gefiltert werden.

        val db = Firebase.firestore
        val result: QuerySnapshot = try {
            var query = db.collection("Rezepte").whereArrayContains("Kategorie", suchKriterien.kategorie)
            if (suchKriterien.kalorien.second != 0) {
                query = when (suchKriterien.kalorien.first) {
                    "<" -> query.whereLessThan("Kalorien", suchKriterien.kalorien.second)
                    ">" -> query.whereGreaterThan("Kalorien", suchKriterien.kalorien.second)
                    else -> query.whereEqualTo("Kalorien", suchKriterien.kalorien.second)
                }
            }
            query.get().await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        } as QuerySnapshot

        if (!result.isEmpty) {
            var rezepteList: MutableList<RezeptModel> = result.documents.map {documentToRezeptModel(it) }.toMutableList()

            if (suchKriterien.zutat != "") {
                rezepteList = rezepteList.filter { it.zutaten.containsKey(suchKriterien.zutat) }.toMutableList()
            }
            return when (rezepteList.size) {
                0 -> RezeptModel()
                1 -> rezepteList.first()
                else -> { //Wenn mehrere Gerichte zur Auswahl stehen, wähle das Gericht das am längsten nicht gekocht wurde
                    rezepteList.sortBy { it.zuletztGekochtAm }
                    rezepteList.first()
                }
            }
        }
        return RezeptModel()
    }

    suspend fun updateRezept(documentID: String, rezept: RezeptModel) {
        val db = Firebase.firestore
        val mutableMapRezept = rezeptModelToMap(rezept)

        try {
            db.collection("Rezepte").document(documentID)
                .set(mutableMapRezept).await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    suspend fun insertRezept(rezept: RezeptModel) {
        val db = Firebase.firestore
        val mutableMapRezept = rezeptModelToMap(rezept)

        try {
            db.collection("Rezepte").add(mutableMapRezept).await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    suspend fun deleteRezept(documentID: String) {
        val db = Firebase.firestore
        try {
            db.collection("Rezepte").document(documentID).delete().await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    suspend fun jetztKochen(documentID: String, newValues: Map<String, Any>) {
        val db = Firebase.firestore
        try {
            db.collection("Rezepte").document(documentID).update(newValues).await()
        } catch (e: Exception) {
            print(e.stackTrace)
            print(e.message)
        }
    }

    private fun timeStampToDate(timestamp: Timestamp): Date {
        val date = timestamp.toDate()
        date.hours = date.hours + 2
        return date
    }

    private fun anyMapToStringMap(maps: Map<*, *>): MutableMap<String, String> {
        val newMap: Map<String, String> = emptyMap()
        val newNEWMap = newMap.toMutableMap()
        if (maps.isNotEmpty()) {
            for (map in maps) {
                var tempKey = ""
                var tempValue = ""
                if (!(map.key as String?).isNullOrEmpty()) {
                    tempKey = map.key as String
                }
                if (!(map.value as String?).isNullOrEmpty()) {
                    tempValue = map.value as String
                }
                newNEWMap[tempKey] = tempValue
            }
        }
        return newNEWMap.toMap().toMutableMap()
    }

    private fun rezeptModelToMap(rezept: RezeptModel): Map<String, Any> {
        return hashMapOf(
            "AnzahlGekocht" to rezept.anzahlGekocht,
            "Kalorien" to rezept.kalorien,
            "Kategorie" to rezept.kategorien,
            "Name" to rezept.name,
            "RezeptSchritte" to rezept.schritte,
            "ZuletztGekochtAm" to rezept.zuletztGekochtAm,
            "Zutaten" to rezept.zutaten
        )
    }

    private fun documentToRezeptModel(document: DocumentSnapshot): RezeptModel {
        var kategorien: MutableList<String> =
            emptyList<String>().toMutableList()
        if (document.get("Kategorie") != null) {
            kategorien =
                (document.get("Kategorie") as ArrayList<*>).filterIsInstance<String>() as MutableList<String>
        }
        @Suppress("Unchecked_Cast")
        return RezeptModel(
            rezeptID = document.id,
            rezeptAnzahlGekocht = document.get("AnzahlGekocht") as Long,
            rezeptKalorien = document.get("Kalorien") as Long,
            rezeptKategorien = kategorien,
            rezeptName = document.get("Name") as String,
            rezeptSchritte = (document.get("RezeptSchritte") as? ArrayList<*>)?.filterIsInstance<String>() as MutableList<String>,
            rezeptZuletztGekocht = timeStampToDate(document.get("ZuletztGekochtAm") as Timestamp),
            rezeptZutaten = anyMapToStringMap(document.get("Zutaten") as Map<Any?, Any?>)
        )
    }

    // Zum Debuggen
    fun printRezept(rezept: RezeptModel) {
        println("AnzahlGekocht: " + rezept.anzahlGekocht)
        println("Kalorien: " + rezept.kalorien)
        val tmpKategorien = rezept.kategorien.orEmpty()
        for (kategorie in tmpKategorien) {
            println("Kategorie: $kategorie")
        }
        println("Name: " + rezept.name)
        val tmpSchritte = rezept.schritte.orEmpty()
        for (schritt in tmpSchritte) {
            println("Schritt: $schritt")
        }
        println("ZuletztGekochtAm: " + rezept.zuletztGekochtAm.toString())
        for (zutat in rezept.zutaten) {
            println("Zutat: $zutat")
        }
    }

    // Zum Debuggen
    private fun printSlimRezept(rezept: SlimRezeptModel) {
        println("AnzahlGekocht: " + rezept.anzahlGekocht)
        println("Kalorien: " + rezept.kalorien)
        val tmpKategorien = rezept.kategorien.orEmpty()
        for (kategorie in tmpKategorien) {
            println("Kategorie: $kategorie")
        }
        println("Name: " + rezept.name)
        println("ZuletztGekochtAm: " + rezept.zuletztGekochtAm.toString())
    }

}