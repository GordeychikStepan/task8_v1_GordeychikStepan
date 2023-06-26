package com.bignerdranch.android.cinemaapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NavigationSetFragment : Fragment() {
    private lateinit var movieDao: MovieDao
    private lateinit var adapter: MovieAdapter
    private lateinit var selectedImageUri: Uri
    private lateinit var selectedImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_navigation_set, container, false)

        val spinner = view.findViewById<Spinner>(R.id.sortSpinner)
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_items_genres,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // сортировка по жанру
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val genre = parent.getItemAtPosition(position) as String
                loadMoviesByGenre(genre)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.setRecyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "movies-dbase"
        ).build()
        movieDao = database.movieDao()

        adapter = MovieAdapter(emptyList(), viewLifecycleOwner.lifecycleScope, movieDao, { movie -> onMovieClicked(movie) }, { loadMovies() })
        recyclerView.adapter = adapter

        // загрузка списка фильмов из базы данных и установка его в адаптер
        loadMovies()

        val createSetButton = view.findViewById<Button>(R.id.createSetButton)
        createSetButton.setOnClickListener {
            // обработатка нажатия кнопки "Добавить фильм"
            showAddMovieDialog()
        }

        return view
    }

    // загрузка фильмов по жанру
    private fun loadMoviesByGenre(genre: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val movies = movieDao.getMoviesByGenre(genre)
            withContext(Dispatchers.Main) {
                adapter.setData(movies)
            }
        }
    }

    // получение списка всех фильмов из базы данных и обновление адаптера
    private fun loadMovies() {
        lifecycleScope.launch(Dispatchers.IO) {
            val movies = movieDao.getAllMovies()
            withContext(Dispatchers.Main) {
                adapter.setData(movies)
            }
        }
    }

    // открытие галереи, чтобы выбрать изображение
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_SELECT_IMAGE)
    }

    // обработка результата выбора галереи
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageView.visibility = View.VISIBLE
            selectedImageView.setImageURI(selectedImageUri)
            if (selectedImageUri != null) {
                this.selectedImageUri = selectedImageUri
            }
        }
    }

    // отображение диалогового окна для добавления фильма
    private fun showAddMovieDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_movie, null)

        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        val authorEditText = dialogView.findViewById<EditText>(R.id.authorEditText)
        val genreSpinner = dialogView.findViewById<Spinner>(R.id.genreSpinner)
        val addImageButton = dialogView.findViewById<Button>(R.id.addImageButton)
        selectedImageView = dialogView.findViewById(R.id.selectedImageView)
        val addButton = dialogView.findViewById<Button>(R.id.addButton)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Добавить фильм")
            .setCancelable(false)
            .create()

        addImageButton.setOnClickListener {
            openGallery()
        }

        addButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val author = authorEditText.text.toString()
            val genre = genreSpinner.selectedItem.toString()
            val icon = selectedImageUri.toString()

            if (title.isNotEmpty() && genre.isNotEmpty() && icon.isNotEmpty() && author.isNotEmpty()) {
                val movie = Movie(icon = icon, title = title, genre = genre, author = author)
                lifecycleScope.launch(Dispatchers.IO) {
                    movieDao.addMovie(movie)
                    withContext(Dispatchers.Main) {
                        loadMovies()
                        alertDialog.dismiss()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    // обработка клика по фильму в RecyclerView
    private fun onMovieClicked(movie: Movie) {
        val intent = Intent(requireContext(), MovieDetailsActivity::class.java)
        intent.putExtra("movie", movie)
        startActivityForResult(intent, REQUEST_MOVIE_DETAILS)
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE = 100
        private const val REQUEST_MOVIE_DETAILS = 101
    }
}