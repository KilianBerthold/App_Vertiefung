package models

import java.util.*

class RezeptModel(rezeptName: String, rezeptKategorien: MutableList<String>?, rezeptKalorien: Long, rezeptAnzahlGekocht: Long, rezeptZutaten: Map<String, String>, rezeptSchritte: MutableList<String>?, rezeptZuletztGekocht: Date) {
    var name = rezeptName;
    var kategorien = rezeptKategorien;
    var kalorien = rezeptKalorien;
    var anzahlGekocht = rezeptAnzahlGekocht;
    var zutaten = rezeptZutaten;
    var schritte = rezeptSchritte;
    var zuletztGekochtAm = rezeptZuletztGekocht;
}

class SlimRezeptModel(rezeptName: String, rezeptKategorien: MutableList<String>?, rezeptKalorien: Long, rezeptAnzahlGekocht: Long, rezeptZuletztGekocht: Date) {
    var name = rezeptName;
    var kategorien = rezeptKategorien;
    var kalorien = rezeptKalorien;
    var anzahlGekocht = rezeptAnzahlGekocht;
    var zuletztGekochtAm = rezeptZuletztGekocht;
}