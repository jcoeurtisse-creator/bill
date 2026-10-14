package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.room.AppDatabase
import com.example.data.room.EtudiantEntity
import com.example.data.room.EtudiantRepository
import com.example.data.room.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormState(
  val idSelectionne: Int? = null,
  val nomInput: String = "",
  val emailInput: String = "",
  val moyenneInput: String = "",
  val codeInput: String = "",
  val searchQuery: String = "",
  val sortOrder: SortOrder = SortOrder.MOYENNE_DESC,
  val studentToDelete: EtudiantEntity? = null,
  val feedbackMessage: String? = null
)

class EtudiantViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application, viewModelScope)
  val repository = EtudiantRepository(database.etudiantDao())

  private val _formState = MutableStateFlow(FormState())
  val formState: StateFlow<FormState> = _formState.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val etudiantsList: StateFlow<List<EtudiantEntity>> = combine(
    _formState
  ) { stateArray ->
    val state = stateArray[0]
    Pair(state.sortOrder, state.searchQuery)
  }.flatMapLatest { (order, query) ->
    repository.getEtudiants(order, query)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val totalCount: StateFlow<Int> = repository.totalCount.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0
  )

  val moyenneGenerale: StateFlow<Double?> = repository.moyenneGenerale.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null
  )

  val admisCount: StateFlow<Int> = repository.admisCount.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0
  )

  fun onNomChange(value: String) {
    _formState.update { it.copy(nomInput = value) }
  }

  fun onEmailChange(value: String) {
    _formState.update { it.copy(emailInput = value) }
  }

  fun onMoyenneChange(value: String) {
    _formState.update { it.copy(moyenneInput = value) }
  }

  fun onCodeChange(value: String) {
    _formState.update { it.copy(codeInput = value) }
  }

  fun onSearchChange(query: String) {
    _formState.update { it.copy(searchQuery = query) }
  }

  fun setSortOrder(order: SortOrder) {
    _formState.update { it.copy(sortOrder = order) }
  }

  fun selectionnerEtudiant(etudiant: EtudiantEntity) {
    _formState.update {
      it.copy(
        idSelectionne = etudiant.id,
        nomInput = etudiant.nom,
        emailInput = etudiant.email,
        moyenneInput = etudiant.moyenne.toString(),
        codeInput = etudiant.code,
        feedbackMessage = "Étudiant sélectionné : ${etudiant.nom}"
      )
    }
  }

  fun annulerSelection() {
    _formState.update {
      it.copy(
        idSelectionne = null,
        nomInput = "",
        emailInput = "",
        moyenneInput = "",
        codeInput = ""
      )
    }
  }

  // Extension : Boîte de dialogue de confirmation avant suppression
  fun demanderSuppression(etudiant: EtudiantEntity) {
    _formState.update { it.copy(studentToDelete = etudiant) }
  }

  fun annulerSuppression() {
    _formState.update { it.copy(studentToDelete = null) }
  }

  fun confirmerSuppression() {
    val etudiant = _formState.value.studentToDelete ?: return
    viewModelScope.launch {
      repository.deleteById(etudiant.id)
      if (_formState.value.idSelectionne == etudiant.id) {
        annulerSelection()
      }
      _formState.update {
        it.copy(
          studentToDelete = null,
          feedbackMessage = "Étudiant ${etudiant.nom} supprimé"
        )
      }
    }
  }

  fun enregistrer() {
    val nom = _formState.value.nomInput.trim()
    val email = _formState.value.emailInput.trim()
    val moyenneStr = _formState.value.moyenneInput.trim().replace(',', '.')
    val code = _formState.value.codeInput.trim()

    if (nom.isEmpty()) {
      _formState.update { it.copy(feedbackMessage = "Veuillez saisir au moins un nom") }
      return
    }

    val moyenne = moyenneStr.toDoubleOrNull()
    if (moyenne == null || moyenne < 0.0 || moyenne > 20.0) {
      _formState.update { it.copy(feedbackMessage = "La moyenne doit être un nombre valide entre 0 et 20") }
      return
    }

    val idSel = _formState.value.idSelectionne
    viewModelScope.launch {
      if (idSel != null) {
        // Modification avec Room
        repository.update(
          EtudiantEntity(
            id = idSel,
            nom = nom,
            prenom = "",
            email = email,
            moyenne = moyenne,
            code = code
          )
        )
        _formState.update {
          it.copy(
            idSelectionne = null,
            nomInput = "",
            emailInput = "",
            moyenneInput = "",
            codeInput = "",
            feedbackMessage = "Étudiant modifié avec succès"
          )
        }
      } else {
        // Ajout avec Room
        repository.insert(
          EtudiantEntity(
            nom = nom,
            prenom = "",
            email = email,
            moyenne = moyenne,
            code = code
          )
        )
        _formState.update {
          it.copy(
            nomInput = "",
            emailInput = "",
            moyenneInput = "",
            codeInput = "",
            feedbackMessage = "Étudiant enregistré dans Room"
          )
        }
      }
    }
  }

  fun chargerExemples() {
    viewModelScope.launch {
      repository.resetExemples()
      annulerSelection()
      _formState.update { it.copy(feedbackMessage = "Exemples rechargés") }
    }
  }

  fun clearFeedback() {
    _formState.update { it.copy(feedbackMessage = null) }
  }
}
