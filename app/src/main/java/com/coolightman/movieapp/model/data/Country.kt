package com.coolightman.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Country(
    @PrimaryKey
    @SerializedName("country") val name: String
)