package io.github.castrors.githubarchkotlin.ui.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.ui.detail.DetailActivity
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils
import io.github.castrors.githubarchkotlin.utilities.putBundle

class MainActivity : AppCompatActivity(){
    private lateinit var factory: MainViewModelFactory
    private lateinit var viewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()

        factory = InjectorUtils.provideMainActivityViewModelFactory(this.applicationContext)
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)
        val repository = InjectorUtils.provideRepository(this)

        val listAdapter = RepoListAdapter(ArrayList(0), this, { repo ->
            startActivity(Intent(this, DetailActivity::class.java)
                    .putBundle(repo.owner.login, repo.name))
        })
        val swipeToRefresh = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        swipeToRefresh.setOnRefreshListener{
            viewModel.forceUpdate()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.repoList)
        recyclerView.adapter = listAdapter

        viewModel.getRepoList().observe(this, Observer<List<Repo>> { repoEntries ->
            repoEntries?.let {
                listAdapter.repoList = it
                repository.forceUpdate = false
            }
        })
        viewModel.isLoadingLiveData.observe(this, Observer<Boolean> {
            it?.let { swipeToRefresh.isRefreshing = it }
        })
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
    }
}


