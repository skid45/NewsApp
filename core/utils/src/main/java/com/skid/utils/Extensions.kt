package com.skid.utils

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun String.getCountryName(): String {
    return Locale("", this).displayCountry
}

fun <T : Flow<R>, R> Fragment.collectFlow(flow: T, collectBlock: suspend (R) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collectBlock)
        }
    }
}

fun <T : Flow<R>, R> AppCompatActivity.collectFlow(flow: T, collectBlock: suspend (R) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collectBlock)
        }
    }
}

fun Calendar.format(pattern: String, locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(pattern, locale).format(this.time)
}

fun String.parseToCalendar(pattern: String, locale: Locale = Locale.getDefault()): Calendar {
    val format = SimpleDateFormat(pattern, locale).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return Calendar.getInstance(TimeZone.getDefault()).apply {
        time = format.parse(this@parseToCalendar)!!
    }
}

fun Pair<Calendar, Calendar>.getDisplayChosenRange(): String {
    return when {
        first == second -> first.format("MMM dd, yyyy")
        first[Calendar.YEAR] == second[Calendar.YEAR] -> {
            val firstDate = first.format("MMM dd")
            val secondDate = second.format("MMM dd, yyyy")
            "$firstDate - $secondDate"
        }

        else -> {
            val firstDate = first.format("MMM dd, yyyy")
            val secondDate = second.format("MMM dd, yyyy")
            "$firstDate - $secondDate"
        }
    }
}

fun <T : Any> Flow<T>.asObservable(): Observable<T> {
    return Observable.create { emitter ->
        onEach { value ->
            emitter.onNext(value)
        }.catch { e ->
            emitter.onError(e)
        }.onCompletion {
            emitter.onComplete()
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
}