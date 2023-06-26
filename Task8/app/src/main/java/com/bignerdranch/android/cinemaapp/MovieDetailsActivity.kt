package com.bignerdranch.android.cinemaapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class MovieDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        // получение объекта фильма с помощью getParcelableExtra
        val movie = intent.getParcelableExtra<Movie>("movie")

        if (movie != null) {
            // отображение информации о фильме на экране
            val titleTextView = findViewById<TextView>(R.id.titleTextView)
            val genreTextView = findViewById<TextView>(R.id.genreTextView)
            val iconImageView = findViewById<ImageView>(R.id.iconImageView)
            val authorTextView = findViewById<TextView>(R.id.authorTextView)

            titleTextView.text = movie.title
            genreTextView.text = "Жанр: ${movie.genre}"
            iconImageView.setImageURI(Uri.parse(movie.icon))
            authorTextView.text = "Автор: ${movie.author}"
        }

        val backImage = findViewById<ImageView>(R.id.backImage)
        backImage.setOnClickListener {
            finish()
        }
    }
}