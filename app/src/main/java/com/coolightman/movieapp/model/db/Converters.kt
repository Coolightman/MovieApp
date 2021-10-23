package com.coolightman.movieapp.model.db

import androidx.room.TypeConverter
import com.coolightman.movieapp.model.data.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun listMoviesToJson(movies: List<Movie>): String {
        return Gson().toJson(movies)
    }

    @TypeConverter
    fun jsonToListMovies(json: String): List<Movie> {
        val itemType = object : TypeToken<List<Movie>>() {}.type
        return Gson().fromJson(json, itemType)
    }

    @TypeConverter
    fun listCountriesToJson(countries: List<Country>): String {
        return Gson().toJson(countries)
    }

    @TypeConverter
    fun jsonToListCountries(json: String): List<Country> {
        val itemType = object : TypeToken<List<Country>>() {}.type
        return Gson().fromJson(json, itemType)
    }

    @TypeConverter
    fun listGenresToJson(genres: List<Genre>): String {
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun jsonToListGenres(json: String): List<Genre> {
        val itemType = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson(json, itemType)
    }

    @TypeConverter
    fun listVideosToJson(videos: List<Video>): String {
        return Gson().toJson(videos)
    }

    @TypeConverter
    fun jsonToListVideos(json: String): List<Video> {
        val itemType = object : TypeToken<List<Video>>() {}.type
        return Gson().fromJson(json, itemType)
    }

    @TypeConverter
    fun listFramesToJson(frames: List<Frame>): String {
        return Gson().toJson(frames)
    }

    @TypeConverter
    fun jsonToListFrames(json: String): List<Frame> {
        val itemType = object : TypeToken<List<Frame>>() {}.type
        return Gson().fromJson(json, itemType)
    }

    @TypeConverter
    fun listFactsToJson(facts: List<Fact>): String {
        return Gson().toJson(facts)
    }

    @TypeConverter
    fun jsonToListFacts(json: String): List<Fact> {
        val itemType = object : TypeToken<List<Fact>>() {}.type
        return Gson().fromJson(json, itemType)
    }
}