package com.example.groeiproject_ui2.model

import com.example.groeiproject_ui2.network.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class BankAccount(
    @SerialName("id")
    val id: Int,
    @SerialName("persoon_id")
    val persoon_id: Int,
    @SerialName("banknaam")
    val banknaam: String,
    @SerialName("rekeningnummer")
    val rekeningnummer: String,
    @SerialName("saldo")
    val saldo: Double,
    @SerialName("valuta")
    val valuta: String,
    @SerialName("limiet")
    val limiet: Double,
    @SerialName("aangemaakt_op")
    @Serializable(with = LocalDateSerializer::class)
    val aangemaakt_op: LocalDate,
    @SerialName("flagged")
    val flagged: Boolean
)