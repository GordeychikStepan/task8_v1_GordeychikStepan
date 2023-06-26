package com.bignerdranch.android.cinemaapp

import androidx.room.Database
import androidx.room.RoomDatabase

// класс базы данных, который будет использоваться Room
@Database(entities = [Movie::class], version = 1)
abstract  class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}