package io.github.castrors.githubarchkotlin.ui.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.Repo
import io.github.castrors.githubarchkotlin.utilities.InjectorUtils
import kotlinx.android.synthetic.main.repo_list_item.view.*

class RepoListAdapter(newRepoList: List<Repo>,
                      private val context: Context,
                      private val itemClick: (Repo) -> Unit) : RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    private val repository = InjectorUtils.provideRepository(context)

    var repoList: List<Repo> = newRepoList
        set(repoList) {
            field = repoList
            repository.userReachedEndOfList = false
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = repoList[position]
        holder.bindView(expense)
        if (reachedLastPosition(position)) {
            repository.requestMore()
        }
    }

    private fun reachedLastPosition(position: Int) = repoList.size - 1 == position

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.repo_list_item, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

    class ViewHolder(itemView: View, private val itemClick: (Repo) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bindView(repo: Repo) {
            with(repo) {
                Picasso.with(itemView.context).load(owner.avatar_url).into(itemView.image)
                itemView.name.text = full_name
                itemView.fullDescription.text = description
                itemView.forkCount.text = forks_count.toString()
                itemView.starCount.text = stargazers_count.toString()
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}