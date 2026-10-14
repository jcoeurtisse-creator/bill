package com.example.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.room.EtudiantEntity
import com.example.data.room.SortOrder
import com.example.ui.components.MinimalistInputField
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun EtudiantScreen(
  viewModel: EtudiantViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val formState by viewModel.formState.collectAsState()
  val etudiants by viewModel.etudiantsList.collectAsState()
  val totalCount by viewModel.totalCount.collectAsState()
  val moyenneGen by viewModel.moyenneGenerale.collectAsState()
  val admisCount by viewModel.admisCount.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }
  var maskPassword by remember { mutableStateOf(true) }

  LaunchedEffect(formState.feedbackMessage) {
    formState.feedbackMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearFeedback()
    }
  }

  // Extension 1 : Boîte de dialogue AlertDialog de confirmation avant suppression
  if (formState.studentToDelete != null) {
    val etudiant = formState.studentToDelete!!
    AlertDialog(
      onDismissRequest = { viewModel.annulerSuppression() },
      title = {
        Text(
          text = "Confirmation de suppression",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      },
      text = {
        Text(
          text = "Voulez-vous vraiment supprimer l'étudiant ${etudiant.nomComplet} ? Cette action est irréversible.",
          fontSize = 14.sp,
          color = Slate600
        )
      },
      confirmButton = {
        Button(
          onClick = { viewModel.confirmerSuppression() },
          colors = ButtonDefaults.buttonColors(containerColor = RoseError),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("btn_confirm_delete")
        ) {
          Text("Supprimer", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { viewModel.annulerSuppression() },
          modifier = Modifier.testTag("btn_cancel_delete")
        ) {
          Text("Annuler", color = Slate600)
        }
      },
      shape = RoundedCornerShape(12.dp),
      containerColor = Color.White
    )
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = Color.White
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 24.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // En-tête épuré
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Slate900),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "GESTION ÉTUDIANTS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = Slate900
              )
              Text(
                text = "Room Database (Kotlin + Coroutines Flow)",
                fontSize = 11.sp,
                color = Slate500
              )
            }
          }

          IconButton(
            onClick = { viewModel.chargerExemples() },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Recharger exemples",
              tint = Slate600
            )
          }
        }
      }

      // Extension 3 & 4 : Stats SQL Room (COUNT(*) et AVG(moyenne))
      if (totalCount > 0) {
        item {
          val moyenneFormattee = if (moyenneGen != null) {
            String.format("%.1f", moyenneGen)
          } else {
            "0.0"
          }
          val taux = if (totalCount > 0) ((admisCount.toDouble() / totalCount) * 100).toInt() else 0

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Slate50)
              .border(1.dp, Slate200, RoundedCornerShape(10.dp))
              .padding(vertical = 10.dp, horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            StatItem(label = "COUNT(*)", value = "$totalCount")
            StatDivider()
            StatItem(label = "AVG(moyenne)", value = "$moyenneFormattee/20")
            StatDivider()
            StatItem(label = "TAUX RÉUSSITE", value = "$taux%")
          }
        }
      }

      // Section formulaire - Design minimaliste de l'image de référence
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Champ 1: Nom complet (ex: RACHEL JANSEN)
          MinimalistInputField(
            value = formState.nomInput,
            onValueChange = { viewModel.onNomChange(it) },
            placeholder = "RACHEL JANSEN",
            testTag = "input_nom"
          )

          // Champ 2: Email (ex: RACHEL_J@EMAIL.COM)
          MinimalistInputField(
            value = formState.emailInput,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "RACHEL_J@EMAIL.COM",
            keyboardType = KeyboardType.Email,
            testTag = "input_email"
          )

          // Champ 3: Moyenne
          MinimalistInputField(
            value = formState.moyenneInput,
            onValueChange = { viewModel.onMoyenneChange(it) },
            placeholder = "MOYENNE (EX: 15.5)",
            keyboardType = KeyboardType.Decimal,
            testTag = "input_moyenne"
          )

          // Champ 4: Code secret ou matricule (ex: ••••••••)
          MinimalistInputField(
            value = formState.codeInput,
            onValueChange = { viewModel.onCodeChange(it) },
            placeholder = "••••••••",
            isPassword = maskPassword,
            keyboardType = KeyboardType.Password,
            trailingIcon = {
              IconButton(onClick = { maskPassword = !maskPassword }) {
                Icon(
                  imageVector = if (maskPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = "Afficher/Masquer",
                  tint = Slate400,
                  modifier = Modifier.size(20.dp)
                )
              }
            },
            testTag = "input_code"
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Bouton d'action principal minimaliste
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { viewModel.enregistrer() },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("btn_enregistrer"),
              colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = if (formState.idSelectionne != null) "MODIFIER" else "ENREGISTRER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }

            if (formState.idSelectionne != null) {
              OutlinedButton(
                onClick = { viewModel.annulerSelection() },
                modifier = Modifier
                  .height(48.dp)
                  .testTag("btn_annuler"),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate300)
              ) {
                Text(
                  text = "ANNULER",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Slate600
                )
              }
            }
          }
        }
      }

      // Extension 2 : Sélecteur de Tri (ORDER BY moyenne DESC, NOM ASC, RECENT)
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "TRIER PAR :",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                letterSpacing = 0.8.sp
              )
            }

            Text(
              text = "${etudiants.size} étudiants",
              fontSize = 12.sp,
              color = Slate400
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            SortFilterChip(
              label = "Moyenne ↓",
              selected = formState.sortOrder == SortOrder.MOYENNE_DESC,
              onClick = { viewModel.setSortOrder(SortOrder.MOYENNE_DESC) }
            )
            SortFilterChip(
              label = "Nom (A-Z)",
              selected = formState.sortOrder == SortOrder.NOM_ASC,
              onClick = { viewModel.setSortOrder(SortOrder.NOM_ASC) }
            )
            SortFilterChip(
              label = "Récents",
              selected = formState.sortOrder == SortOrder.RECENT,
              onClick = { viewModel.setSortOrder(SortOrder.RECENT) }
            )
          }
        }
      }

      // Filtre de recherche
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Slate50)
            .border(1.dp, Slate200, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = Slate400,
              modifier = Modifier.size(16.dp)
            )
            MinimalistInputField(
              value = formState.searchQuery,
              onValueChange = { viewModel.onSearchChange(it) },
              placeholder = "Rechercher par nom ou email...",
              modifier = Modifier.weight(1f),
              testTag = "input_search"
            )
            if (formState.searchQuery.isNotEmpty()) {
              IconButton(
                onClick = { viewModel.onSearchChange("") },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Effacer",
                  tint = Slate400,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }

      // Liste des étudiants
      if (etudiants.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Aucun étudiant trouvé",
              fontSize = 14.sp,
              color = Slate400,
              fontWeight = FontWeight.Medium
            )
          }
        }
      } else {
        items(
          items = etudiants,
          key = { it.id }
        ) { etudiant ->
          // Extension 5 : Clic sur l'élément ouvre DetailActivity
          EtudiantRoomCard(
            etudiant = etudiant,
            isSelected = formState.idSelectionne == etudiant.id,
            onCardClick = {
              context.startActivity(DetailActivity.newIntent(context, etudiant.id))
            },
            onEditClick = { viewModel.selectionnerEtudiant(etudiant) },
            onDeleteClick = { viewModel.demanderSuppression(etudiant) }
          )
        }
      }
    }
  }
}

