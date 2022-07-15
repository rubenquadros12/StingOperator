package com.ruben.stingoperator

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by Ruben Quadros on 15/07/22
 **/
class StingOperator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null

    fun init(mediaProjection: MediaProjection, width: Int, height: Int, densityDpi: Int) {
        this.mediaRecorder = if (Build.VERSION.SDK_INT >= 31) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncodingBitRate(512 * 1000)
            setVideoFrameRate(30)
            setVideoSize(width, height)
            setOutputFile(getPath())
            prepare()
        }

        this.mediaProjection = mediaProjection.apply {
            virtualDisplay = createVirtualDisplay(
                this.javaClass.simpleName,
                width,
                height,
                densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder?.surface,
                null,
                null
            )
        }
    }

    fun startRecording() {
        this.mediaRecorder?.start()
    }

    fun stopRecording() {
        this.mediaRecorder?.stop()
        this.mediaRecorder?.release()
        this.virtualDisplay?.release()
        this.mediaProjection?.stop()
    }

    private fun getPath(): String {
        val directoryPath = context.getDir("record", Context.MODE_PRIVATE).absolutePath
        return "$directoryPath/example.mp4"
    }
}