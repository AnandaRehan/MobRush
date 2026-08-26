package com.ehan.mobrush.model

import androidx.compose.ui.graphics.Color

enum class MobSpecies(
  val displayName: String,
  val baseHp: Float,
  val baseSpeed: Float,
  val baseDamage: Float,
  val radius: Float,
  val xpValue: Int,
  val color: Color,
  val accentColor: Color,
  val isBoss: Boolean = false,
  val canRangedAttack: Boolean = false
) {
  GOBLIN_SWARMER(
    displayName = "Goblin Pelari",
    baseHp = 25f,
    baseSpeed = 160f,
    baseDamage = 10f,
    radius = 16f,
    xpValue = 10,
    color = Color(0xFF4CAF50),
    accentColor = Color(0xFFC8E6C9)
  ),
  ORC_BRUTE(
    displayName = "Orc Perkasa",
    baseHp = 85f,
    baseSpeed = 100f,
    baseDamage = 22f,
    radius = 24f,
    xpValue = 28,
    color = Color(0xFFFF5722),
    accentColor = Color(0xFFFFCCBC)
  ),
  SHADOW_BAT(
    displayName = "Kelelawar Bayangan",
    baseHp = 18f,
    baseSpeed = 210f,
    baseDamage = 12f,
    radius = 14f,
    xpValue = 15,
    color = Color(0xFFAB47BC),
    accentColor = Color(0xFFE1BEE7)
  ),
  SKELETON_MAGE(
    displayName = "Penyihir Tengkorak",
    baseHp = 60f,
    baseSpeed = 115f,
    baseDamage = 18f,
    radius = 20f,
    xpValue = 35,
    color = Color(0xFF26C6DA),
    accentColor = Color(0xFFB2EBF2),
    canRangedAttack = true
  ),
  ELITE_GOLEM_BOSS(
    displayName = "GOLEM TITAN (BOSS)",
    baseHp = 450f,
    baseSpeed = 85f,
    baseDamage = 35f,
    radius = 42f,
    xpValue = 200,
    color = Color(0xFFFF1744),
    accentColor = Color(0xFFFFD700),
    isBoss = true
  )
}

data class MobInstance(
  val id: Long,
  val species: MobSpecies,
  var x: Float,
  var y: Float,
  var hp: Float,
  val maxHp: Float,
  var speed: Float,
  var damage: Float,
  val radius: Float,
  var isDead: Boolean = false,
  var hitFlashTimer: Float = 0f,
  var shootCooldown: Float = 2.0f,
  var knockbackVx: Float = 0f,
  var knockbackVy: Float = 0f
)
