package com.example.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
  SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  companion object {
    private const val DATABASE_NAME = "Etudiants.db"
    private const val DATABASE_VERSION = 2
    private const val TABLE_NAME = "etudiant"

    private const val COL_ID = "id"
    private const val COL_NOM = "nom"
    private const val COL_PRENOM = "prenom"
    private const val COL_EMAIL = "email"
    private const val COL_MOYENNE = "moyenne"
    private const val COL_CODE = "code"
  }

  override fun onCreate(db: SQLiteDatabase) {
    val createTable = """
      CREATE TABLE $TABLE_NAME (
        $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_NOM TEXT NOT NULL,
        $COL_PRENOM TEXT,
        $COL_EMAIL TEXT,
        $COL_MOYENNE REAL NOT NULL,
        $COL_CODE TEXT
      )
    """.trimIndent()
    db.execSQL(createTable)

    // Initial sample student matching user's image: Rachel Jansen
    val values = ContentValues().apply {
      put(COL_NOM, "JANSEN")
      put(COL_PRENOM, "Rachel")
      put(COL_EMAIL, "RACHEL_J@EMAIL.COM")
      put(COL_MOYENNE, 16.5)
      put(COL_CODE, "SEC-8821")
    }
    db.insert(TABLE_NAME, null, values)

    val sample2 = ContentValues().apply {
      put(COL_NOM, "ADJOVI")
      put(COL_PRENOM, "Jean")
      put(COL_EMAIL, "JEAN.ADJOVI@CAMPUS.EDU")
      put(COL_MOYENNE, 14.5)
      put(COL_CODE, "SEC-1420")
    }
    db.insert(TABLE_NAME, null, sample2)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    onCreate(db)
  }

  // CREATE (Ajouter)
  fun ajouter(
    nom: String,
    prenom: String = "",
    email: String = "",
    moyenne: Double,
    code: String = ""
  ): Long {
    val db = writableDatabase
    val values = ContentValues().apply {
      put(COL_NOM, nom.trim())
      put(COL_PRENOM, prenom.trim())
      put(COL_EMAIL, email.trim())
      put(COL_MOYENNE, moyenne)
      put(COL_CODE, code.trim())
    }
    return db.insert(TABLE_NAME, null, values)
  }

  // READ (Lire la liste)
  fun getEtudiants(searchQuery: String = ""): List<Etudiant> {
    val liste = mutableListOf<Etudiant>()
    val db = readableDatabase

    val query = if (searchQuery.isBlank()) {
      "SELECT * FROM $TABLE_NAME ORDER BY $COL_ID DESC"
    } else {
      "SELECT * FROM $TABLE_NAME WHERE $COL_NOM LIKE ? OR $COL_PRENOM LIKE ? OR $COL_EMAIL LIKE ? ORDER BY $COL_ID DESC"
    }

    val args = if (searchQuery.isBlank()) null else {
      val likeArg = "%${searchQuery.trim()}%"
      arrayOf(likeArg, likeArg, likeArg)
    }

    val cursor = db.rawQuery(query, args)
    cursor.use {
      val idCol = it.getColumnIndex(COL_ID)
      val nomCol = it.getColumnIndex(COL_NOM)
      val prenomCol = it.getColumnIndex(COL_PRENOM)
      val emailCol = it.getColumnIndex(COL_EMAIL)
      val moyCol = it.getColumnIndex(COL_MOYENNE)
      val codeCol = it.getColumnIndex(COL_CODE)

      while (it.moveToNext()) {
        val id = if (idCol >= 0) it.getInt(idCol) else 0
        val nom = if (nomCol >= 0) it.getString(nomCol).orEmpty() else ""
        val prenom = if (prenomCol >= 0) it.getString(prenomCol).orEmpty() else ""
        val email = if (emailCol >= 0) it.getString(emailCol).orEmpty() else ""
        val moyenne = if (moyCol >= 0) it.getDouble(moyCol) else 0.0
        val code = if (codeCol >= 0) it.getString(codeCol).orEmpty() else ""

        liste.add(
          Etudiant(
            id = id,
            nom = nom,
            prenom = prenom,
            email = email,
            moyenne = moyenne,
            code = code
          )
        )
      }
    }
    return liste
  }

  // UPDATE (Modifier)
  fun modifier(
    id: Int,
    nom: String,
    prenom: String = "",
    email: String = "",
    moyenne: Double,
    code: String = ""
  ): Int {
    val db = writableDatabase
    val values = ContentValues().apply {
      put(COL_NOM, nom.trim())
      put(COL_PRENOM, prenom.trim())
      put(COL_EMAIL, email.trim())
      put(COL_MOYENNE, moyenne)
      put(COL_CODE, code.trim())
    }
    return db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(id.toString()))
  }

  // DELETE (Supprimer)
  fun supprimer(id: Int): Int {
    val db = writableDatabase
    return db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
  }

  // STATS (Calculs utiles)
  fun getStats(): EtudiantStats {
    val etudiants = getEtudiants()
    if (etudiants.isEmpty()) return EtudiantStats()

    val total = etudiants.size
    val somme = etudiants.sumOf { it.moyenne }
    val moyenneGen = Math.round((somme / total) * 10.0) / 10.0
    val admis = etudiants.count { it.isAdmis }
    val ajournes = total - admis
    val taux = ((admis.toDouble() / total) * 100).toInt()

    return EtudiantStats(
      total = total,
      moyenneGenerale = moyenneGen,
      tauxReussite = taux,
      admisCount = admis,
      ajourneCount = ajournes
    )
  }

  // RESET / REPOPULATE EXAMPLES
  fun chargerExemples() {
    val db = writableDatabase
    db.execSQL("DELETE FROM $TABLE_NAME")
    val samples = listOf(
      arrayOf("JANSEN", "Rachel", "RACHEL_J@EMAIL.COM", "16.5", "••••••••"),
      arrayOf("ADJOVI", "Jean", "JEAN.ADJOVI@EMAIL.COM", "14.5", "••••••••"),
      arrayOf("AHOUANSOU", "Paul", "PAUL.AH@EMAIL.COM", "9.5", "••••••••"),
      arrayOf("SOGLO", "Marie", "MARIE.SOGLO@EMAIL.COM", "17.0", "••••••••")
    )
    for (s in samples) {
      val cv = ContentValues().apply {
        put(COL_NOM, s[0])
        put(COL_PRENOM, s[1])
        put(COL_EMAIL, s[2])
        put(COL_MOYENNE, s[3].toDouble())
        put(COL_CODE, s[4])
      }
      db.insert(TABLE_NAME, null, cv)
    }
  }
}
