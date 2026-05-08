package com.example.mobilne_projekt_lab


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimeDao {
    @Query("SELECT * FROM Czasy WHERE trasa=:nazwa")
    fun getByName(nazwa: String): LiveData<List<Czasy>>
    @Query("SELECT COUNT(*) FROM Czasy")
    suspend fun getCount(): Int
    @Query("DELETE FROM Czasy WHERE 1=1")
    suspend fun deleteAll()
    @Query("INSERT INTO Czasy (trasa,czas,data) VALUES(:nazwa,:czas,:data)")
    suspend fun insertTime(nazwa: String,czas: Int,data: String)
    @Update
    suspend fun updateTime(czas: Czasy)
    @Delete
    suspend fun deleteTime(czas: Czasy)
}