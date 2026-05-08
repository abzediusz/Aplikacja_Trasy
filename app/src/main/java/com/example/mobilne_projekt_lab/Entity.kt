package com.example.mobilne_projekt_lab
import android.net.Uri
import androidx.room.*
@Entity
data class Trasy(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val nazwa: String,
    val opis: String,
    val typ: String,
    val image_uri: String? = null
)
