package com.appcore.core.ui.utils.voice_recorder

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.io.File

/** What the UI needs to show and control a voice note: [toggle] plays or pauses it. */
data class VoicePlayerState(
    val isPlaying: Boolean,
    val durationMillis: Int,
    val toggle: () -> Unit,
)

/** Plays the clip [bytes] (as recorded by [VoiceRecorder]); the player is released when the clip changes or leaves. */
@Composable
fun rememberVoicePlayer(bytes: ByteArray): VoicePlayerState {
    val context = LocalContext.current.applicationContext
    var isPlaying by remember(bytes) { mutableStateOf(false) }
    val player = remember(bytes) { createPlayer(context, bytes) { isPlaying = false } }
    DisposableEffect(player) { onDispose { player?.release() } }
    return VoicePlayerState(
        isPlaying = isPlaying,
        durationMillis = player?.duration ?: 0,
        toggle = {
            if (player != null) {
                if (player.isPlaying) player.pause() else player.start()
                isPlaying = player.isPlaying
            }
        },
    )
}

/** Null when the clip cannot be played (corrupt or empty). */
private fun createPlayer(context: Context, bytes: ByteArray, onCompleted: () -> Unit): MediaPlayer? {
    val file = File.createTempFile("PLAY_", ".m4a", context.cacheDir)
    return try {
        file.writeBytes(bytes)
        MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            setOnCompletionListener { onCompleted() }
        }
    } catch (e: Exception) {
        null
    } finally {
        file.delete()
    }
}
