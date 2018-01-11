package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.github.castrors.githubarchkotlin.data.GithubRepository

class DetailViewModelFactory(private val repository: GithubRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return DetailViewModelFactory(repository) as T
    }
}