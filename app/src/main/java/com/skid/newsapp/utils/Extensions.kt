package com.skid.newsapp.utils

import java.util.Locale

fun String.getCountryName(): String {
    return Locale("", this).displayCountry
}