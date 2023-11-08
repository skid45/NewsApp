package com.skid.sources.di

import androidx.lifecycle.ViewModel

internal class SourcesComponentViewModel : ViewModel() {

    val sourcesComponent = DaggerSourcesComponent
        .factory()
        .create(deps = SourcesDepsProvider.deps)
}