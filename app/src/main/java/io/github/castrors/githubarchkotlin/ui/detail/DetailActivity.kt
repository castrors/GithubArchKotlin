package io.github.castrors.githubarchkotlin.ui.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.PullRequest
import io.github.castrors.githubarchkotlin.ui.list.DetailActivityViewModel
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils

class DetailActivity : AppCompatActivity() {

    private val LOG_TAG: String = DetailActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val factory = InjectorUtils.provideDetailActivityViewModelFactory(this.applicationContext)
        val viewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel::class.java)

        var listAdapter = PullRequestsAdapter(ArrayList(0), this, { pullRequest ->
            Toast.makeText(applicationContext, "item clicked ${pullRequest.title}", Toast.LENGTH_SHORT).show()
        })

        val recyclerView = findViewById<RecyclerView>(R.id.pullRequestList)
        recyclerView.adapter = listAdapter

        viewModel.pullRequesList.observe(this, Observer<List<PullRequest>> { pullRequestEntries ->
            pullRequestEntries?.let {
                Log.d(LOG_TAG, "atualizado" + pullRequestEntries?.size)
                listAdapter.pullRequestList = pullRequestEntries
            }
        })
    }
}
