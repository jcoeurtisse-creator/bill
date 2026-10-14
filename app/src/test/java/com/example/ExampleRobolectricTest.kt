package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.room.AppDatabase
import com.example.data.room.EtudiantDao
import com.example.data.room.EtudiantEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: AppDatabase
  private lateinit var dao: EtudiantDao

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = db.etudiantDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Étudiants", appName)
  }

  @Test
  fun `test room sorting by moyenne DESC`() = runBlocking {
    val e1 = EtudiantEntity(nom = "BAS", moyenne = 10.0)
    val e2 = EtudiantEntity(nom = "HAUT", moyenne = 18.5)
    val e3 = EtudiantEntity(nom = "MOYEN", moyenne = 14.0)

    dao.insertAll(listOf(e1, e2, e3))

    val sorted = dao.getAllByMoyenneDesc().first()
    assertEquals(3, sorted.size)
    assertEquals("HAUT", sorted[0].nom)
    assertEquals("MOYEN", sorted[1].nom)
    assertEquals("BAS", sorted[2].nom)
  }

  @Test
  fun `test room count and avg queries`() = runBlocking {
    val e1 = EtudiantEntity(nom = "A", moyenne = 10.0)
    val e2 = EtudiantEntity(nom = "B", moyenne = 20.0)

    dao.insertAll(listOf(e1, e2))

    val count = dao.getTotalCount().first()
    assertEquals(2, count)

    val avg = dao.getMoyenneGenerale().first()
    assertNotNull(avg)
    assertEquals(15.0, avg!!, 0.01)

    val admis = dao.getAdmisCount().first()
    assertEquals(2, admis)
  }

  @Test
  fun `test room delete and update`() = runBlocking {
    val id = dao.insert(EtudiantEntity(nom = "INITIAL", moyenne = 12.0))
    assertTrue(id > 0)

    dao.update(EtudiantEntity(id = id.toInt(), nom = "MODIFIE", moyenne = 16.0))
    val retrieved = dao.getById(id.toInt()).first()
    assertNotNull(retrieved)
    assertEquals("MODIFIE", retrieved?.nom)
    assertEquals(16.0, retrieved?.moyenne ?: 0.0, 0.01)

    val deletedRows = dao.deleteById(id.toInt())
    assertEquals(1, deletedRows)

    val countAfter = dao.getTotalCount().first()
    assertEquals(0, countAfter)
  }
}
