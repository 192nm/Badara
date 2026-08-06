package com.badara.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.badara.mobile.api.ApiProvider
import com.badara.mobile.api.FileItem
import com.badara.mobile.api.LoginRequest
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

    fun login(username: String, password: String) = viewModelScope.launch {
        runCatching {
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
        runCatching {
            syncStatus = "SYNCING"
            ApiProvider.api.files("Bearer " + currentToken)
        }.onSuccess {
            files = it
            syncStatus = "SYNCED"
        }.onFailure {
            syncStatus = "FAILED"
            errorMessage = it.message
        }
    }
}
