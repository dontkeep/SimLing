package com.doni.simling.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Composable
fun <T> liveDataAsState(liveData: LiveData<T>, initial: T? = null): State<T?> {
    val state = remember { mutableStateOf(initial) }
    DisposableEffect(liveData) {
        val observer = Observer<T> { value -> state.value = value }
        liveData.observeForever(observer)
        onDispose { liveData.removeObserver(observer) }
    }
    return state
}