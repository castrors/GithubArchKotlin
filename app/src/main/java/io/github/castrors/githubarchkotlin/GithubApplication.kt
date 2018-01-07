package io.github.castrors.githubarchkotlin

import android.app.Application
import com.facebook.stetho.Stetho

class GithubApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}