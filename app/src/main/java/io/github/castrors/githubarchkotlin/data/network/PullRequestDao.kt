package io.github.castrors.githubarchkotlin.data.network

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.github.castrors.githubarchkotlin.data.database.PullRequest

@Dao
interface PullRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pullRequest: PullRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(pullRequestList: List<PullRequest>)

    @Query("SELECT * FROM pullrequest")
    fun getPullRequests(): LiveData<List<PullRequest>>

    @Query("SELECT * FROM pullrequest WHERE owner = :owner AND repo = :repo")
    fun getPullRequestsByOwnerAndRepo(owner: String, repo: String): LiveData<List<PullRequest>>

    @Query("SELECT COUNT(id) FROM pullrequest")
    fun countAllPullRequest(): Int

}