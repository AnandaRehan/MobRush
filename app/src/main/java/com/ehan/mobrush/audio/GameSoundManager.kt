package com.ehan.mobrush.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class GameSoundManager(private val context: Context) {

  private val audioScope = CoroutineScope(Dispatchers.Default)
  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  val isSfxEnabledFlow = kotlinx.coroutines.flow.MutableStateFlow(true)
  val isHapticsEnabledFlow = kotlinx.coroutines.flow.MutableStateFlow(true)

  var isSfxEnabled: Boolean
    get() = isSfxEnabledFlow.value
    set(value) { isSfxEnabledFlow.value = value }

  var isHapticsEnabled: Boolean
    get() = isHapticsEnabledFlow.value
    set(value) { isHapticsEnabledFlow.value = value }

  private val sampleRate = 22050

  private fun playTone(
    frequencies: FloatArray,
    durationSec: Float,
    waveType: String = "sine",
    volume: Float = 0.5f
  ) {
    if (!isSfxEnabled) return
    audioScope.launch {
      try {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        val stepSamples = (totalSamples / frequencies.size.coerceAtLeast(1)).coerceAtLeast(1)

        for (i in 0 until totalSamples) {
          val freqIdx = (i / stepSamples).coerceAtMost(frequencies.size - 1)
          val freq = frequencies[freqIdx]
          val time = i.toDouble() / sampleRate
          val envelope = when {
            i < totalSamples * 0.1 -> i / (totalSamples * 0.1)
            i > totalSamples * 0.6 -> (totalSamples - i) / (totalSamples * 0.4)
            else -> 1.0
          }

          val wave = when (waveType) {
            "square" -> if (sin(2.0 * PI * freq * time) >= 0) 1.0 else -1.0
            "triangle" -> {
              val t = (freq * time) % 1.0
              if (t < 0.5) 4.0 * t - 1.0 else 3.0 - 4.0 * t
            }
            "noise" -> (Math.random() * 2.0 - 1.0)
            else -> sin(2.0 * PI * freq * time)
          }

          val sampleVal = (wave * envelope * volume * Short.MAX_VALUE).toInt().coerceIn(
            Short.MIN_VALUE.toInt(),
            Short.MAX_VALUE.toInt()
          )
          buffer[i] = sampleVal.toShort()
        }

        val track = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(buffer.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        audioScope.launch {
          kotlinx.coroutines.delay((durationSec * 1000).toLong() + 100)
          try {
            track.stop()
            track.release()
          } catch (_: Exception) {}
        }
      } catch (_: Exception) {}
    }
  }

  fun playSlash() {
    playTone(floatArrayOf(350f, 220f, 120f), 0.09f, "triangle", 0.45f)
  }

  fun playArrowShot() {
    playTone(floatArrayOf(580f, 880f, 1200f), 0.07f, "sine", 0.4f)
  }

  fun playMobHit() {
    playTone(floatArrayOf(180f, 100f), 0.06f, "triangle", 0.35f)
  }

  fun playMobDeath() {
    playTone(floatArrayOf(220f, 150f, 80f), 0.12f, "noise", 0.4f)
    triggerHaptic(20)
  }

  fun playGemPickup() {
    playTone(floatArrayOf(660f, 990f), 0.08f, "sine", 0.3f)
  }

  fun playLevelUp() {
    playTone(floatArrayOf(440f, 554f, 659f, 880f), 0.35f, "square", 0.5f)
    triggerHaptic(60)
  }

  fun playItemFanfare() {
    playTone(floatArrayOf(523f, 659f, 784f, 1046f), 0.45f, "sine", 0.6f)
    triggerHaptic(80)
  }

  fun playPlayerHurt() {
    playTone(floatArrayOf(140f, 80f), 0.14f, "square", 0.6f)
    triggerHaptic(50)
  }

  fun playGameOver() {
    playTone(floatArrayOf(300f, 240f, 180f, 110f), 0.6f, "triangle", 0.6f)
    triggerHaptic(120)
  }

  private fun triggerHaptic(durationMs: Long) {
    if (!isHapticsEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }
}
