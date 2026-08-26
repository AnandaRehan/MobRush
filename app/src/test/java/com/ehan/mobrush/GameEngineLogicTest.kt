package com.ehan.mobrush

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ehan.mobrush.audio.GameSoundManager
import com.ehan.mobrush.game.GameEngine
import com.ehan.mobrush.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GameEngineLogicTest {

  private lateinit var context: Context
  private lateinit var soundManager: GameSoundManager
  private lateinit var engine: GameEngine

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    soundManager = GameSoundManager(context)
    soundManager.isSfxEnabled = false
    soundManager.isHapticsEnabled = false
    engine = GameEngine(soundManager)
  }

  @Test
  fun testHeroSelectionAndStart() {
    engine.selectHero(HeroPresets.Archer)
    assertEquals(HeroId.ARCHER, engine.currentHero.id)

    engine.startNewGame()
    assertEquals(GamePhase.PLAYING, engine.gamePhase.value)
    assertEquals(1, engine.level)
    assertEquals(HeroPresets.Archer.baseMaxHp, engine.maxHp, 0.1f)
    assertEquals(HeroPresets.Archer.baseMaxHp, engine.currentHp, 0.1f)
  }

  @Test
  fun testRegularLevelUpPassiveSkillSelection() {
    engine.selectHero(HeroPresets.Knight)
    engine.startNewGame()

    // Level 1 -> Add XP to reach Level 2 (Regular level)
    val reqXp = engine.requiredXp
    val gem = ExpGem(
      id = 999L,
      x = engine.playerX,
      y = engine.playerY,
      xpValue = reqXp + 5,
      radius = 10f,
      color = androidx.compose.ui.graphics.Color.Green,
      isBeingMagnetized = true
    )
    engine.expGems.add(gem)
    engine.update(0.1f)

    assertEquals(2, engine.level)
    assertEquals(GamePhase.LEVEL_UP_SKILL_SELECT, engine.gamePhase.value)
    assertTrue(engine.skillUpgradeOptions.value.isNotEmpty())

    val firstOption = engine.skillUpgradeOptions.value.first()
    engine.selectSkillUpgrade(firstOption)

    assertEquals(GamePhase.PLAYING, engine.gamePhase.value)
    assertEquals(1, engine.activePassives[firstOption.type])
  }

  @Test
  fun testMilestoneLevel5ItemSelectionAndPemulihanRegen() {
    engine.selectHero(HeroPresets.Knight)
    engine.startNewGame()

    // Force engine level to 4, then level up to 5 (Milestone level)
    engine.level = 4
    engine.requiredXp = 50
    val gem = ExpGem(
      id = 888L,
      x = engine.playerX,
      y = engine.playerY,
      xpValue = 60,
      radius = 10f,
      color = androidx.compose.ui.graphics.Color.Yellow,
      isBeingMagnetized = true
    )
    engine.expGems.add(gem)
    engine.update(0.1f)

    assertEquals(5, engine.level)
    assertEquals(GamePhase.ITEM_SELECT, engine.gamePhase.value)
    assertEquals(2, engine.itemSelectionOptions.value.size) // Both Pemulihan and Kristal Berkat available

    // Choose Pemulihan
    val pemulihan = engine.itemSelectionOptions.value.first { it.id == ItemId.PEMULIHAN }
    val oldMaxHp = engine.maxHp
    engine.selectItem(pemulihan)

    assertEquals(GamePhase.PLAYING, engine.gamePhase.value)
    assertTrue(engine.acquiredItems.contains(ItemId.PEMULIHAN))
    assertEquals(oldMaxHp + 40f, engine.maxHp, 0.1f)

    // Damage player to test 10% regen per second
    engine.currentHp = 50f
    repeat(25) {
      engine.update(0.045f) // advance > 1 sec in game time steps
    }
    val expectedHeal = engine.maxHp * 0.10f
    assertEquals(50f + expectedHeal, engine.currentHp, 0.1f)

    // Advance to next milestone level 10 -> Pemulihan should NOT be present anymore (1x limit)
    engine.level = 9
    engine.requiredXp = 50
    val gem2 = ExpGem(
      id = 777L,
      x = engine.playerX,
      y = engine.playerY,
      xpValue = 60,
      radius = 10f,
      color = androidx.compose.ui.graphics.Color.Yellow,
      isBeingMagnetized = true
    )
    engine.expGems.add(gem2)
    engine.update(0.1f)

    assertEquals(10, engine.level)
    assertEquals(GamePhase.ITEM_SELECT, engine.gamePhase.value)
    assertEquals(1, engine.itemSelectionOptions.value.size)
    assertEquals(ItemId.KRISTAL_BERKAT, engine.itemSelectionOptions.value.first().id)
  }

  @Test
  fun testKristalBerkatDraft4Picks() {
    engine.selectHero(HeroPresets.Knight)
    engine.startNewGame()

    // Level up to milestone level 5
    engine.level = 4
    engine.requiredXp = 50
    val gem = ExpGem(
      id = 555L,
      x = engine.playerX,
      y = engine.playerY,
      xpValue = 60,
      radius = 10f,
      color = androidx.compose.ui.graphics.Color.Yellow,
      isBeingMagnetized = true
    )
    engine.expGems.add(gem)
    engine.update(0.1f)

    assertEquals(5, engine.level)
    assertEquals(GamePhase.ITEM_SELECT, engine.gamePhase.value)

    val kristal = engine.itemSelectionOptions.value.first { it.id == ItemId.KRISTAL_BERKAT }
    engine.selectItem(kristal)

    // Draft Phase: 4 sequential picks
    assertEquals(GamePhase.ATTRIBUTE_DRAFT, engine.gamePhase.value)
    assertEquals(1, engine.attributeDraftState.value?.currentStep)

    // Pick step 1
    engine.selectDraftAttribute(AttributeOptionType.ATK_BOOST)
    assertEquals(2, engine.attributeDraftState.value?.currentStep)

    // Pick step 2
    engine.selectDraftAttribute(AttributeOptionType.SPEED_BOOST)
    assertEquals(3, engine.attributeDraftState.value?.currentStep)

    // Pick step 3
    engine.selectDraftAttribute(AttributeOptionType.CRIT_BOOST)
    assertEquals(4, engine.attributeDraftState.value?.currentStep)

    // Pick step 4 (final)
    engine.selectDraftAttribute(AttributeOptionType.DEFENSE_BOOST)

    // Should return to PLAYING phase
    assertEquals(GamePhase.PLAYING, engine.gamePhase.value)
    assertEquals(1, engine.randomCrystalPurchasedCount)
    assertTrue(engine.bonusAtkPercent > 0f)
    assertTrue(engine.bonusMoveSpeedPercent > 0f)
    assertTrue(engine.bonusCritRate > 0f)
    assertTrue(engine.bonusArmorPercent > 0f)
  }
}
