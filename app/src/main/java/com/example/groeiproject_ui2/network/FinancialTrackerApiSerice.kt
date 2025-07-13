package com.example.groeiproject_ui2.network

import com.example.groeiproject_ui2.model.BankAccount
import com.example.groeiproject_ui2.model.Person
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FinancialTrackerApiSerice {
    @GET("personen")
    suspend fun getPeople(): List<Person>

    @GET("bankrekeningen")
    suspend fun getBankAccounts(): List<BankAccount>

    @GET("bankrekeningen")
    suspend fun getBankAccountsFromPerson(@Query("planetId") personId: Int): List<BankAccount>

    @POST("personen")
    suspend fun addPerson(@Body person: Person): Person

    @PUT("personen/{id}")
    suspend fun updatePerson(@Path("id") id: Int, @Body person: Person): Person

    @DELETE("personen/{id}")
    suspend fun deletePerson(@Path("id") id: Int)
}