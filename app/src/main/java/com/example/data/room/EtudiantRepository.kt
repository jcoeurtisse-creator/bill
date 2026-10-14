package com.example.data.room

import kotlinx.coroutines.flow.Flow

enum class SortOrder {
  MOYENNE_DESC,
  NOM_ASC,
  RECENT
}

class EtudiantRepository(private val dao: EtudiantDao) {

  fun getEtudiants(sortOrder: SortOrder, searchQuery: String = ""): Flow<List<EtudiantEntity>> {
    return if (searchQuery.isNotBlank()) {
      dao.searchEtudiants(searchQuery.trim())
    } else {
      when (sortOrder) {
        SortOrder.MOYENNE_DESC -> dao.getAllByMoyenneDesc()
        SortOrder.NOM_ASC -> dao.getAllByNomAsc()
        SortOrder.RECENT -> dao.getAllRecent()
      }
    }
  }

  fun getEtudiantById(id: Int): Flow<EtudiantEntity?> = dao.getById(id)

  val moyenneGenerale: Flow<Double?> = dao.getMoyenneGenerale()

  val totalCount: Flow<Int> = dao.getTotalCount()

  val admisCount: Flow<Int> = dao.getAdmisCount()

  suspend fun insert(etudiant: EtudiantEntity): Long = dao.insert(etudiant)

  suspend fun update(etudiant: EtudiantEntity) = dao.update(etudiant)

  suspend fun delete(etudiant: EtudiantEntity) = dao.delete(etudiant)

  suspend fun deleteById(id: Int): Int = dao.deleteById(id)

  suspend fun resetExemples() {
    dao.clearAll()
    val samples = listOf(
      EtudiantEntity(
        nom = "JANSEN",
        prenom = "Rachel",
        email = "RACHEL_J@EMAIL.COM",
        moyenne = 16.5,
        code = "SEC-8821"
      ),
      EtudiantEntity(
        nom = "ADJOVI",
        prenom = "Jean",
        email = "JEAN.ADJOVI@EMAIL.COM",
        moyenne = 14.5,
        code = "SEC-1420"
      ),
      EtudiantEntity(
        nom = "SOGLO",
        prenom = "Marie",
        email = "MARIE.SOGLO@EMAIL.COM",
        moyenne = 17.0,
        code = "SEC-9931"
      ),
      EtudiantEntity(
        nom = "AHOUANSOU",
        prenom = "Paul",
        email = "PAUL.AH@EMAIL.COM",
        moyenne = 9.5,
        code = "SEC-5510"
      )
    )
    dao.insertAll(samples)
  }
}
