package com.example.krasnale

import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.Call
import java.io.File
import java.util.*

//object RequestInterceptor : Interceptor{
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        println("Outgoing request to ${request.url()}")
//        return chain.proceed(request)
//    }
//}
//object AuthInterceptor : Interceptor{
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val requestWithHeader = chain.request()
//            .newBuilder()
//            .header(
//                "Authorization", UUID.randomUUID().toString()
//            ).build()
//        return chain.proceed(requestWithHeader)
//    }
//}

interface MyApi {
    @Multipart
    @POST("image/upload") //print
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("desc") desc: RequestBody
        //@Part image: File
    ): Call<UploaadResponse>

    companion object{
        private val okHttpClient = OkHttpClient()
            .newBuilder()
//            .addInterceptor(AuthInterceptor)
//            .addInterceptor(RequestInterceptor)
            .build()
        operator fun invoke(): MyApi{
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://49d493cd-74a7-47c4-a2e7-ad4022224dd1.mock.pstmn.io") //http:/192.168.0.151:3000/  https://krasnalewroclawskie.azurewebsites.net/
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)


        }
    }


}
