package com.example.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EtudiantDao {

  // Extension : Tri par moyenne (ORDER BY moyenne DESC)
  @Query("SELECT * FROM etudiant ORDER BY moyenne DESC")
  fun getAllByMoyenneDesc(): Flow<List<EtudiantEntity>>

  // Tri par nom alphabétique
  @Query("SELECT * FROM etudiant ORDER BY nom ASC")
  fun getAllByNomAsc(): Flow<List<EtudiantEntity>>

  // Plus récents
  @Query("SELECT * FROM etudiant ORDER BY id DESC")
  fun getAllRecent(): Flow<List<EtudiantEntity>>

  // Recherche
  @Query("""
    SELECT * FROM etudiant 
    WHERE nom LIKE '%' || :query || '%' 
       OR prenom LIKE '%' || :query || '%' 
       OR email LIKE '%' || :query || '%' 
    ORDER BY moyenne DESC
  """)
  fun searchEtudiants(query: String): Flow<List<EtudiantEntity>>

  @Query("SELECT * FROM etudiant WHERE id = :id LIMIT 1")
  fun getById(id: Int): Flow<EtudiantEntity?>

  // Extension : Calcul de la moyenne générale de la classe (AVG(moyenne))
  @Query("SELECT AVG(moyenne) FROM etudiant")
  fun getMoyenneGenerale(): Flow<Double?>

  // Extension : Nombre total d'étudiants (COUNT(*))
  @Query("SELECT COUNT(*) FROM etudiant")
  fun getTotalCount(): Flow<Int>

  // Nombre d'admis (moyenne >= 10)
  @Query("SELECT COUNT(*) FROM etudiant WHERE moyenne >= 10.0")
  fun getAdmisCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(etudiant: EtudiantEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(etudiants: List<EtudiantEntity>)

  @Update
  suspend fun update(etudiant: EtudiantEntity)

  @Delete
  suspend fun delete(etudiant: EtudiantEntity)

  @Query("DELETE FROM etudiant WHERE id = :id")
  suspend fun deleteById(id: Int): Int

  @Query("DELETE FROM etudiant")
  suspend fun clearAll()
}
