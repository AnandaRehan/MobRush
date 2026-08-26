package com.ehan.mobrush.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ehan.mobrush.audio.GameSoundManager
import com.ehan.mobrush.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

  val soundManager = GameSoundManager(application.applicationContext)
  val engine = GameEngine(soundManager)

  val gamePhase = engine.gamePhase
  val skillUpgradeOptions = engine.skillUpgradeOptions
  val itemSelectionOptions = engine.itemSelectionOptions
  val attributeDraftState = engine.attributeDraftState

  var selectedHero: HeroConfig = HeroPresets.Knight
    private set

  private var gameLoopJob: Job? = null

  init {
    startGameLoop()
  }

  fun selectHero(hero: HeroConfig) {
    selectedHero = hero
    engine.selectHero(hero)
  }

  fun startNewGame() {
    engine.selectHero(selectedHero)
    engine.startNewGame()
  }

  fun pauseGame() {
    engine.pauseGame()
  }

  fun resumeGame() {
    engine.resumeGame()
  }

  fun returnToMenu() {
    engine.returnToMenu()
  }

  fun toggleSfx() {
    soundManager.isSfxEnabled = !soundManager.isSfxEnabled
  }

  fun toggleHaptics() {
    soundManager.isHapticsEnabled = !soundManager.isHapticsEnabled
  }

  fun onJoystickInput(dx: Float, dy: Float) {
    engine.updateJoystickInput(dx, dy)
  }

  fun onSelectSkillUpgrade(option: SkillUpgradeOption) {
    engine.selectSkillUpgrade(option)
  }

  fun onSelectItem(item: ItemDefinition) {
    engine.selectItem(item)
  }

  fun onSelectDraftAttribute(option: AttributeOptionType) {
    engine.selectDraftAttribute(option)
  }

  private fun startGameLoop() {
    gameLoopJob?.cancel()
    gameLoopJob = viewModelScope.launch {
      var lastTimeNanos = System.nanoTime()
      while (isActive) {
        val nowNanos = System.nanoTime()
        val dt = ((nowNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
        lastTimeNanos = nowNanos

        engine.update(dt)
        delay(16) // ~60 FPS
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    gameLoopJob?.cancel()
  }
}
