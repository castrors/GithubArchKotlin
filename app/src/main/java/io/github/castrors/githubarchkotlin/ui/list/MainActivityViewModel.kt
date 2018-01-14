package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.castrors.githubarchkotlin.data.GithubRepository
import io.github.castrors.githubarchkotlin.data.database.Repo

internal class MainActivityViewModel(private val repository: GithubRepository) : ViewModel() {
    val repoList: LiveData<List<Repo>> = repository.githubRepositoriesList
}