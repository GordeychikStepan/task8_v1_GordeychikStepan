package com.bignerdranch.android.cinemaapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// интерфейс доступа к данным (DAO) для работы с таблицей фильмов
@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAllMovies(): List<Movie>

    @Insert
    fun addMovie(movie: Movie)

    @Delete
    fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE genre = :genre")
    fun getMoviesByGenre(genre: String): List<Movie>
}