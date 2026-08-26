package com.ehan.mobrush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ehan.mobrush.game.GameViewModel
import com.ehan.mobrush.model.GamePhase
import com.ehan.mobrush.model.HeroPresets
import com.ehan.mobrush.ui.GameScreen
import com.ehan.mobrush.ui.MainMenuScreen
import com.ehan.mobrush.ui.theme.DarkBackground
import com.ehan.mobrush.ui.theme.MobRushTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MobRushTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = DarkBackground
        ) {
          MobRushApp()
        }
      }
    }
  }
}

@Composable
fun MobRushApp(
  viewModel: GameViewModel = viewModel()
) {
  val gamePhase by viewModel.gamePhase.collectAsState()

  when (gamePhase) {
    GamePhase.MAIN_MENU -> {
      MainMenuScreen(
        selectedHero = viewModel.selectedHero,
        onSelectHero = { hero -> viewModel.selectHero(hero) },
        onStartGame = { viewModel.startNewGame() },
        isSfxEnabled = viewModel.soundManager.isSfxEnabled,
        onToggleSfx = { viewModel.toggleSfx() },
        isHapticsEnabled = viewModel.soundManager.isHapticsEnabled,
        onToggleHaptics = { viewModel.toggleHaptics() }
      )
    }
    else -> {
      GameScreen(viewModel = viewModel)
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMenuPreview() {
  MobRushTheme {
    MainMenuScreen(
      selectedHero = HeroPresets.Knight,
      onSelectHero = {},
      onStartGame = {},
      isSfxEnabled = true,
      onToggleSfx = {},
      isHapticsEnabled = true,
      onToggleHaptics = {}
    )
  }
}
