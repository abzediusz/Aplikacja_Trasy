package com.example.mobilne_projekt_lab

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT nazwa FROM trasy")
    fun getAllNames(): LiveData<List<String>> // pobierz wszystkich użytkowników
    @Query("SELECT opis FROM trasy WHERE nazwa=:name LIMIT 1")
    fun getDescription(name: String): LiveData<String>
    @Query("SELECT nazwa FROM trasy WHERE typ=:type")
    fun getByType(type: String): LiveData<List<String>>
    @Query("SELECT image_uri FROM trasy WHERE nazwa = :name LIMIT 1")
    fun getRouteByName(name: String): LiveData<String>
    @Query("SELECT COUNT(*) FROM trasy")
    suspend fun getCount(): Int
    @Query("DELETE FROM trasy WHERE 1=1")
    suspend fun deleteAll()
    @Insert
    suspend fun insertRoute(trasa: Trasy) // wstaw nowego użytkownika
    @Update
    suspend fun updateUser(trasa: Trasy) // zaktualizuj dane użytkownika
    @Delete
    suspend fun deleteUser(trasa: Trasy) // usuń użytkownika
}