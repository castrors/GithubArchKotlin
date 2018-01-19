package io.github.castrors.githubarchkotlin.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.PullRequest
import io.github.castrors.githubarchkotlin.ui.list.DetailActivityViewModel
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils
import io.github.castrors.githubarchkotlin.utilities.owner
import io.github.castrors.githubarchkotlin.utilities.repo


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        require(!intent.extras.isEmpty) { "You must send the owner and the repo informations" }
        setupToolbar()
        val repository = InjectorUtils.provideRepository(this)
        val factory = InjectorUtils.provideDetailActivityViewModelFactory(this.applicationContext, intent.owner(), intent.repo())
        val viewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel::class.java)

        val listAdapter = PullRequestsAdapter(ArrayList(0), this, { pullRequest ->
            openPullRequestUrl(pullRequest)
        })
        val swipeToRefresh = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        swipeToRefresh.setOnRefreshListener{
            viewModel.forceUpdate()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.pullRequestList)
        recyclerView.adapter = listAdapter

        viewModel.pullRequesList.observe(this, Observer<List<PullRequest>> { pullRequestEntries ->
            pullRequestEntries?.let {
                listAdapter.pullRequestList = it
                repository.forceUpdate = false
            }
        })

        viewModel.isLoadingLiveData.observe(this, Observer<Boolean> {
            it?.let { swipeToRefresh.isRefreshing = it }
        })
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = intent.repo()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openPullRequestUrl(pullRequest: PullRequest) {
        val colorInt = ContextCompat.getColor(this, R.color.colorPrimary)
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(colorInt)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(pullRequest.html_url))
    }
}
