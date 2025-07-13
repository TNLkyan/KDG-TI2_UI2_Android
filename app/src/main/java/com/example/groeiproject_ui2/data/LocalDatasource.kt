package com.example.groeiproject_ui2.data

import com.example.groeiproject_ui2.model.BankAccount
import com.example.groeiproject_ui2.model.ClientStatuses
import com.example.groeiproject_ui2.model.Person
import java.time.LocalDate

object LocalDatasource {
    var localPeople = mutableListOf<Person>(
        Person(
            1,
            "Angela Merkel",
            69,
            LocalDate.parse("1954-07-17"),
            "Duits",
            1.65,
            true,
            ClientStatuses.platinum,
            "https://www.bundestag.de/resource/image/521966/3x4/864/1152/f3faeb589dba839edcdd516af63e06c9/2CD06EF9822B9E7F280B683C36A07C4F/merkel_angela_gross.jpg"
        ),
        Person(
            2,
            "Barack Obama",
            62,
            LocalDate.parse("1961-08-04"),
            "Amerikaans",
            1.85,
            true,
            ClientStatuses.goud,
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrGh43up3RAkGouoYLV4O5C8jcWAJkKBnc2A&s"
        ),
        Person(
            3,
            "Emma Watson",
            33,
            LocalDate.parse("1990-04-15"),
            "Brits",
            1.65,
            false,
            ClientStatuses.zilver,
            "https://m.media-amazon.com/images/M/MV5BMTQ3ODE2NTMxMV5BMl5BanBnXkFtZTgwOTIzOTQzMjE@._V1_.jpg"
        ),
        Person(
            4,
            "Lionel Messi",
            36,
            LocalDate.parse("1987-06-24"),
            "Argentijns",
            1.70,
            true,
            ClientStatuses.platinum,
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGv0ZIrLidHrXmxdSY38qwW3_FyQZhJo-sFQ&s"
        ),
    )

    val localBankAccounts = listOf<BankAccount>(
        BankAccount(
            1,
            1,
            "Deutsche Bank",
            "DE89370400440532013000",
            285000.00,
            "EUR",
            500000.00,
            LocalDate.parse("2005-11-22"),
            false
        ),
        BankAccount(
            2,
            2,
            "JPMorgan Chase",
            "US02100002100000000001",
            1250000.50,
            "EUR",
            2000000.00,
            LocalDate.parse("2008-01-20"),
            false
        ),
        BankAccount(
            3,
            4,
            "Banco de la Nación Argentina",
            "AR3200000000000000000001",
            987500.75,
            "EUR",
            1500000.00,
            LocalDate.parse("2010-05-15"),
            false
        ),
        BankAccount(
            4,
            4,
            "Santander",
            "ES9121000418450200051332",
            250000.00,
            "EUR",
            500000.00,
            LocalDate.parse("2018-07-01"),
            false
        )
    )
}