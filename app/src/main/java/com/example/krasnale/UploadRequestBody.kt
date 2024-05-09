package com.example.krasnale

import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import android.os.Handler

class UploadRequestBody (
    private val image: File, //file
    private val contentType: String,
    private val callback: UploadCallback
): RequestBody() {
    private val DEFAULT_BUFFER_SIZE = 2048

    override fun contentType() = MediaType.parse("$contentType/*")

    override fun contentLength() = image.length() //
    override fun writeTo(sink: BufferedSink) {
        val length = image.length() //
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(image) //
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also {
                    read = it
                } != -1) {
                uploaded += read
                sink.write(buffer, 0, read)
            }

        }
    }


    interface UploadCallback { // to do progresBara - można wywalić
        fun onProgresUpdate(percentage: Int)
    }

    inner class ProgressUpdater( //cała ta klasa też nie jest niezbędna
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {
        override fun run() {
            callback.onProgresUpdate((100*uploaded/total).toInt())
        }
    }
}
