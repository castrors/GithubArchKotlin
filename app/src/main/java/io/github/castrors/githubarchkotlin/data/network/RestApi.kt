package io.github.castrors.githubarchkotlin.data.network

import io.github.castrors.githubarchkotlin.data.database.GithubBase
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class RestApi{
    private val BASE_URL: String = "https://api.github.com"
    private val githubApi: GithubApi

    init {
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

        githubApi = retrofit.create(GithubApi::class.java)
    }

    fun getGithubRepositories(page: Int = 1): Call<GithubBase>{
        return githubApi.fetchRepositories(page.toString())
    }

}

interface GithubApi {
    @GET("search/repositories?q=language:Java&sort=stars")
    fun fetchRepositories(@Query("page") page: String): Call<GithubBase>
}