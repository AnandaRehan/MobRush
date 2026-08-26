package com.ehan.mobrush.model

enum class GamePhase {
  MAIN_MENU,
  PLAYING,
  PAUSED,
  LEVEL_UP_SKILL_SELECT,
  ITEM_SELECT,
  ATTRIBUTE_DRAFT,
  GAME_OVER
}

data class SkillUpgradeOption(
  val type: PassiveSkillType,
  val isNew: Boolean,
  val currentLevel: Int,
  val targetLevel: Int,
  val description: String
)

data class AttributeDraftState(
  val currentStep: Int, // 1 to 4
  val totalSteps: Int = 4,
  val currentOptions: List<AttributeOptionType>
)

data class GameStatistics(
  val timeSurvivedSeconds: Float = 0f,
  val totalKills: Int = 0,
  val highestLevel: Int = 1,
  val totalDamageDealt: Float = 0f,
  val gemsCollected: Int = 0,
  val bossesDefeated: Int = 0
)
