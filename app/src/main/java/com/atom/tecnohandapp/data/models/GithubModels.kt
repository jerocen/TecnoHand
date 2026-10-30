package com.atom.tecnohandapp.data.models

import com.google.gson.annotations.SerializedName

data class GithubCommitResponse(
    @SerializedName("sha") val sha: String,
    @SerializedName("commit") val commit: GithubCommitDetail
)

data class GithubCommitDetail(
    @SerializedName("message") val message: String,
    @SerializedName("author") val author: GithubCommitAuthor
)

data class GithubCommitAuthor(
    @SerializedName("name") val name: String,
    @SerializedName("date") val date: String
)
