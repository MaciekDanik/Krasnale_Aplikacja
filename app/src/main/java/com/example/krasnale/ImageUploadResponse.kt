package com.example.krasnale

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ImageUploadResponse {
    @SerializedName("message")
    @Expose
    private var message: String? = null

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

}