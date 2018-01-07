package io.github.castrors.githubarchkotlin.data.network

import io.github.castrors.githubarchkotlin.data.database.GithubBase
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories?q=language:Java&sort=stars")
    fun fetchRepositories(@Query("page") page: String,
                          @Query("per_page") perPage: String): Call<GithubBase>
}