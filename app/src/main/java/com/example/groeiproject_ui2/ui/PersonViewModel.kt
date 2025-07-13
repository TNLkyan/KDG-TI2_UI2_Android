package com.example.groeiproject_ui2.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.groeiproject_ui2.data.PersonUiState
import com.example.groeiproject_ui2.model.BankAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Log
import com.example.groeiproject_ui2.network.FinancialTrackerApi
import com.example.groeiproject_ui2.data.LocalDatasource.localBankAccounts
import com.example.groeiproject_ui2.data.LocalDatasource.localPeople
import com.example.groeiproject_ui2.data.SettingsManager
import com.example.groeiproject_ui2.model.ClientStatuses
import com.example.groeiproject_ui2.model.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter


sealed class FinancialEvent {
    object Saved: FinancialEvent()
    data class Error(val message: String): FinancialEvent()
}

class PersonViewModel(private val application: Application) : ViewModel() {
    private val settingsManager = SettingsManager(application)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentLanguageCode = MutableStateFlow("nl")
    val currentLanguageCode: StateFlow<String> = _currentLanguageCode.asStateFlow()


    private val _events = MutableSharedFlow<FinancialEvent>()
    val events: SharedFlow<FinancialEvent> = _events


    private val _people = MutableStateFlow<List<Person>>(emptyList())
    val people: StateFlow<List<Person>> = _people

    private val _bankAccounts = MutableStateFlow<List<BankAccount>>(emptyList())
    val bankAccounts: StateFlow<List<BankAccount>> = _bankAccounts


    private val _uiState = MutableStateFlow(PersonUiState())
    val uiState: StateFlow<PersonUiState> = _uiState.asStateFlow()

    private var currentIndex by mutableStateOf(0)


