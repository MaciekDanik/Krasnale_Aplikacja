package com.example.krasnale

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("image/upload")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part ("image_name") img_name: RequestBody
    ): Call<ImageUploadResponse>
}