package com.example.springbreakchooser

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.springbreakchooser.databinding.ActivityMainBinding
import android.speech.RecognizerIntent
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import java.util.*
import kotlin.math.sqrt
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.app.Activity



private const val LOG_TAG = "MainActivity"

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener {
    private lateinit var uiBinding: ActivityMainBinding
    private lateinit var speechEngine: TextToSpeech
    private var chosenLanguage = ""
    private val languageOptions =
        arrayOf("Choose Language", "English", "French", "Spanish", "Italian")
    private val languageCodes = mapOf(
        "English" to "en-US",
        "French" to "fr-FR",
        "Spanish" to "es-ES",
        "Italian" to "it-IT"
    )
    private var motionSensorManager: SensorManager? = null
    private var shakeAcceleration = 0f
    private var currentShakeAcceleration = 0f
    private var previousShakeAcceleration = 0f

    private val notableLocations = mapOf(
        "English" to listOf("Stonehenge, England", "Times Square, New York, USA"),
        "French" to listOf("Louvre Museum, Paris, France", "Mont Blanc, Chamonix, France"),
        "Spanish" to listOf("Prado Museum, Madrid, Spain", "Chichen Itza, Yucatan, Mexico"),
        "Italian" to listOf("Venice Canals, Venice, Italy", "Duomo Cathedral, Milan, Italy")
    )

    private var locationUri: Uri =
        Uri.parse("geo:0,0?q=${Uri.encode("Times Square, New York, USA")}")


    private val speechActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val speechData: Intent? = result.data
                val speechResults =
                    speechData?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                uiBinding.userInput.setText(speechResults?.get(0) ?: "")
            }
        }

    private val greetingsMap = mapOf(
        "English" to "Hello",
        "French" to "Bonjour",
        "Spanish" to "Hola",
        "Italian" to "Ciao"
    )

    private val motionSensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val xAxis = event.values[0]
            val yAxis = event.values[1]
            val zAxis = event.values[2]
            previousShakeAcceleration = currentShakeAcceleration
            currentShakeAcceleration =
                sqrt((xAxis * xAxis + yAxis * yAxis + zAxis * zAxis).toDouble()).toFloat()
            val accelerationDifference: Float = currentShakeAcceleration - previousShakeAcceleration
            shakeAcceleration = shakeAcceleration * 0.9f + accelerationDifference

            if (shakeAcceleration > 8 && uiBinding.userInput.text.toString().isNotBlank()) {
                Log.d(LOG_TAG, "Shake event detected")
                val mapIntent = Intent(Intent.ACTION_VIEW, locationUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                }
                announceGreeting()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(uiBinding.root)
        initializeActivityComponents()
    }

    override fun onResume() {
        super.onResume()
        motionSensorManager?.registerListener(
            motionSensorListener,
            motionSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        motionSensorManager?.unregisterListener(motionSensorListener)
        super.onPause()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(applicationContext, languageOptions[position], Toast.LENGTH_LONG).show()
        chosenLanguage = languageOptions[position]
        Log.d(LOG_TAG, "Selected language: $chosenLanguage")
        changeLocationBasedOnLanguage(chosenLanguage)

        if (position > 0) {
            initiateSpeechRecognition(chosenLanguage)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(LOG_TAG, "Speech engine initialized.")
        } else {
            Log.e(LOG_TAG, "Failed to initialize speech engine.")
        }
    }

    private fun initializeActivityComponents() {
        uiBinding.userInput.isEnabled = false

        speechEngine = TextToSpeech(this, this)

        motionSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        motionSensorManager?.registerListener(
            motionSensorListener,
            motionSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        shakeAcceleration = 10f
        currentShakeAcceleration = SensorManager.GRAVITY_EARTH
        previousShakeAcceleration = SensorManager.GRAVITY_EARTH

        setupLanguageSpinner()
    }

    private fun setupLanguageSpinner() {
        uiBinding.languageSpinner.onItemSelectedListener = this
        val adapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languageOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        uiBinding.languageSpinner.adapter = adapter
    }

    private fun changeLocationBasedOnLanguage(language: String) {
        val locations = notableLocations[language] ?: return
        val randomLocation = locations.random()
        locationUri = Uri.parse("geo:0,0?q=${Uri.encode(randomLocation)}")
    }

    private fun initiateSpeechRecognition(language: String) {
        val languageTag = languageCodes[language] ?: Locale.getDefault().toLanguageTag()

        val promptId = when (language) {
            "English" -> R.string.speak_now
            "French" -> R.string.speak_now
            "Spanish" -> R.string.speak_now
            "Italian" -> R.string.speak_now
            else -> R.string.speak_now
        }
        val recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(promptId))
        }
        speechActivityResultLauncher.launch(recognitionIntent)
    }


    private fun announceGreeting() {
        val greetingLanguageTag = languageCodes[chosenLanguage] ?: return
        val locale = Locale.forLanguageTag(greetingLanguageTag)
        if (speechEngine.setLanguage(locale) == TextToSpeech.LANG_MISSING_DATA || speechEngine.setLanguage(
                locale
            ) == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            Log.e(LOG_TAG, "This language is not supported.")
            return
        }
        val greetingText = greetingsMap[chosenLanguage] ?: "Hello"
        speechEngine.speak(greetingText, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
