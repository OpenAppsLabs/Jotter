package com.openapps.jotter.data.model

import com.google.gson.annotations.SerializedName

data class GithubRelease(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("assets") val assets: List<GithubAsset>,
    @SerializedName("body") val description: String
)

data class GithubAsset(
    @SerializedName("browser_download_url") val downloadUrl: String,
    @SerializedName("name") val name: String
)
