package com.ehan.mobrush

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.ehan.mobrush.model.*
import com.ehan.mobrush.ui.ItemSelectDialog
import com.ehan.mobrush.ui.LevelUpDialog
import com.ehan.mobrush.ui.MainMenuScreen
import com.ehan.mobrush.ui.theme.MobRushTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "+land", sdk = [34])
class MobRushScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testMainMenuScreenshot() {
    composeTestRule.setContent {
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

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/main_menu.png")
  }

  @Test
  fun testLevelUpDialogScreenshot() {
    val options = listOf(
      SkillUpgradeOption(
        type = PassiveSkillType.ATTACK_POWER,
        isNew = false,
        currentLevel = 2,
        targetLevel = 3,
        description = "+45% Total Kerusakan Serangan"
      ),
      SkillUpgradeOption(
        type = PassiveSkillType.ATTACK_SPEED,
        isNew = true,
        currentLevel = 0,
        targetLevel = 1,
        description = "+12% Frekuensi Kecepatan Menyerang"
      ),
      SkillUpgradeOption(
        type = PassiveSkillType.VITALITY,
        isNew = true,
        currentLevel = 0,
        targetLevel = 1,
        description = "+25 Max HP & Pulihkan 20 HP Langsung"
      )
    )

    composeTestRule.setContent {
      MobRushTheme {
        LevelUpDialog(
          currentLevel = 3,
          options = options,
          onSelectOption = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/level_up_dialog.png")
  }

  @Test
  fun testItemSelectMilestoneScreenshot() {
    val items = listOf(
      GameItems.Pemulihan,
      GameItems.KristalBerkat
    )

    composeTestRule.setContent {
      MobRushTheme {
        ItemSelectDialog(
          currentLevel = 5,
          availableItems = items,
          onSelectItem = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/item_select_milestone.png")
  }
}
