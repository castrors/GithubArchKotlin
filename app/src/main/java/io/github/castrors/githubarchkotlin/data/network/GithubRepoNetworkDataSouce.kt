package io.github.castrors.githubarchkotlin.data.network

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.util.Log
import com.firebase.jobdispatcher.*
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


    /**
     * Starts an intent service to fetch the weather.
     */
    fun startFetchGithubRepositoriesService() {
        val intentToFetch = Intent(mContext, GithubSyncIntentService::class.java)
        mContext.startService(intentToFetch)
        Log.d(LOG_TAG, "Service created")
    }

    fun startFetchPullRequestService() {
        val intentToFetch = Intent(mContext, PullRequestIntentService::class.java)
        mContext.startService(intentToFetch)
        Log.d(LOG_TAG, "Service created")
    }

    /**
     * Schedules a repeating job service which fetches the weather.
     */
    fun scheduleRecurringFetchGithubRepositoriesSync() {
        val driver = GooglePlayDriver(mContext)
        val dispatcher = FirebaseJobDispatcher(driver)

        // Create the Job to periodically sync Github
        val syncGithubJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Github's data */
                .setService(GithubFirebaseJobService::class.java)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(SUNSHINE_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want Github's weather data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the weather data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build()

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncGithubJob)
        Log.d(LOG_TAG, "Job scheduled")
    }

    /**
     * Gets the newest weather
     */
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

    fun fetchPullRequests() {
        mExecutors.networkIO().execute({
            try {
                val repository = InjectorUtils.provideRepository(mContext)
                Log.d(LOG_TAG, "Fetch pull request")

                val pullRequests = RestApi().getPullRequests(repository.owner, repository.repo)
                val response = pullRequests.execute()

                if (response.isSuccessful) {
                    val pullRequestsList: MutableList<PullRequest> = response.body() as MutableList<PullRequest>
                    pullRequestsList.map {
                        it.owner = repository.owner
                        it.repo = repository.repo
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