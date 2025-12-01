package com.voicecalc.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.voicecalc.R
import com.voicecalc.databinding.ActivityMainBinding
import com.voicecalc.model.ArabicDialect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    private lateinit var inputAdapter: InputAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            Toast.makeText(
                this,
                getString(R.string.permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupDialectSpinner()
        setupButtons()
        observeViewModel()
        checkSpeechRecognitionAvailability()
    }

    private fun setupRecyclerViews() {
        // Input list
        inputAdapter = InputAdapter(
            onItemUpdated = { id, newValue ->
                viewModel.updateInputItem(id, newValue)
            },
            onItemDeleted = { id ->
                viewModel.deleteInputItem(id)
            }
        )
        binding.recyclerViewInputs.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = inputAdapter
        }

        // History list
        historyAdapter = HistoryAdapter()
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }

    private fun setupDialectSpinner() {
        val dialects = ArabicDialect.values()
        val dialectNames = dialects.map { it.displayName }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            dialectNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDialect.adapter = adapter

        binding.spinnerDialect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setDialect(dialects[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            checkPermissionAndStart()
        }

        binding.btnStop.setOnClickListener {
            viewModel.stopListening()
        }

        binding.btnClear.setOnClickListener {
            viewModel.clearAll()
        }

        binding.btnCalculate.setOnClickListener {
            viewModel.calculateResult()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Input list
            viewModel.inputList.collect { items ->
                inputAdapter.submitList(items)
            }
        }

        lifecycleScope.launch {
            // Listening status
            viewModel.isListening.collect { isListening ->
                updateListeningStatus(isListening)
            }
        }

        lifecycleScope.launch {
            // Last heard text
            viewModel.lastHeardText.collect { text ->
                if (text != null) {
                    binding.tvLastHeard.text = "${getString(R.string.last_heard)} \"$text\" ✓"
                } else {
                    binding.tvLastHeard.text = ""
                }
            }
        }

        lifecycleScope.launch {
            // Calculation result
            viewModel.calculationResult.collect { result ->
                if (result != null) {
                    binding.cardResult.visibility = View.VISIBLE
                    binding.tvResult.text = "📊 ${getString(R.string.result)} = ${result.getFormattedResult()}"
                } else {
                    binding.cardResult.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            // History
            viewModel.historyList.collect { history ->
                historyAdapter.submitList(history)
            }
        }

        lifecycleScope.launch {
            // Errors
            viewModel.error.collect { error ->
                if (error != null) {
                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }

    private fun updateListeningStatus(isListening: Boolean) {
        if (isListening) {
            binding.tvListeningStatus.text = "🎤 ${getString(R.string.listening)}"
            binding.tvListeningStatus.setTextColor(
                ContextCompat.getColor(this, R.color.listening_active)
            )
            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true
        } else {
            binding.tvListeningStatus.text = getString(R.string.stopped)
            binding.tvListeningStatus.setTextColor(
                ContextCompat.getColor(this, R.color.listening_stopped)
            )
            binding.btnStart.isEnabled = true
            binding.btnStop.isEnabled = false
        }
    }

    private fun checkPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.startListening()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(
                    this,
                    getString(R.string.permission_required),
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun checkSpeechRecognitionAvailability() {
        if (!CalculatorViewModel.isSpeechRecognitionAvailable(application)) {
            Toast.makeText(
                this,
                getString(R.string.speech_not_available),
                Toast.LENGTH_LONG
            ).show()
            binding.btnStart.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopListening()
    }
}
