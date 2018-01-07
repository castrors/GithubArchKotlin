package io.github.castrors.githubarchkotlin.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import io.github.castrors.githubarchkotlin.data.network.RepoDao

@Database(entities = arrayOf(Repo::class), version = 1, exportSchema = false)
abstract class GithubDatabase : RoomDatabase() {

    abstract fun repoDao(): RepoDao

    companion object {

        private val DATABASE_NAME = "repo"

        private val LOCK = Any()
        @Volatile private var instance: GithubDatabase? = null

        fun getInstance(context: Context): GithubDatabase {
            if (instance == null) {
                synchronized(LOCK) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext,
                                GithubDatabase::class.java, GithubDatabase.DATABASE_NAME).build()
                    }
                }
            }
            return instance!!
        }
    }
}
