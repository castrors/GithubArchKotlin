package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.ui.detail.DetailActivity
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils
import io.github.castrors.githubarchkotlin.utilities.putBundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()

        val factory = InjectorUtils.provideMainActivityViewModelFactory(this.applicationContext)
        val viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)

        val listAdapter = RepoListAdapter(ArrayList(0), this, { repo ->
            startActivity(Intent(this, DetailActivity::class.java)
                    .putBundle(repo.owner.login, repo.name))
        })
        val recyclerView = findViewById<RecyclerView>(R.id.repoList)
        recyclerView.adapter = listAdapter

        viewModel.repoList.observe(this, Observer<List<Repo>> { repoEntries ->
            repoEntries?.let {
                listAdapter.repoList = it
            }
        })
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
    }
}


