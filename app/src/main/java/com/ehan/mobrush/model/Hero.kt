package com.ehan.mobrush.model

import androidx.compose.ui.graphics.Color

enum class HeroId {
  KNIGHT,
  ARCHER
}

data class HeroConfig(
  val id: HeroId,
  val name: String,
  val title: String,
  val weaponName: String,
  val description: String,
  val attackTypeDescription: String,
  val baseMaxHp: Float,
  val baseSpeed: Float,
  val baseAttackDamage: Float,
  val baseAttackIntervalSec: Float,
  val baseAttackRange: Float,
  val baseCritRate: Float,
  val baseCritMultiplier: Float,
  val primaryColor: Color,
  val accentColor: Color
)

object HeroPresets {
  val Knight = HeroConfig(
    id = HeroId.KNIGHT,
    name = "Ksatria Badai",
    title = "Blademaster Melee",
    weaponName = "Pedang Api Kembar",
    description = "Petarung jarak dekat dengan tebasan melingkar berdaya rusak tinggi dan knockback kuat terhadap gerombolan musuh.",
    attackTypeDescription = "Tebasan Busur Luar 160° dengan dorongan mundur dan radius area luas.",
    baseMaxHp = 160f,
    baseSpeed = 190f,
    baseAttackDamage = 35f,
    baseAttackIntervalSec = 0.9f,
    baseAttackRange = 135f,
    baseCritRate = 0.10f,
    baseCritMultiplier = 1.6f,
    primaryColor = Color(0xFFFF3D00),
    accentColor = Color(0xFFFFD700)
  )

  val Archer = HeroConfig(
    id = HeroId.ARCHER,
    name = "Pemanah Angin",
    title = "Storm Ranger",
    weaponName = "Busur Kilat Tembus",
    description = "Penembak jitu jarak jauh dengan kecepatan tembak tinggi dan anak panah yang menembus barisan monster.",
    attackTypeDescription = "Panah Energi Cepat menembus target dengan jangkauan pandang luas.",
    baseMaxHp = 110f,
    baseSpeed = 230f,
    baseAttackDamage = 22f,
    baseAttackIntervalSec = 0.45f,
    baseAttackRange = 360f,
    baseCritRate = 0.20f,
    baseCritMultiplier = 1.8f,
    primaryColor = Color(0xFF00E5FF),
    accentColor = Color(0xFF76FF03)
  )

  val allHeroes = listOf(Knight, Archer)
}
