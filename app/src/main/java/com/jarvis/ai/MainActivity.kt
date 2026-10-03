package com.jarvis.ai

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.*
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var status: TextView
    private lateinit var chat: TextView
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 32, 28, 20)
            setBackgroundColor(Color.rgb(5, 7, 11))
        }

        val title = TextView(this).apply {
            text = "J A R V I S"
            textSize = 27f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        root.addView(title, LinearLayout.LayoutParams(-1, 70))

        status = TextView(this).apply {
            text = "● ONLINE  •  V1"
            textSize = 13f
            setTextColor(Color.rgb(100, 210, 255))
            gravity = Gravity.CENTER
        }
        root.addView(status, LinearLayout.LayoutParams(-1, 45))

        chat = TextView(this).apply {
            text = "JARVIS\n\nGood evening. I am ready.\nAsk me something or use the microphone."
            textSize = 17f
            setTextColor(Color.WHITE)
            setPadding(20, 25, 20, 25)
        }
        val scroll = ScrollView(this)
        scroll.addView(chat)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        input = EditText(this).apply {
            hint = "Message JARVIS..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            setSingleLine(true)
        }
        root.addView(input, LinearLayout.LayoutParams(-1, 60))

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val send = Button(this).apply {
            text = "SEND"
            setOnClickListener { respond(input.text.toString()) }
        }
        val mic = Button(this).apply {
            text = "🎙"
            setOnClickListener { startVoice() }
        }
        row.addView(send, LinearLayout.LayoutParams(0, 65, 1f))
        row.addView(mic, LinearLayout.LayoutParams(0, 65, 1f))
        root.addView(row)
        setContentView(root)
    }

    private fun respond(text: String) {
        if (text.isBlank()) return
        val answer = localResponse(text)
        chat.text = "YOU\n$text\n\nJARVIS\n$answer"
        tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "jarvis")
        input.text.clear()
    }

    private fun localResponse(text: String): String {
        val q = text.lowercase(Locale.getDefault())
        return when {
            q.contains("hello") || q.contains("hi") -> "Hello. I am JARVIS V1. My core application is online."
            q.contains("calculate") || q.contains("math") -> "The calculation engine is reserved for the next module. V1 is ready for integration."
            q.contains("who are you") -> "I am your JARVIS application. V1 provides the interface, voice output and a foundation for memory, tools and AI integration."
            else -> "I received: \"$text\". The AI backend will be connected in the next build."
        }
    }

    private fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS")
        }
        try { startActivityForResult(intent, 9001) }
        catch (_: Exception) { status.text = "Voice recognition unavailable" }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9001 && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            input.setText(results?.firstOrNull() ?: "")
            if (!input.text.isNullOrBlank()) respond(input.text.toString())
        }
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setSpeechRate(0.95f)
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
