package io.github.castrors.githubarchkotlin.ui.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.Repo
import kotlinx.android.synthetic.main.repo_list_item.view.*

class RepoListAdapter(newRepoList: List<Repo>,
                      private val context: Context,
                      private val itemListener: RepoListAdapterOnItemClickHandler) : RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    var repoList: List<Repo> = newRepoList
        set(repoList) {
            field = repoList
            notifyDataSetChanged()
        }

    interface RepoListAdapterOnItemClickHandler {
        fun onItemClick(repo: Repo)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val expense = repoList[position]
        holder?.bindView(expense)

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.repo_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(repo: Repo) {
            val name = itemView.name
            val description = itemView.description
            val forksCount = itemView.forkCount
            val starCount = itemView.starCount

            name.text = repo.full_name
            description.text = repo.description
            forksCount.text = repo.forks_count.toString()
            starCount.text = repo.stargazers_count.toString()
        }
    }
}