package io.github.castrors.githubarchkotlin.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.Toast
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

        require(!intent.extras.isEmpty){"You must send the owner and the repo informations"}
        val factory = InjectorUtils.provideDetailActivityViewModelFactory(this.applicationContext, intent.owner(), intent.repo())
        val viewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel::class.java)

        val listAdapter = PullRequestsAdapter(ArrayList(0), this, { pullRequest ->
            Toast.makeText(applicationContext, "item clicked ${pullRequest.title}", Toast.LENGTH_SHORT).show()
        })

        val recyclerView = findViewById<RecyclerView>(R.id.pullRequestList)
        recyclerView.adapter = listAdapter

        viewModel.pullRequesList.observe(this, Observer<List<PullRequest>> { pullRequestEntries ->
            pullRequestEntries?.let {
                listAdapter.pullRequestList = it
            }
        })
    }
}
