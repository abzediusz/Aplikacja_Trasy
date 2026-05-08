package com.example.mobilne_projekt_lab


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Czasy(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val trasa: String,
    val czas: Int,
    val data: String
)