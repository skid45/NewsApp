package com.skid.headlines.di

import androidx.lifecycle.ViewModel

internal class HeadlinesComponentViewModel : ViewModel() {

    val headlinesComponent =
        DaggerHeadlinesComponent.factory().create(deps = HeadlinesDepsProvider.deps)
}