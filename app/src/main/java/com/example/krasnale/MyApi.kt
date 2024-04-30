package com.example.krasnale

import android.media.Image
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST

interface MyApi {
    @Multipart
    @POST("")
    fun uploadImage(
        @Part image : MultipartBody.Part,
        @Part("desc") desc: RequestBody
    )

    companion object{
        operator fun invoke(): MyApi{
            return Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)

        }
    }
}