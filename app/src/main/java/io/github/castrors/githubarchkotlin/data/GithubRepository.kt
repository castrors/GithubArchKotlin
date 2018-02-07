package io.github.castrors.githubarchkotlin.data

import android.arch.lifecycle.LiveData
import android.util.Log
import io.github.castrors.githubarchkotlin.AppExecutors
import io.github.castrors.githubarchkotlin.data.database.PullRequest
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.data.network.GithubRepoNetworkDataSource
import io.github.castrors.githubarchkotlin.data.network.PullRequestDao
import io.github.castrors.githubarchkotlin.data.network.RepoDao

class GithubRepository private constructor(private val repoDao: RepoDao,
                                           private val pullRequestDao: PullRequestDao,
                                           private val githubNetworkDataSource: GithubRepoNetworkDataSource,
                                           private val executors: AppExecutors) {

    public var userReachedEndOfList = false
    var forceUpdate = false
    public var currentPage = 1

    private val isFetchNeeded: Boolean
        get() {
            val count = repoDao.countAllRepo()
            return count == 0 || userReachedEndOfList || forceUpdate
        }

    val githubRepositoriesList: LiveData<List<Repo>>
        get() {
            initializeData()
            return repoDao.getRepos()
        }


    fun providePullRequestsList(owner: String, repo: String): LiveData<List<PullRequest>> {
        githubNetworkDataSource.fetchPullRequests(owner, repo)
        return pullRequestDao.getPullRequestsByOwnerAndRepo(owner, repo)
    }

    init {

        val networkData = githubNetworkDataSource.currentGithubRepositories
        networkData.observeForever({ newReposFromInternet ->
            executors.diskIO().execute({
                Log.d(LOG_TAG, "Old weather deleted")
                newReposFromInternet?.let { repoDao.bulkInsert(it) }
                Log.d(LOG_TAG, "New values inserted")
            })
        })

        val repoNetworkData = githubNetworkDataSource.currentPullRequests
        repoNetworkData.observeForever({ newPullRequestFromInternet ->
            executors.diskIO().execute({
                newPullRequestFromInternet?.let { pullRequestDao.bulkInsert(it) }
                Log.d(LOG_TAG, "New pull request values inserted")
            })
        })

    }

    @Synchronized
    private fun initializeData() {
        executors.diskIO().execute({
            if (isFetchNeeded) {
                startFetchReposService()
            }
        })
    }

    private fun startFetchReposService() {
        githubNetworkDataSource.fetchRepos()
    }

    companion object {
        private val LOG_TAG = GithubRepository::class.java.simpleName

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: GithubRepository? = null

        @Synchronized
        fun getInstance(
                repoDao: RepoDao, pullRequestDao: PullRequestDao, githubRepoNetworkDataSource: GithubRepoNetworkDataSource,
                executors: AppExecutors): GithubRepository {
            Log.d(LOG_TAG, "Getting the repository")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = GithubRepository(repoDao, pullRequestDao, githubRepoNetworkDataSource,
                            executors)
                    Log.d(LOG_TAG, "Made new repository")
                }
            }
            return sInstance!!
        }
    }

    fun requestMore() {
        userReachedEndOfList = true
        startFetchReposService()
    }


    fun forceUpdateGithubRepos() {
        forceUpdate = true
        initializeData()
    }


}