    companion object {
        private const val TAG = "PersonViewModel"

        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PersonViewModel::class.java)) {
                        return PersonViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }

    var newNaam by mutableStateOf("")
    var newLeeftijd by mutableStateOf("")
    var newGeboortedatum by mutableStateOf("")
    var newNationaliteit by mutableStateOf("")
    var newLengte by mutableStateOf("")
    var newPremiumKlant by mutableStateOf(false)
    var klantStatus: ClientStatuses by mutableStateOf(ClientStatuses.standard)

    init {
        fetchPeople()
        fetchBankAccounts()
        viewModelScope.launch {
            settingsManager.isDarkModeEnabled.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
        viewModelScope.launch {
            settingsManager.languageCode.collect { langCode ->
                _currentLanguageCode.value = langCode
            }
        }
    }



    private fun fetchPeople() {
        viewModelScope.launch {
            try {
                val tempList = FinancialTrackerApi.api.getPeople()

                _people.value = tempList
                _uiState.update { currentState ->
                    currentState.copy(
                        people = tempList,
                        inUpdateMode = false,
                        IsAddingPerson = false,
                        isExtraOptionsExpended = false
                    )
                }

                if (tempList.isNotEmpty() && currentIndex >= tempList.size) {
                    currentIndex = 0
                }

                Log.d(TAG, "${tempList.size} people fetched")
            } catch (e: Exception) {
                Log.e(TAG, "Error while fetching people", e)
                _events.emit(FinancialEvent.Error("Error while fetching people: ${e.message}"))

            }
        }
    }
    private fun fetchBankAccounts() {
        viewModelScope.launch {
            try {
                _bankAccounts.value = FinancialTrackerApi.api.getBankAccounts()
                Log.d(TAG, "${_bankAccounts.value.size} bankaccounts fetched")
            } catch (e: Exception) {
                Log.e(TAG, "Error while fetching bankaccounts", e)
                _events.emit(FinancialEvent.Error("Error while fetching people: ${e.message}"))
            }
        }
    }



    fun collectBankRek(personId: Int): List<BankAccount> {
        return bankAccounts.value.filter { it.persoon_id == personId }.toMutableList()
    }

    fun selectPerson(person: Person) {
        currentIndex = _uiState.value.people.indexOf(person)

        _uiState.update { currentState ->
            currentState.copy(
                person = person,
                bankAccs = collectBankRek(person.id),
                inUpdateMode = false,
                IsAddingPerson = false,
                isExtraOptionsExpended = false
            )
        }
        Log.d(TAG, "selected person with id: ${person.id}, name: ${person.naam}")
    }


    fun nextPerson() {
        currentIndex = (currentIndex + 1) % _uiState.value.people.size
        var nextPerson = _uiState.value.people[currentIndex]

        Log.d(TAG, "updating to next person")

        _uiState.update { currentState ->
            currentState.copy(
                person = nextPerson,
                bankAccs = collectBankRek(nextPerson.id),
                inUpdateMode = false
            )
        }
    }

    fun previousPerson() {
        currentIndex = (currentIndex - 1 + _uiState.value.people.size) % _uiState.value.people.size
        var prevPerson = _uiState.value.people[currentIndex]

        Log.d(TAG, "updating to previous person")

        _uiState.update { currentState ->
            currentState.copy(
                person = prevPerson,
                bankAccs = collectBankRek(prevPerson.id),
                inUpdateMode = false
            )
        }
    }

    suspend fun loadImage(fotoUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(fotoUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val inputStream = BufferedInputStream(connection.inputStream)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.d(TAG, "Error loading image: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.d(TAG, "Unexpected error loading image: ${e.message}", e)
            null
        } finally {
            connection?.disconnect()
        }
    }

    fun toggleExpandBankAccounts() {
        if (!_uiState.value.isBankAccountsExpanded)
            _uiState.update { currentState ->
                currentState.copy(
                    isBankAccountsExpanded = true
                )
            }
        else
            _uiState.update { currentState ->
                currentState.copy(
                    isBankAccountsExpanded = false
                )
            }
    }

    fun toggleEditMode() {
        if (!_uiState.value.inUpdateMode)
            _uiState.update { currentState ->
                Log.d(TAG, "entering editmode")
                currentState.copy(
                    inUpdateMode = true
                )
            }
        else
            _uiState.update { currentState ->
                Log.d(TAG, "exiting editmode")
                currentState.copy(
                    inUpdateMode = false
                )
            }
    }

    fun updatePersonLeeftijd(newAge: Int) {
        var currentperson = localPeople[currentIndex]
        var updatedPerson = currentperson.copy(leeftijd = newAge)

        localPeople[currentIndex] = updatedPerson

        _uiState.update { currentState ->
            Log.d(TAG, "updating current person's age")
            currentState.copy(
                person = updatedPerson
            )
        }
    }

    fun toggleExtraOptions() {
        if (!_uiState.value.isExtraOptionsExpended)
            _uiState.update { currentState ->
                Log.d(TAG, "switching to extra options")
                currentState.copy(
                    isExtraOptionsExpended = true
                )
            }
        else
            _uiState.update { currentState ->
                Log.d(TAG, "switching to \"normal\" options")
                currentState.copy(
                    isExtraOptionsExpended = false
                )
            }
    }



    fun saveNewPerson() {
        val newPerson = Person(
            id = localPeople.maxOf { it.id } + 1,
            naam = newNaam,
            leeftijd = newLeeftijd.toIntOrNull() ?: 0,
            geboortedatum = try {
                LocalDate.parse(newGeboortedatum, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            } catch (e: Exception) {
                LocalDate.now()
            },
            nationaliteit = newNationaliteit,
            lengte = newLengte.toDoubleOrNull() ?: 0.0,
            premiumKlant = newPremiumKlant,
            klantStatus = klantStatus,
            fotoUrl = "default"
        )

        viewModelScope.launch {
            try {
                FinancialTrackerApi.api.addPerson(newPerson)
                Log.d(TAG, "Successfully added person")
                _events.emit(FinancialEvent.Saved)
                fetchPeople()
            } catch (e: Exception){
                Log.e(TAG, "Something went wrong trying to add a person: ${e.message}")
                _events.emit(FinancialEvent.Error("Couldn't delete person: ${e.message}"))
            }
        }

        currentIndex = _uiState.value.people.indexOf(newPerson)
        resetForm()

        selectPerson(newPerson)
    }

    fun resetForm() {
        newNaam = ""
        newLeeftijd = ""
        newGeboortedatum = ""
        newNationaliteit = ""
        newLengte = ""
        newPremiumKlant = false
        klantStatus = ClientStatuses.standard
    }

    fun deletePerson() {
        if (_uiState.value.people.size > 1){
            Log.d(TAG, "deleting current selected person")

            viewModelScope.launch {
                try {
                    FinancialTrackerApi.api.deletePerson(_uiState.value.people[currentIndex].id)
                    Log.d(TAG, "Successfully deleted person")
                    _events.emit(FinancialEvent.Saved)
                    fetchPeople()

                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting person: ", e)
                    _events.emit(FinancialEvent.Error("Couldn't delete person: ${e.message}"))
                }
            }
            previousPerson()
        }
        else {
            Log.d(TAG, "atleast 1 person has to be in the peoplelist")
        }
    }



    fun toggleDarkMode() {
        viewModelScope.launch {
            val newMode = !_isDarkMode.value
            settingsManager.setDarkModeEnabled(newMode)
            _isDarkMode.value = newMode
        }
        Log.d(TAG, "toggling dark / light mode")
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            settingsManager.setLanguageCode(languageCode)
            _currentLanguageCode.value = languageCode
        }
        Log.d(TAG, "Changing language to ${languageCode}")
    }
}