package com.example.studyflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        SessionEntity::class,
        TaskEntity::class,
        SubjectEntity::class,        // ★ new
        StudySessionEntity::class    // ★ new
    ],
    version = 3,                      // bumped
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun taskDao(): TaskDao
    abstract fun subjectDao(): SubjectDao              // ★ new
    abstract fun studySessionDao(): StudySessionDao    // ★ new

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyflow.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}