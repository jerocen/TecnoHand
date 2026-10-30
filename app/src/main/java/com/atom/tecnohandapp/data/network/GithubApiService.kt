package com.atom.tecnohandapp.data.network

import com.atom.tecnohandapp.data.models.GithubCommitResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getRepoCommits(
        @Path("owner") owner: String = "jerocen",
        @Path("repo") repo: String = "TecnoHand",
        @Query("per_page") perPage: Int = 15
    ): Response<List<GithubCommitResponse>>
}
