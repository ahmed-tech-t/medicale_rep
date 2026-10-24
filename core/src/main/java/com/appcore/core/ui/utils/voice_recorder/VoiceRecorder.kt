package com.appcore.core.ui.utils.voice_recorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File

/** Records AAC audio (.m4a) from the microphone through a cache file and hands it back as bytes. */
class VoiceRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var file: File? = null

    /** Returns false when the recorder could not start (busy mic, missing permission, ...). */
    fun start(): Boolean {
        stop()
        val target = File.createTempFile("VOICE_", ".m4a", context.cacheDir)
        val newRecorder = createMediaRecorder()
        return try {
            newRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            newRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            newRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            newRecorder.setAudioChannels(1)
            newRecorder.setAudioSamplingRate(SAMPLE_RATE)
            newRecorder.setAudioEncodingBitRate(BIT_RATE)
            newRecorder.setOutputFile(target.absolutePath)
            newRecorder.prepare()
            newRecorder.start()
            recorder = newRecorder
            file = target
            true
        } catch (e: Exception) {
            newRecorder.release()
            target.delete()
            false
        }
    }

    /** Returns the recorded bytes, or null when nothing was recording or the clip was too short. */
    fun stop(): ByteArray? {
        val active = recorder ?: return null
        val target = file
        recorder = null
        file = null
        val stopped = try {
            active.stop()
            true
        } catch (e: Exception) {
            false
        } finally {
            active.release()
        }
        val bytes = if (stopped) target?.readBytes() else null
        target?.delete()
        return bytes
    }

    private fun createMediaRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

    companion object {
        const val MAX_DURATION_MILLIS = 120_000L
        private const val SAMPLE_RATE = 44_100
        private const val BIT_RATE = 64_000
    }
}

fun Context.hasRecordAudioPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED

@Composable
fun rememberVoiceRecorder(): VoiceRecorder {
    val context = LocalContext.current
    val recorder = remember { VoiceRecorder(context.applicationContext) }
    DisposableEffect(recorder) { onDispose { recorder.stop() } }
    return recorder
}

@Composable
fun rememberRecordAudioPermissionLauncher(
    onResult: (granted: Boolean) -> Unit,
): ManagedActivityResultLauncher<String, Boolean> =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission(), onResult)
