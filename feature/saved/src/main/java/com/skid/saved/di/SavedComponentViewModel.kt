package com.skid.saved.di

import androidx.lifecycle.ViewModel

internal class SavedComponentViewModel : ViewModel() {

    val savedComponent = DaggerSavedComponent.factory().create(deps = SavedDepsProvider.deps)
}