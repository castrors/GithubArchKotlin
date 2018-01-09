package io.github.castrors.githubarchkotlin.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.ui.list.MainActivityViewModel
import io.github.castrors.githubarchkotlin.ui.list.RepoListAdapter
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils

class MainActivity : AppCompatActivity() {

    private val LOG_TAG: String = MainActivity::class.java.simpleName

    private val itemListener: RepoListAdapter.RepoListAdapterOnItemClickHandler = object :
            RepoListAdapter.RepoListAdapterOnItemClickHandler {
        override fun onItemClick(repo: Repo) {
            Toast.makeText(applicationContext, "item clicked ${repo.full_name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = InjectorUtils.provideMainActivityViewModelFactory(this.applicationContext)
        val viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)

        var listAdapter = RepoListAdapter(ArrayList(0), this, itemListener)
        val recyclerView = findViewById<RecyclerView>(R.id.repoList)
        recyclerView.adapter = listAdapter

        viewModel.repoList.observe(this, Observer<List<Repo>> { repoEntries ->
            repoEntries?.let {
                Log.d(LOG_TAG, "atualizado" + repoEntries?.size)
                listAdapter.repoList = repoEntries
            }
        })
    }
}
