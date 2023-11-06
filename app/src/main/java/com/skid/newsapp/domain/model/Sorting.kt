package com.skid.newsapp.domain.model

enum class Sorting(val apiName: String) {
    POPULAR("popularity"),
    NEW("publishedAt"),
    RELEVANT("relevancy")
}