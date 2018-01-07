package io.github.castrors.githubarchkotlin.data.network

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.github.castrors.githubarchkotlin.data.database.Repo

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repo: Repo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(repoList: List<Repo>)

    @Query("SELECT * FROM repo")
    fun getRepos(): LiveData<List<Repo>>

    @Query("SELECT COUNT(id) FROM repo")
    fun countAllRepo(): Int

}