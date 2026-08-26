package com.ehan.mobrush.model

import androidx.compose.ui.graphics.Color

enum class ItemId {
  PEMULIHAN,
  KRISTAL_BERKAT
}

data class ItemDefinition(
  val id: ItemId,
  val name: String,
  val subtitle: String,
  val iconSymbol: String,
  val color: Color,
  val baseAttributeDescription: String,
  val uniquePassiveDescription: String?,
  val maxStack: Int, // 1 for unique passives, Int.MAX_VALUE for random attribute crystal
  val isRepeatable: Boolean
)

object GameItems {
  val Pemulihan = ItemDefinition(
    id = ItemId.PEMULIHAN,
    name = "Pemulihan Abadi",
    subtitle = "Item Relik Pasif Unik",
    iconSymbol = "💖",
    color = Color(0xFF00E676),
    baseAttributeDescription = "+40 Max HP Seketika",
    uniquePassiveDescription = "Pasif Unik: Memulihkan 10% dari Max HP setiap 1.0 detik secara terus-menerus.",
    maxStack = 1,
    isRepeatable = false
  )

  val KristalBerkat = ItemDefinition(
    id = ItemId.KRISTAL_BERKAT,
    name = "Kristal Berkat Acak",
    subtitle = "Item Berkat Berulang",
    iconSymbol = "💎",
    color = Color(0xFFFFD54F),
    baseAttributeDescription = "Membuka 4 Kali Pemilihan Atribut Tambahan secara berurutan!",
    uniquePassiveDescription = "Tanpa batasan pembelian (Dapat dipilih berulang kali pada setiap kelipatan Level 5, 10, 15, dst).",
    maxStack = Int.MAX_VALUE,
    isRepeatable = true
  )

  val allItems = listOf(Pemulihan, KristalBerkat)
}

enum class AttributeOptionType(
  val displayName: String,
  val iconSymbol: String,
  val effectDescription: String,
  val color: Color
) {
  ATK_BOOST(
    displayName = "+10% Kerusakan Serang",
    iconSymbol = "⚔️",
    effectDescription = "Meningkatkan daya hancur setiap pukulan/tembakan.",
    color = Color(0xFFFF5252)
  ),
  ATK_SPEED_BOOST(
    displayName = "+8% Kecepatan Serang",
    iconSymbol = "⚡",
    effectDescription = "Mengurangi jeda waktu antar serangan.",
    color = Color(0xFFFFD600)
  ),
  MAX_HP_BOOST(
    displayName = "+25 Max HP & Heal",
    iconSymbol = "❤️",
    effectDescription = "Memperbesar kapasitas darah dan langsung mengisi 25 HP.",
    color = Color(0xFFE91E63)
  ),
  CRIT_BOOST(
    displayName = "+6% Peluang Kritis",
    iconSymbol = "🎯",
    effectDescription = "Meningkatkan kemungkinan serangan berlipat ganda.",
    color = Color(0xFFFF4081)
  ),
  SPEED_BOOST(
    displayName = "+8% Kecepatan Gerak",
    iconSymbol = "👟",
    effectDescription = "Membuat karakter berlari lebih lincah menghindari kepungan.",
    color = Color(0xFF00E676)
  ),
  DEFENSE_BOOST(
    displayName = "+6% Pertahanan Perisai",
    iconSymbol = "🛡️",
    effectDescription = "Meredam persentase kerusakan serangan musuh.",
    color = Color(0xFF7C4DFF)
  ),
  MAGNET_BOOST(
    displayName = "+20% Radius Magnet XP",
    iconSymbol = "🧲",
    effectDescription = "Menarik permata XP dari jarak yang lebih jauh.",
    color = Color(0xFF00E5FF)
  )
}
