package models

import java.io.Serializable

data class KategorieModel(var id: String, var name: String, var beschreibung: String) :
    Serializable {
    constructor() : this("", "", "")

}