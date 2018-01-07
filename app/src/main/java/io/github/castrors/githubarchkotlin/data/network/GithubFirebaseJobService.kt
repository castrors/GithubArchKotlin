package io.github.castrors.githubarchkotlin.data.network

import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils

class GithubFirebaseJobService : JobService() {

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.d(LOG_TAG, "Job service started")

        val networkDataSource = InjectorUtils.provideNetworkDataSource(this.applicationContext)
        networkDataSource.fetchRepos()

        jobFinished(jobParameters, false)

        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return true
    }

    companion object {
        private val LOG_TAG = GithubFirebaseJobService::class.java.simpleName
    }
}