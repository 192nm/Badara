package com.badara.mobile.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface BadaraApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @GET("/api/files")
    suspend fun files(@Header("Authorization") bearerToken: String): List<FileItem>

    @Multipart
    @POST("/api/files/upload")
    suspend fun upload(
        @Header("Authorization") bearerToken: String,
        @Part file: MultipartBody.Part
    ): FileItem

    @POST("/api/files/{fileKey}/share")
    suspend fun share(
        @Header("Authorization") bearerToken: String,
        @Path("fileKey") fileKey: String,
        @Body request: ShareRequest = ShareRequest()
    ): ShareResponse
}
