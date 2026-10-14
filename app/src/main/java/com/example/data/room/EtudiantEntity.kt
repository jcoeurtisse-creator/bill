package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "etudiant")
data class EtudiantEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val nom: String,
  val prenom: String = "",
  val email: String = "",
  val moyenne: Double,
  val code: String = ""
) {
  val nomComplet: String
    get() = if (prenom.isBlank()) nom else "$prenom $nom".trim()

  val isAdmis: Boolean
    get() = moyenne >= 10.0

  val mention: String
    get() = when {
      moyenne >= 16.0 -> "Très Bien"
      moyenne >= 14.0 -> "Bien"
      moyenne >= 12.0 -> "Assez Bien"
      moyenne >= 10.0 -> "Passable"
      else -> "Ajourné"
    }
}
