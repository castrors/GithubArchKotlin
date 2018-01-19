package io.github.castrors.githubarchkotlin.data.database

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class GithubBase(
        val total_count: Int,
        val incomplete_results: Boolean,
        val message: String,
        @SerializedName("items") val repos: List<Repo>)

@Entity(tableName = "repo")
data class Repo(
        @PrimaryKey val id: Int,
        val name: String,
        val full_name: String,
        @Embedded val owner: Owner,
        val description: String? = "",
        val stargazers_count: Int,
        val forks_count: Int)

data class Owner(
        val login: String,
        val avatar_url: String)

@Entity(tableName = "pullrequest")
data class PullRequest(
        @PrimaryKey val id: Int,
        val html_url: String? = "",
        val title: String? = "",
        @Embedded val user: User,
        val body: String? = "",
        var repo: String? = "",
        var owner: String? = "")

data class User(
        var login: String? = "",
        var avatar_url: String? = "")