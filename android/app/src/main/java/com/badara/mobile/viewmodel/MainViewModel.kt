package com.badara.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.badara.mobile.api.ApiProvider
import com.badara.mobile.api.FileItem
import com.badara.mobile.api.LoginRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var token by mutableStateOf<String?>(null)
        private set

    var files by mutableStateOf<List<FileItem>>(emptyList())
        private set

    var syncStatus by mutableStateOf("Idle")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var pendingActions by mutableStateOf<List<String>>(emptyList())
        private set

    fun login(username: String, password: String) = viewModelScope.launch {
        runCatchingWithRetry {
            ApiProvider.api.login(LoginRequest(username, password)).accessToken
        }.onSuccess {
            token = it
            refreshFiles()
        }.onFailure {
            errorMessage = it.message
        }
    }

    fun refreshFiles() = viewModelScope.launch {
        val currentToken = token ?: return@launch
        runCatchingWithRetry {
            syncStatus = "SYNCING"
            ApiProvider.api.files("Bearer " + currentToken)
        }.onSuccess {
            files = it
            syncStatus = "SYNCED"
        }.onFailure {
            syncStatus = "FAILED"
            errorMessage = it.message
            pendingActions = pendingActions + "refreshFiles"
        }
    }

    fun consumePendingActions() = viewModelScope.launch {
        if (pendingActions.isEmpty()) return@launch
        pendingActions = emptyList()
        refreshFiles()
    }

    private suspend fun <T> runCatchingWithRetry(maxRetry: Int = 2, block: suspend () -> T): Result<T> {
        var lastError: Throwable? = null
        repeat(maxRetry + 1) { attempt ->
            try {
                return Result.success(block())
            } catch (error: Throwable) {
                lastError = error
                delay((attempt + 1) * 400L)
            }
        }
        return Result.failure(lastError ?: IllegalStateException("Unknown error"))
    }
}
