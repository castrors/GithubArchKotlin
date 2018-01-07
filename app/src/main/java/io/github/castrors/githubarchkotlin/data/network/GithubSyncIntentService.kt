package io.github.castrors.githubarchkotlin.data.network

import android.app.IntentService
import android.content.Intent
import android.util.Log
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils


class GithubSyncIntentService : IntentService("GithubSyncIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        Log.d(LOG_TAG, "Intent service started")
        val networkDataSource = InjectorUtils.provideNetworkDataSource(this.applicationContext)
        networkDataSource.fetchRepos()
    }

    companion object {
        private val LOG_TAG = GithubSyncIntentService::class.java.simpleName
    }
}