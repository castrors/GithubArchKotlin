package io.github.castrors.githubarchkotlin.data.network

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import io.github.castrors.githubarchkotlin.AppExecutors
import io.github.castrors.githubarchkotlin.data.database.PullRequest
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils
import java.util.concurrent.TimeUnit

class GithubRepoNetworkDataSource constructor(private val mContext: Context, private val mExecutors: AppExecutors) {

    val EMPTY_RESPONSE_MESSAGE = "Only the first 1000 search results are available"
    private val downloadedGithubRepositories: MutableLiveData<List<Repo>>
    private val downloadedPullRequests: MutableLiveData<List<PullRequest>>

    val currentGithubRepositories: LiveData<List<Repo>>
        get() = downloadedGithubRepositories

    val currentPullRequests: LiveData<List<PullRequest>>
        get() = downloadedPullRequests

    init {
        downloadedGithubRepositories = MutableLiveData<List<Repo>>()
        downloadedPullRequests = MutableLiveData<List<PullRequest>>()
    }


    internal fun fetchRepos() {

        mExecutors.networkIO().execute({
            try {
                val repository = InjectorUtils.provideRepository(mContext)
                Log.d(LOG_TAG, "Fetch started, page ${repository.currentPage}")
                val githubRepositories = RestApi().getGithubRepositories(repository.currentPage)
                val response = githubRepositories.execute()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.message == null) {
                        val githubRepos = body?.repos
                        downloadedGithubRepositories.postValue(githubRepos)
                        repository.currentPage++
                    } else {
                        repository.currentPage = -1
                    }
                }
            } catch (e: Exception) {
                // Server probably invalid
                e.printStackTrace()
            }
        })
    }

    internal fun fetchPullRequests(owner: String, repo: String) {
        mExecutors.networkIO().execute({
            try {
                Log.d(LOG_TAG, "Fetch pull request")

                val pullRequests = RestApi().getPullRequests(owner, repo)
                val response = pullRequests.execute()

                if (response.isSuccessful) {
                    val pullRequestsList: MutableList<PullRequest> = response.body() as MutableList<PullRequest>
                    pullRequestsList.map {
                        it.owner = owner
                        it.repo = repo
                    }
                    downloadedPullRequests.postValue(pullRequestsList)
                }
            } catch (e: Exception) {
                // Server probably invalid
                e.printStackTrace()
            }
        })
    }

    companion object {
        // The number of days we want our API to return, set to 14 days or two weeks
        private val LOG_TAG = GithubRepoNetworkDataSource::class.java.simpleName

        // Interval at which to sync with the weather. Use TimeUnit for convenience, rather than
        // writing out a bunch of multiplication ourselves and risk making a silly mistake.
        private val SYNC_INTERVAL_HOURS = 3
        private val SYNC_INTERVAL_SECONDS = TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS.toLong()).toInt()
        private val SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3
        private val SUNSHINE_SYNC_TAG = "sunshine-sync"

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: GithubRepoNetworkDataSource? = null

        /**
         * Get the singleton for this class
         */
        fun getInstance(context: Context, executors: AppExecutors): GithubRepoNetworkDataSource {
            Log.d(LOG_TAG, "Getting the network data source")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = GithubRepoNetworkDataSource(context.applicationContext, executors)
                    Log.d(LOG_TAG, "Made new network data source")
                }
            }
            return sInstance!!
        }
    }

}