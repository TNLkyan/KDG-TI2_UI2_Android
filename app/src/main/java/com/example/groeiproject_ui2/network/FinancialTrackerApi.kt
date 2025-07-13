package com.example.groeiproject_ui2.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import java.time.LocalDate

object FinancialTrackerApi {
    @OptIn(ExperimentalSerializationApi::class)
    val api: FinancialTrackerApiSerice by lazy {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            useAlternativeNames = false
            isLenient = true

            serializersModule = SerializersModule {
                contextual(LocalDate::class, LocalDateSerializer)
            }
        }

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FinancialTrackerApiSerice::class.java)
    }
}