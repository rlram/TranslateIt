package com.example.translateit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerInput: Spinner
    private lateinit var spinnerOutput: Spinner
    private lateinit var etTextInputLang: EditText
    private lateinit var tvOutputLang: TextView
    private lateinit var btnTranslate: Button

    private var inputLang = arrayOf("From", "English", "Afrikaans", "Arabic", "Bengali", "Hindi")
    private var outputLang = arrayOf("To", "English", "Afrikaans", "Arabic", "Bengali", "Hindi")
    var fromLangCode = 0
    var toLangCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        spinnerInput = findViewById(R.id.spinnerInput)
        spinnerOutput = findViewById(R.id.spinnerOutput)
        etTextInputLang = findViewById(R.id.etTextInputLang)
        tvOutputLang = findViewById(R.id.tvOutputLang)
        btnTranslate = findViewById(R.id.btnTranslate)

        spinnerInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                fromLangCode = getLangCode(inputLang[p2])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        val adapterInput: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, inputLang)
        adapterInput.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInput.adapter = adapterInput


        spinnerOutput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                toLangCode = getLangCode(outputLang[p2])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val adapterOutput: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, outputLang)
        adapterOutput.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOutput.adapter = adapterOutput


        btnTranslate.setOnClickListener {
            val text = etTextInputLang.text.toString()
            if (text.isEmpty()) etTextInputLang.error = "Please enter your text to translate"
            if (fromLangCode == 0) {
                Toast.makeText(
                    applicationContext,
                    "Please select source language",
                    Toast.LENGTH_LONG
                ).show()
            }
            if (toLangCode == 0) {
                Toast.makeText(
                    applicationContext,
                    "Please select the language to make translation",
                    Toast.LENGTH_LONG
                ).show()
            }

            if (text.isNotEmpty() && fromLangCode != 0 && toLangCode != 0) {
                translateText(fromLangCode, toLangCode, text)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun translateText(fromLangCode: Int, toLangCode: Int, text: String) {
        tvOutputLang.text = "Please wait..."
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(fromLangCode)
            .setTargetLanguage(toLangCode)
            .build()

        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        val conditions = FirebaseModelDownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                tvOutputLang.text = "Translating..."
                translator.translate(text)
                    .addOnSuccessListener {
                        tvOutputLang.setText(it.toString())
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext,
                            "Failed ${it.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
    }

    private fun getLangCode(s: String): Int {
        var langCode = 0
        when (s) {
            "English" -> langCode = FirebaseTranslateLanguage.EN
            "Afrikaans" -> langCode = FirebaseTranslateLanguage.AF
            "Arabic" -> langCode = FirebaseTranslateLanguage.AR
            "Bengali" -> langCode = FirebaseTranslateLanguage.BN
            "Hindi" -> langCode = FirebaseTranslateLanguage.HI
            else -> langCode = 0
        }
        return langCode
    }

}