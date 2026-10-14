package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [EtudiantEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

  abstract fun etudiantDao(): EtudiantDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "etudiant_room.db"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch {
            populateInitialData(database.etudiantDao())
          }
        }
      }

      suspend fun populateInitialData(dao: EtudiantDao) {
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
  }
}
