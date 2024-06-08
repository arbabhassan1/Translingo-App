package com.example.translationapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.spinner)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.dropdown_items, // Reference to the string array
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val translateBtn: Button = findViewById(R.id.translateBtn)
        translateBtn.setOnClickListener {
            translateText()
        }
    }

    private fun translateText() {
        val textInput: EditText = findViewById(R.id.textInput)
        val spinner: Spinner = findViewById(R.id.spinner)
        val selectedLanguage = spinner.selectedItem.toString()
        val textToTranslate = textInput.text.toString()

        val languageCode = when (selectedLanguage) {
            "English" -> "en"
            "Urdu" -> "ur"
            "Arabic" -> "ar"
            "Hindi" -> "hi"
            "French" -> "fr"
            else -> "en" // Default to English if language is not found
        }

        val requestBody = JSONObject().apply {
            put("content", textToTranslate)
            put("language", languageCode)
        }.toString().toRequestBody("application/json".toMediaType())

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://translate-api-alpha.vercel.app/translate")
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            // Handle response
            if (response.isSuccessful && responseBody != null) {
                // Process the response here
                // For example, you can update UI with the translated text
                val translatedText = JSONObject(responseBody).getString("translated_text")
                runOnUiThread {
                    // Update UI with translated text
                    val resultTxt=findViewById<TextView>(R.id.resultShow);
                    resultTxt.text=translatedText;
                }
            } else {
                // Handle unsuccessful response
            }
        }
    }
}
