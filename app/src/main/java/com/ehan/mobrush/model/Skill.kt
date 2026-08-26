package com.ehan.mobrush.model

import androidx.compose.ui.graphics.Color

enum class PassiveSkillType(
  val displayName: String,
  val iconSymbol: String,
  val color: Color,
  val descriptionTemplate: (Int) -> String
) {
  ATTACK_POWER(
    displayName = "Kekuatan Serang",
    iconSymbol = "⚔️",
    color = Color(0xFFFF5252),
    descriptionTemplate = { lvl -> "+${lvl * 15}% Total Kerusakan Serangan" }
  ),
  ATTACK_SPEED(
    displayName = "Kecepatan Serang",
    iconSymbol = "⚡",
    color = Color(0xFFFFD600),
    descriptionTemplate = { lvl -> "+${lvl * 12}% Frekuensi Kecepatan Menyerang" }
  ),
  MOVE_SPEED(
    displayName = "Sepatu Kilat",
    iconSymbol = "👟",
    color = Color(0xFF00E676),
    descriptionTemplate = { lvl -> "+${lvl * 10}% Kecepatan Gerak Karakter" }
  ),
  MAGNET_RANGE(
    displayName = "Jangkauan Magnet",
    iconSymbol = "🧲",
    color = Color(0xFF00E5FF),
    descriptionTemplate = { lvl -> "+${lvl * 25}% Radius Menarik Permata XP" }
  ),
  CRITICAL_STRIKE(
    displayName = "Pukulan Kritis",
    iconSymbol = "🎯",
    color = Color(0xFFFF4081),
    descriptionTemplate = { lvl -> "+${lvl * 7}% Peluang Kritis & +${lvl * 20}% Kerusakan Kritis" }
  ),
  ARMOR_DEFENSE(
    displayName = "Ketahanan Perisai",
    iconSymbol = "🛡️",
    color = Color(0xFF7C4DFF),
    descriptionTemplate = { lvl -> "+${lvl * 8}% Reduksi Semua Kerusakan Masuk" }
  ),
  ATTACK_AREA(
    displayName = "Radius Area & Jarak",
    iconSymbol = "💥",
    color = Color(0xFFFF6E40),
    descriptionTemplate = { lvl -> "+${lvl * 15}% Jangkauan Serang & Ukuran Efek" }
  ),
  VITALITY(
    displayName = "Vitalitas",
    iconSymbol = "❤️",
    color = Color(0xFFE91E63),
    descriptionTemplate = { lvl -> "+${lvl * 25} Max HP & Pulihkan 20 HP Langsung" }
  );

  val maxLevel: Int = 5
}

data class PassiveSkillState(
  val type: PassiveSkillType,
  val currentLevel: Int
) {
  val isMaxed: Boolean get() = currentLevel >= type.maxLevel
  val nextLevel: Int get() = (currentLevel + 1).coerceAtMost(type.maxLevel)
  val description: String get() = type.descriptionTemplate(currentLevel)
  val nextLevelDescription: String get() = type.descriptionTemplate(nextLevel)
}
