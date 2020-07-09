package com.example.app_vertiefung.models

import java.io.Serializable

data class SuchKriterienModel (var kategorie: String, var kalorien: Pair<String, Number>, var zutat: String) : Serializable {

}