@Composable
fun SortFilterChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    label = {
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
      )
    },
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = Slate900,
      selectedLabelColor = Color.White,
      containerColor = Slate50,
      labelColor = Slate600
    ),
    border = FilterChipDefaults.filterChipBorder(
      enabled = true,
      selected = selected,
      borderColor = if (selected) Slate900 else Slate200
    ),
    shape = RoundedCornerShape(8.dp)
  )
}

@Composable
fun EtudiantRoomCard(
  etudiant: EtudiantEntity,
  isSelected: Boolean,
  onCardClick: () -> Unit,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  val borderColor = if (isSelected) BluePrimary else Slate200
  val bgColor = if (isSelected) Color(0xFFF0F7FF) else Color.White

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .border(1.2.dp, borderColor, RoundedCornerShape(10.dp))
      .clickable { onCardClick() }
      .testTag("etudiant_card_${etudiant.id}"),
    color = bgColor
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = etudiant.nomComplet.uppercase(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900,
            letterSpacing = 0.5.sp
          )
          if (isSelected) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(BluePrimary)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "ÉDITION",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }

        if (etudiant.email.isNotEmpty()) {
          Text(
            text = etudiant.email.uppercase(),
            fontSize = 11.sp,
            color = Slate500,
            letterSpacing = 0.3.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Text(
            text = "Moyenne : ${etudiant.moyenne}/20",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate700
          )

          val badgeBg = if (etudiant.isAdmis) EmeraldSoft else RoseSoft
          val badgeColor = if (etudiant.isAdmis) EmeraldSuccess else RoseError
          val statusText = if (etudiant.isAdmis) "Admis (${etudiant.mention})" else "Ajourné"

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(badgeBg)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = statusText,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = badgeColor
            )
          }
        }
      }

      // Actions rapides : Éditer, Supprimer (avec AlertDialog), et Flèche détail
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        IconButton(
          onClick = onEditClick,
          modifier = Modifier
            .size(32.dp)
            .testTag("btn_editer_${etudiant.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Éditer",
            tint = Slate500,
            modifier = Modifier.size(16.dp)
          )
        }

        IconButton(
          onClick = onDeleteClick,
          modifier = Modifier
            .size(32.dp)
            .testTag("btn_supprimer_${etudiant.id}")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Supprimer",
            tint = RoseError,
            modifier = Modifier.size(16.dp)
          )
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Voir fiche",
          tint = Slate300,
          modifier = Modifier
            .size(16.dp)
            .padding(start = 2.dp)
        )
      }
    }
  }
}

@Composable
fun StatItem(label: String, value: String) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      color = Slate400,
      letterSpacing = 0.8.sp
    )
    Text(
      text = value,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = Slate800
    )
  }
}

@Composable
fun StatDivider() {
  Box(
    modifier = Modifier
      .height(24.dp)
      .width(1.dp)
      .background(Slate200)
  )
}

