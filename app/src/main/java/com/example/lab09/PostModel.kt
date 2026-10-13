package com.example.lab09

import com.google.gson.annotations.SerializedName

data class PostModel(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("link") val link: String?,
    @SerializedName("comment_count") val commentCount: Int?
)git add .
git commit -m "feat: crea PostModel para mapear el JSON de /posts"
git push