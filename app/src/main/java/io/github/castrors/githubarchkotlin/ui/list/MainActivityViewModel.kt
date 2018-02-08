package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import io.github.castrors.githubarchkotlin.data.GithubRepository
import io.github.castrors.githubarchkotlin.data.database.Repo

class MainActivityViewModel(private val repository: GithubRepository) : ViewModel() {

    var isLoadingLiveData = MediatorLiveData<Boolean>()

    fun getRepoList(): LiveData<List<Repo>> {
        val repo = repository.githubRepositoriesList
        isLoadingLiveData.addSource(repo) { isLoadingLiveData.value = false }

        return repo
    }

    fun forceUpdate() {
        repository.forceUpdateGithubRepos()
    }
}