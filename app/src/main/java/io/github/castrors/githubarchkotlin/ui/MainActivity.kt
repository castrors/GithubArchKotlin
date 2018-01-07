package io.github.castrors.githubarchkotlin.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.ui.list.MainActivityViewModel
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils

class MainActivity : AppCompatActivity() {

    private val LOG_TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = InjectorUtils.provideMainActivityViewModelFactory(this.applicationContext)
        val viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)


        viewModel.repoList.observe(this, Observer<List<Repo>>{ repoEntries ->
            Log.d(LOG_TAG, "atualizado" + repoEntries?.size)
        })
    }
}
