package com.coolightman.movieapp.util

import androidx.room.TypeConverter
import com.coolightman.movieapp.model.data.Country
import com.coolightman.movieapp.model.data.Frame
import com.coolightman.movieapp.model.data.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun listCountryToJson(countries: List<Country>): String {
        val gson = Gson()
        return gson.toJson(countries)
    }

    @TypeConverter
    fun jsonToListCountry(json: String): List<Country> {
        val gson = Gson()
        val itemType = object : TypeToken<List<Country>>() {}.type
        return gson.fromJson(json, itemType)
    }

    @TypeConverter
    fun listGenreToJson(genres: List<Genre>): String {
        val gson = Gson()
        return gson.toJson(genres)
    }

    @TypeConverter
    fun jsonToListGenre(json: String): List<Genre> {
        val gson = Gson()
        val itemType = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(json, itemType)
    }

    @TypeConverter
    fun listFramesToJson(frames: List<Frame>): String {
        val gson = Gson()
        return gson.toJson(frames)
    }

    @TypeConverter
    fun jsonToListFrames(json: String): List<Frame> {
        val gson = Gson()
        val itemType = object : TypeToken<List<Frame>>() {}.type
        return gson.fromJson(json, itemType)
    }
}