package io.github.castrors.githubarchkotlin.ui.detail

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.github.castrors.githubarchkotlin.R
import io.github.castrors.githubarchkotlin.data.database.PullRequest
import kotlinx.android.synthetic.main.pull_request_list_item.view.*

class PullRequestsAdapter(newPullRequestList: List<PullRequest>,
                          private val context: Context,
                          private val itemClick: (PullRequest) -> Unit) : RecyclerView.Adapter<PullRequestsAdapter.ViewHolder>() {

    var pullRequestList: List<PullRequest> = newPullRequestList
        set(pullRequestList) {
            field = pullRequestList
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: PullRequestsAdapter.ViewHolder, position: Int) {
        val pullRequest = pullRequestList[position]
        holder.bindView(pullRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PullRequestsAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.pull_request_list_item, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return pullRequestList.size
    }

    class ViewHolder(itemView: View, private val itemClick: (PullRequest) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bindView(pullRequest: PullRequest) {
            with(pullRequest) {
                Picasso.with(itemView.context).load(pullRequest.user.avatar_url).into(itemView.userImage)
                itemView.title.text = pullRequest.title
                itemView.body.text = pullRequest.body
                itemView.userName.text = pullRequest.user.login
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}