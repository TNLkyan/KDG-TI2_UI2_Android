package com.example.groeiproject_ui2.data

import com.example.groeiproject_ui2.model.BankAccount
import com.example.groeiproject_ui2.model.ClientStatuses
import com.example.groeiproject_ui2.model.Person
import java.time.LocalDate

data class PersonUiState (
    val people: List<Person> = emptyList(),
    val person: Person = Person(1, "Angela Merkel", 69, LocalDate.parse("1954-07-17"), "Duits", 1.65, true, ClientStatuses.platinum, "merkel"),
    val bankAccs: List<BankAccount> = emptyList<BankAccount>(),
    val inUpdateMode: Boolean = false,
    val isBankAccountsExpanded: Boolean = false,
    val isExtraOptionsExpended: Boolean = false,
    val IsAddingPerson: Boolean = false
)