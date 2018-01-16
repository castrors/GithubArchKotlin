package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.github.castrors.githubarchkotlin.data.GithubRepository

class DetailViewModelFactory(private val repository: GithubRepository,
                             private val owner: String,
                             private val repo: String) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return DetailActivityViewModel(repository, owner, repo) as T
    }
}