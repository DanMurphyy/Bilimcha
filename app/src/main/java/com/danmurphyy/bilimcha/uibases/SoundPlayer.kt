package com.danmurphyy.bilimcha.uibases

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

object SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context, soundPath: String) {
        try {
            stopSound()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                
                if (soundPath.startsWith("http")) {
                    setDataSource(soundPath)
                } else {
                    val assetFileDescriptor = context.assets.openFd(soundPath)
                    setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length
                    )
                    assetFileDescriptor.close()
                }
                
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener { 
                    stopSound() 
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("SoundPlayer", "Error playing sound: $what, $extra")
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("SoundPlayer", "Failed to play sound", e)
        }
    }

    fun stopSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }
}
