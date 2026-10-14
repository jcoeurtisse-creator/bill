package com.example.data

data class Etudiant(
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

data class EtudiantStats(
  val total: Int = 0,
  val moyenneGenerale: Double = 0.0,
  val tauxReussite: Int = 0,
  val admisCount: Int = 0,
  val ajourneCount: Int = 0
)
