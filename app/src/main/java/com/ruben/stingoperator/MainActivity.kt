package com.ruben.stingoperator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ruben.stingoperator.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var stingOperator: StingOperator

    private var projectionManager: MediaProjectionManager? = null

    private lateinit var binding: ActivityMainBinding

    private val audioPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            projectionManager?.let {
                projectionResult.launch(it.createScreenCaptureIntent())
            }
        }
    }

    private val projectionResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            projectionManager?.let {
                stingOperator.init(
                    mediaProjection = it.getMediaProjection(result.resultCode, result.data ?: Intent()),
                    width = resources.displayMetrics.widthPixels,
                    height = resources.displayMetrics.heightPixels,
                    densityDpi = resources.configuration.densityDpi
                )
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
        askPermission()
        setupRecording()
    }

    private fun askPermission() {
        audioPermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun setupRecording() {
        binding.startRecord.setOnClickListener {
            stingOperator.startRecording()
        }

        binding.stopRecord.setOnClickListener {
            stingOperator.stopRecording()
        }
    }
}