package io.github.castrors.githubarchkotlin.data.network

import android.app.IntentService
import android.content.Intent
import android.util.Log
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils
import io.github.castrors.githubarchkotlin.utilities.owner
import io.github.castrors.githubarchkotlin.utilities.repo


class PullRequestIntentService : IntentService("PullRequestIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        Log.d(LOG_TAG, "Intent service started")
        val networkDataSource = InjectorUtils.provideNetworkDataSource(this.applicationContext)
        intent?.let {
            networkDataSource.fetchPullRequests(it.owner(), it.repo())
        }
    }

    companion object {
        private val LOG_TAG = PullRequestIntentService::class.java.simpleName
    }
}