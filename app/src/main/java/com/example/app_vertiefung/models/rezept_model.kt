package com.example.app_vertiefung.models

import java.io.Serializable
import java.util.*

class RezeptModel(
    rezeptID: String,
    rezeptName: String,
    rezeptKategorien: MutableList<String>,
    rezeptKalorien: Long,
    rezeptAnzahlGekocht: Long,
    rezeptZutaten: MutableMap<String, String>,
    rezeptSchritte: MutableList<String>,
    rezeptZuletztGekocht: Date
) : Serializable {
    constructor() : this(
        "",
        "",
        emptyList<String>().toMutableList(),
        0,
        0,
        emptyMap<String, String>().toMutableMap(),
        emptyList<String>().toMutableList(),
        Date()
    )

    var id = rezeptID
    var name = rezeptName
    var kategorien = rezeptKategorien
    var kalorien = rezeptKalorien
    var anzahlGekocht = rezeptAnzahlGekocht
    var zutaten = rezeptZutaten
    var schritte = rezeptSchritte
    var zuletztGekochtAm = rezeptZuletztGekocht
}

class SlimRezeptModel(
    var id: String,
    var name: String,
    var kategorien: MutableList<String>,
    var kalorien: Long,
    var anzahlGekocht: Long,
    var zuletztGekochtAm: Date
)

class AnzeigeRezeptModel(
    var name: String,
    var kategorien: String,
    var kalorien: String,
    var anzahlGekocht: String,
    var zutaten: String,
    var schritte: String,
    var zuletztGekochtAm: String
)