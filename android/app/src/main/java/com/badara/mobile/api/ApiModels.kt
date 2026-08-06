package com.badara.mobile.api

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val accessToken: String)

data class FileItem(
    val fileKey: String,
    val filename: String,
    val contentType: String,
    val size: Long,
    val createdAt: String,
    val syncStatus: String
)

data class ShareRequest(val expiresInHours: Int = 24, val maxDownloads: Int = 20)
data class ShareResponse(val token: String, val expiresAt: String, val maxDownloads: Int)
