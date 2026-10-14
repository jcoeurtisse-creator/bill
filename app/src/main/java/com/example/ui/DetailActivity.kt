package com.example.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.room.AppDatabase
import com.example.data.room.EtudiantEntity
import com.example.data.room.EtudiantRepository
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoseError
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.launch

class DetailActivity : ComponentActivity() {

  companion object {
    const val EXTRA_ETUDIANT_ID = "extra_etudiant_id"

    fun newIntent(context: Context, etudiantId: Int): Intent {
      return Intent(context, DetailActivity::class.java).apply {
        putExtra(EXTRA_ETUDIANT_ID, etudiantId)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val etudiantId = intent.getIntExtra(EXTRA_ETUDIANT_ID, -1)
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = EtudiantRepository(database.etudiantDao())

    setContent {
      MyApplicationTheme(darkTheme = false) {
        DetailScreen(
          etudiantId = etudiantId,
          repository = repository,
          onBack = { finish() },
          onDeleted = { finish() }
        )
      }
    }
  }
}

@Composable
fun DetailScreen(
  etudiantId: Int,
  repository: EtudiantRepository,
  onBack: () -> Unit,
  onDeleted: () -> Unit
) {
  val etudiant by repository.getEtudiantById(etudiantId).collectAsState(initial = null)
  var showDeleteDialog by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  // Boîte de dialogue de confirmation avant suppression
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = {
        Text(
          text = "Confirmation de suppression",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      },
      text = {
        Text(
          text = "Voulez-vous vraiment supprimer cet étudiant (${etudiant?.nomComplet}) ? Cette action est irréversible.",
          fontSize = 14.sp,
          color = Slate600
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteDialog = false
            coroutineScope.launch {
              repository.deleteById(etudiantId)
              onDeleted()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoseError)
        ) {
          Text("Supprimer", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Annuler", color = Slate600)
        }
      },
      shape = RoundedCornerShape(12.dp),
      containerColor = Color.White
    )
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding(),
    containerColor = Color.White
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 24.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Top Bar épurée
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("btn_detail_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Retour",
            tint = Slate800
          )
        }

        Text(
          text = "FICHE ÉTUDIANT",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp,
          color = Slate800
        )

        IconButton(
          onClick = { showDeleteDialog = true },
          modifier = Modifier.testTag("btn_detail_delete")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Supprimer",
            tint = RoseError
          )
        }
      }

      if (etudiant == null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "Étudiant introuvable", color = Slate400)
        }
      } else {
        val item = etudiant!!

        // Carte Identité - Style minimaliste raffiné
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.2.dp, Slate200, RoundedCornerShape(12.dp)),
          color = Color.White
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Slate900),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = item.nom.take(1).uppercase(),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Text(
              text = item.nomComplet.uppercase(),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = Slate900
            )

            if (item.email.isNotEmpty()) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Email,
                  contentDescription = null,
                  tint = Slate400,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = item.email.uppercase(),
                  fontSize = 12.sp,
                  color = Slate600
                )
              }
            }
          }
        }

        // Carte Résultats Scolaires
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.2.dp, Slate200, RoundedCornerShape(12.dp)),
          color = Color.White
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "RÉSULTATS ACADÉMIQUES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp
              )

              val badgeBg = if (item.isAdmis) EmeraldSoft else RoseSoft
              val badgeColor = if (item.isAdmis) EmeraldSuccess else RoseError
              val statusText = if (item.isAdmis) "ADMIS" else "AJOURNÉ"

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(badgeBg)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = statusText,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = badgeColor
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              Column {
                Text(
                  text = "Moyenne générale",
                  fontSize = 12.sp,
                  color = Slate500
                )
                Text(
                  text = "${item.moyenne} / 20",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate900
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Mention",
                  fontSize = 12.sp,
                  color = Slate500
                )
                Text(
                  text = item.mention,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (item.isAdmis) EmeraldSuccess else RoseError
                )
              }
            }

            // Jauge de progression de la note (0 à 20)
            val progress = (item.moyenne / 20.0).toFloat().coerceIn(0f, 1f)
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = if (item.isAdmis) BluePrimary else RoseError,
              trackColor = Slate100
            )
          }
        }

        // Code / Matricule
        if (item.code.isNotEmpty()) {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.2.dp, Slate200, RoundedCornerShape(12.dp)),
            color = Color.White
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Key,
                  contentDescription = null,
                  tint = Slate400,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "Code / Matricule",
                  fontSize = 13.sp,
                  color = Slate600
                )
              }

              Text(
                text = item.code,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
              )
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bouton Retour
        Button(
          onClick = onBack,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Slate900),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "RETOUR À LA LISTE",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
      }
    }
  }
}
