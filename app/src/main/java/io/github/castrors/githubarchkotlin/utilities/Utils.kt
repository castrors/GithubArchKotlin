package io.github.castrors.githubarchkotlin.utilities

import android.content.Intent
import android.os.Bundle

val OWNER_EXTRA = "OWNER_EXTRA"
val REPO_EXTRA = "REPO_EXTRA"

fun Intent.putBundle(owner: String, repo: String): Intent? {
    val bundle = Bundle()
    bundle.putString(OWNER_EXTRA, owner)
    bundle.putString(REPO_EXTRA, repo)
    return this.putExtras(bundle)
}

fun Intent.owner(): String {
    return this.extras.get(OWNER_EXTRA).toString()
}

fun Intent.repo(): String {
    return this.extras.get(REPO_EXTRA).toString()
}