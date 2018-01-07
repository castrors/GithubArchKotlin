package io.github.castrors.githubarchkotlin.utilities

import android.content.Context
import io.github.castrors.githubarchkotlin.AppExecutors
import io.github.castrors.githubarchkotlin.data.GithubRepository
import io.github.castrors.githubarchkotlin.data.database.GithubDatabase
import io.github.castrors.githubarchkotlin.data.network.GithubRepoNetworkDataSource
import io.github.castrors.githubarchkotlin.ui.list.MainViewModelFactory

object InjectorUtils {

    fun provideRepository(context: Context): GithubRepository {
        val database = GithubDatabase.getInstance(context.applicationContext)
        val executors = AppExecutors.instance
        val networkDataSource = GithubRepoNetworkDataSource.getInstance(context.applicationContext, executors)
        return GithubRepository.getInstance(database.repoDao(), networkDataSource, executors)
    }

    fun provideNetworkDataSource(context: Context): GithubRepoNetworkDataSource {
        provideRepository(context.applicationContext)
        val executors = AppExecutors.instance
        return GithubRepoNetworkDataSource.getInstance(context.applicationContext, executors)
    }

//    fun provideDetailViewModelFactory(context: Context, date: Date): DetailViewModelFactory {
//        val repository = provideRepository(context.applicationContext)
//        return DetailViewModelFactory(repository, date)
//    }
//
    fun provideMainActivityViewModelFactory(context: Context): MainViewModelFactory {
        val repository = provideRepository(context.applicationContext)
        return MainViewModelFactory(repository)
    }

}