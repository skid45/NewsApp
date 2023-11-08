package com.skid.filters.model

enum class Sorting(val apiName: String) {
    POPULAR("popularity"),
    NEW("publishedAt"),
    RELEVANT("relevancy")
}