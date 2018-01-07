package io.github.castrors.githubarchkotlin.data

import android.arch.lifecycle.LiveData
import android.util.Log
import io.github.castrors.githubarchkotlin.AppExecutors
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.data.network.GithubRepoNetworkDataSource
import io.github.castrors.githubarchkotlin.data.network.RepoDao

class GithubRepository private constructor(private val repoDao: RepoDao,
                                           private val githubNetworkDataSource: GithubRepoNetworkDataSource,
                                           private val executors: AppExecutors) {
    private var initialized = false

    private val isFetchNeeded: Boolean
        get() {
            val count = repoDao.countAllRepo()
            return count == 0
        }

    val githubRepositoriesList: LiveData<List<Repo>>
        get() {
            initializeData()
            return repoDao.getRepos()
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

    }

    @Synchronized
    private fun initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (initialized) return
        initialized = true

        executors.diskIO().execute({
            githubNetworkDataSource.scheduleRecurringFetchGithubRepositoriesSync()

            if (isFetchNeeded) {
                startFetchReposService()
            }
        })

    }

    private fun startFetchReposService() {
        githubNetworkDataSource.startFetchGithubRepositoriesService()
    }

//    fun getWeatherByDate(date: Date): LiveData<WeatherEntry> {
//        initializeData()
//        return repoDao.getWeatherByDate(date)
//    }

    companion object {
        private val LOG_TAG = GithubRepository::class.java.simpleName

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: GithubRepository? = null

        @Synchronized
        fun getInstance(
                weatherDao: RepoDao, githubRepoNetworkDataSource: GithubRepoNetworkDataSource,
                executors: AppExecutors): GithubRepository {
            Log.d(LOG_TAG, "Getting the repository")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = GithubRepository(weatherDao, githubRepoNetworkDataSource,
                            executors)
                    Log.d(LOG_TAG, "Made new repository")
                }
            }
            return sInstance!!
        }
    }

}
