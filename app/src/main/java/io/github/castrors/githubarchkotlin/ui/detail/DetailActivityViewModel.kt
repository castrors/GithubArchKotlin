package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import io.github.castrors.githubarchkotlin.data.GithubRepository
import io.github.castrors.githubarchkotlin.data.database.PullRequest

internal class DetailActivityViewModel(private val repository: GithubRepository,
                                       private val owner: String,
                                       private val repo: String) : ViewModel() {
    val pullRequesList: LiveData<List<PullRequest>> = repository.providePullRequestsList(owner, repo)

    var isLoadingLiveData = MediatorLiveData<Boolean>().apply {
        this.addSource(pullRequesList) { this.value = false }
    }

    fun forceUpdate() {
        repository.forceUpdatePullRequest(owner, repo)
    }
}