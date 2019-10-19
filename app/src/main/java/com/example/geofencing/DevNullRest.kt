package com.example.geofencing

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class DevNullRest {
    fun sendToNull(string:String) {
        DevNullRequst().execute(string)
    }
}

class DevNullRequst: AsyncTask<String, Boolean, Boolean>() {
    override fun doInBackground(vararg p0: String): Boolean {
        with(URL("http://devnull-as-a-service.com/dev/null").openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream());
            wr.write(p0[0]);
            wr.flush();

            println("URL : $url")
            if (responseCode != 200) {
                return false
            }

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
            }
        }
        return true
    }

}