package com.example.app_vertiefung.services

import com.google.common.base.Strings
import com.google.firebase.firestore.FirebaseFirestore
import models.RezeptModel
import models.SlimRezeptModel
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf

class RezeptService {

    suspend fun getAllSlimRezepte(): MutableList<SlimRezeptModel> {
        val db = FirebaseFirestore.getInstance();
        var rezepteList: MutableList<SlimRezeptModel> =
            emptyList<SlimRezeptModel>().toMutableList();

        db.collection("Rezepte")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (document in result.documents) {
                        if (document != null) {
                            //Unchecked casts beim konvertieren der Dokument-Werte zu Array<String> oder Map<String, String>.
                            @Suppress("Unchecked_Cast")
                            println(document.id)
                            var kategorien: MutableList<String>? =
                                emptyList<String>().toMutableList()
                            if (document.get("Kategorie") != null) {
                                kategorien =
                                    (document.get("Kategorie") as? ArrayList<*>)?.filterIsInstance<String>()
                                        ?.toMutableList()
                            }
                            val slimRezept = SlimRezeptModel(
                                rezeptAnzahlGekocht = document.get("AnzahlGekocht") as Long,
                                rezeptKalorien = document.get("Kalorien") as Long,
                                rezeptKategorien = kategorien,
                                rezeptName = document.get("Name") as String,
                                rezeptZuletztGekocht = timeStampToDate(document.get("ZuletztGekochtAm") as com.google.firebase.Timestamp)
                            )
                            rezepteList.add(slimRezept)
                            printSlimRezept(slimRezept)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                print(exception.message)
                print(exception.stackTrace)
            }
        return rezepteList
    }

    //Lädt alle Rezepte aus der Datenbank und speichert sie als Liste
    //TODO: launch {} in der Funktion, die dieses Get aufruft! Coroutine
    suspend fun getAllRezepteWithDetails(): MutableList<RezeptModel> {
        val db = FirebaseFirestore.getInstance();
        var rezepteList: MutableList<RezeptModel> = emptyList<RezeptModel>().toMutableList();

        db.collection("Rezepte")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (document in result.documents) {
                        if (document != null) {
                            //Unchecked casts beim konvertieren der Dokument-Werte zu Array<String> oder Map<String, String>.
                            @Suppress("Unchecked_Cast")
                            println(document.id)
                            var kategorien: MutableList<String>? =
                                emptyList<String>().toMutableList()
                            if (document.get("Kategorie") != null) {
                                kategorien =
                                    (document.get("Kategorie") as? ArrayList<*>)?.filterIsInstance<String>()
                                        ?.toMutableList()
                            }
                            val rezept = RezeptModel(
                                rezeptAnzahlGekocht = document.get("AnzahlGekocht") as Long,
                                rezeptKalorien = document.get("Kalorien") as Long,
                                rezeptKategorien = kategorien,
                                rezeptName = document.get("Name") as String,
                                rezeptSchritte = (document.get("RezeptSchritte") as? ArrayList<*>)?.filterIsInstance<String>()
                                    ?.toMutableList(),
                                rezeptZuletztGekocht = timeStampToDate(document.get("ZuletztGekochtAm") as com.google.firebase.Timestamp),
                                rezeptZutaten = anyMapToStringMap(document.get("Zutaten") as Map<Any?, Any?>)
                            )
                            rezepteList.add(rezept)
                            printRezept(rezept)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                print(exception.message)
                print(exception.stackTrace)
            }
        return rezepteList
    }

    fun timeStampToDate(timestamp: com.google.firebase.Timestamp): Date {
        return timestamp.toDate()
    }

    fun anyMapToStringMap(maps: Map<Any?, Any?>): Map<String, String> {
        var newMap: Map<String, String> = emptyMap()
        var newNEWMap = newMap.toMutableMap()
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
        return newNEWMap.toMap()
    }

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

    fun printSlimRezept(rezept: SlimRezeptModel) {
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