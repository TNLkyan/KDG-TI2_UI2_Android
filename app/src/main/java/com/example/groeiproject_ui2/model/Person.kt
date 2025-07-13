package com.example.groeiproject_ui2.model

import com.example.groeiproject_ui2.network.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Person(
    @SerialName("id")
    val id: Int,
    @SerialName("naam")
    var naam: String,
    @SerialName("leeftijd")
    var leeftijd: Int,
    @SerialName("geboortedatum")
    @Serializable(with = LocalDateSerializer::class)
    var geboortedatum: LocalDate,
    @SerialName("nationaliteit")
    var nationaliteit: String,
    @SerialName("lengte")
    var lengte: Double,  // In meters
    @SerialName("premium_klant")
    var premiumKlant: Boolean,
    @SerialName("klant_status")
    var klantStatus: ClientStatuses,  // Enum
    @SerialName("foto")
    var fotoUrl: String
)