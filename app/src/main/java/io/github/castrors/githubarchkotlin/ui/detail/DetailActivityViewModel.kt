package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.github.castrors.githubarchkotlin.data.GithubRepository
import io.github.castrors.githubarchkotlin.data.database.PullRequest

internal class DetailActivityViewModel(private val repository: GithubRepository, owner: String, repo: String) : ViewModel() {
    val pullRequesList: LiveData<List<PullRequest>> = repository.providePullRequestsList(owner, repo)

    var isLoading = false
}