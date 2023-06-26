package com.bignerdranch.android.cinemaapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieAdapter(
    private var movies: List<Movie>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val movieDao: MovieDao,
    private val onItemClick: (Movie) -> Unit,
    private val loadMovies: (() -> Unit)? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movies.size

    fun setData(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = movies[position]
                    onItemClick(movie)
                }
            }
        }

        // установка данных фильма для элементов интерфейса
        fun bind(movie: Movie) {
            iconImageView.setImageURI(Uri.parse(movie.icon))
            titleTextView.text = movie.title

            deleteImageView.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, deleteImageView)
                popupMenu.inflate(R.menu.menu_delete_film)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_del -> {
                            val movie = movies[adapterPosition]
                            deleteMovie(movie)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }

        // удаление фильма из базы данных и обновление списка
        private fun deleteMovie(movie: Movie) {
            lifecycleScope.launch(Dispatchers.IO) {
                movieDao.deleteMovie(movie)
                withContext(Dispatchers.Main) {
                    loadMovies?.invoke()
                }
            }
        }
    }
}