package com.ehan.mobrush.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ehan.mobrush.model.HeroConfig
import com.ehan.mobrush.model.HeroPresets
import com.ehan.mobrush.ui.theme.*

@Composable
fun MainMenuScreen(
  selectedHero: HeroConfig,
  onSelectHero: (HeroConfig) -> Unit,
  onStartGame: () -> Unit,
  isSfxEnabled: Boolean,
  onToggleSfx: () -> Unit,
  isHapticsEnabled: Boolean,
  onToggleHaptics: () -> Unit
) {
  var showHeroSelectDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "menu_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.025f,
    animationSpec = infiniteRepeatable(
      animation = tween(1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.horizontalGradient(
          colors = listOf(
            BentoBgDark,
            Color(0xFF141618),
            BentoBgBlack
          )
        )
      )
      .systemBarsPadding()
  ) {
    // 2-Column Landscape Layout
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.spacedBy(20.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // LEFT COLUMN: Game Branding + Active Hero Indicator Card
      Column(
        modifier = Modifier
          .weight(1.15f)
          .fillMaxHeight()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // App Title & Tagline
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = BentoAccentPrimary
            ) {
              Text(
                text = "SURVIVAL ARENA",
                color = BentoAccentIce,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "MOB RUSH",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = BentoTextPrimary,
            letterSpacing = 1.sp
          )

          Text(
            text = "Roguelike Action Survival • Lawan Serbuan Monster & Kumpulkan Relik",
            fontSize = 10.sp,
            color = BentoTextSecondary,
            lineHeight = 14.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PENANDA HERO AKTIF (Showcase Card)
        ActiveHeroShowcaseCard(
          hero = selectedHero,
          onChangeHeroClick = { showHeroSelectDialog = true }
        )
      }

      // RIGHT COLUMN: Menu Action Buttons (Mulai, Pilih Hero, Pengaturan)
      Column(
        modifier = Modifier
          .weight(0.95f)
          .fillMaxHeight()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          shape = RoundedCornerShape(22.dp),
          color = BentoSurfaceElevated,
          border = BorderStroke(1.dp, BentoBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "MENU UTAMA",
              color = BentoAccentIce,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )

            // 1. TOMBOL MULAI PERMAINAN (Primary Glowing Button)
            Button(
              onClick = onStartGame,
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .scale(pulseScale)
                .testTag("start_game_button"),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = BentoAccentIce,
                contentColor = BentoAccentDark
              ),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = BentoAccentDark,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "MULAI PERMAINAN",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                )
              }
            }

            // 2. TOMBOL PILIH HERO
            OutlinedButton(
              onClick = { showHeroSelectDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("menu_select_hero_button"),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.2.dp, BentoBorder),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = BentoSurface,
                contentColor = BentoTextPrimary
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = BentoAccentIce,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Pilih Hero",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                  )
                  Text(
                    text = "Ganti karakter aktif (${selectedHero.name})",
                    fontSize = 9.sp,
                    color = BentoTextMuted
                  )
                }
                Text(
                  text = "UBAH →",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = BentoAccentIce
                )
              }
            }

            // 3. TOMBOL PENGATURAN
            OutlinedButton(
              onClick = { showSettingsDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("menu_settings_button"),
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.2.dp, BentoBorder),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = BentoSurface,
                contentColor = BentoTextPrimary
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = null,
                  tint = BentoAccentIce,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Pengaturan",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                  )
                  Text(
                    text = "Suara, Getaran & Panduan",
                    fontSize = 9.sp,
                    color = BentoTextMuted
                  )
                }
                Text(
                  text = "BUKA →",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = BentoAccentIce
                )
              }
            }
          }
        }
      }
    }

    // DIALOG PILIH HERO
    if (showHeroSelectDialog) {
      HeroSelectModalDialog(
        currentHero = selectedHero,
        onSelectHero = { hero ->
          onSelectHero(hero)
          showHeroSelectDialog = false
        },
        onDismiss = { showHeroSelectDialog = false }
      )
    }

    // DIALOG PENGATURAN
    if (showSettingsDialog) {
      SettingsModalDialog(
        isSfxEnabled = isSfxEnabled,
        onToggleSfx = onToggleSfx,
        isHapticsEnabled = isHapticsEnabled,
        onToggleHaptics = onToggleHaptics,
        onDismiss = { showSettingsDialog = false }
      )
    }
  }
}

/**
 * Penanda Hero Aktif (Active Hero Indicator Card)
 */
@Composable
fun ActiveHeroShowcaseCard(
  hero: HeroConfig,
  onChangeHeroClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = BentoSurfaceElevated,
    border = BorderStroke(1.5.dp, BentoAccentIce.copy(alpha = 0.6f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = BentoAccentPrimary
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(BentoAccentIce)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "HERO AKTIF SAAT INI",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          }
        }

        TextButton(
          onClick = onChangeHeroClick,
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "Ganti Hero",
            color = BentoAccentIce,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Hero Profile Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BentoAccentPrimary)
            .border(1.5.dp, BentoAccentIce, RoundedCornerShape(14.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hero.id == HeroPresets.Knight.id) "🗡️" else "🏹",
            fontSize = 22.sp
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = hero.name,
            color = BentoTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "${hero.title} • ⚡ ${hero.weaponName}",
            color = BentoAccentIce,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Quick 4 Stats Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        BentoQuickStat(label = "HP", value = "${hero.baseMaxHp.toInt()}", color = BentoHealthRed, modifier = Modifier.weight(1f))
        BentoQuickStat(label = "ATK", value = "${hero.baseAttackDamage.toInt()}", color = BentoAccentIce, modifier = Modifier.weight(1f))
        BentoQuickStat(label = "SPD", value = "${hero.baseSpeed.toInt()}", color = BentoAccentIceLight, modifier = Modifier.weight(1f))
        BentoQuickStat(label = "CRIT", value = "${(hero.baseCritRate * 100).toInt()}%", color = BentoGold, modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
fun BentoQuickStat(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = BentoSurface,
    border = BorderStroke(1.dp, BentoBorderSubtle),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
      Text(text = label, color = BentoTextMuted, fontSize = 8.sp)
    }
  }
}

/**
 * Dialog Pemilihan Hero (Dedicated Hero Selection Modal)
 */
@Composable
fun HeroSelectModalDialog(
  currentHero: HeroConfig,
  onSelectHero: (HeroConfig) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = BentoBgDark,
      border = BorderStroke(1.5.dp, BentoBorder),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .widthIn(max = 720.dp)
        .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                BentoSurfaceElevated,
                BentoBgDark,
                BentoBgBlack
              )
            )
          )
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = BentoAccentPrimary
          ) {
            Text(
              text = "PILIH HERO KARAKTER",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = BentoTextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Tentukan Karakter Pertempuran",
          style = MaterialTheme.typography.titleLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Setiap hero memiliki senjata unik, jangkauan serang, dan kecepatan yang berbeda",
          color = BentoTextSecondary,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 1.dp, bottom = 12.dp)
        )

        // 2 Hero Selection Bento Cards Side-by-Side
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HeroPresets.allHeroes.forEach { hero ->
            val isSelected = hero.id == currentHero.id
            HeroSelectBentoOptionCard(
              hero = hero,
              isSelected = isSelected,
              onClick = { onSelectHero(hero) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }
}

@Composable
fun HeroSelectBentoOptionCard(
  hero: HeroConfig,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderColor = if (isSelected) BentoAccentIce else BentoBorder
  val bgColor = if (isSelected) BentoSurfaceElevated else BentoSurface

  Card(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(18.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = bgColor)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(if (isSelected) BentoAccentPrimary else BentoSurfaceHighlight)
          .border(
            1.5.dp,
            if (isSelected) BentoAccentIce else BentoBorder,
            RoundedCornerShape(14.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (hero.id == HeroPresets.Knight.id) "🗡️" else "🏹",
          fontSize = 22.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = hero.name,
        color = BentoTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        textAlign = TextAlign.Center
      )

      Text(
        text = hero.title,
        color = if (isSelected) BentoAccentIce else BentoTextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "⚡ ${hero.weaponName}",
        color = BentoAccentIce,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = hero.attackTypeDescription,
        color = BentoTextMuted,
        fontSize = 9.sp,
        textAlign = TextAlign.Center,
        lineHeight = 12.sp,
        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
      )

      // Stats 2x2 or row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        BentoQuickStat(label = "HP", value = "${hero.baseMaxHp.toInt()}", color = BentoHealthRed, modifier = Modifier.weight(1f))
        BentoQuickStat(label = "ATK", value = "${hero.baseAttackDamage.toInt()}", color = BentoAccentIce, modifier = Modifier.weight(1f))
        BentoQuickStat(label = "SPD", value = "${hero.baseSpeed.toInt()}", color = BentoAccentIceLight, modifier = Modifier.weight(1f))
        BentoQuickStat(label = "CRIT", value = "${(hero.baseCritRate * 100).toInt()}%", color = BentoGold, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isSelected) BentoAccentIce else BentoSurfaceHighlight,
          contentColor = if (isSelected) BentoAccentDark else BentoTextPrimary
        )
      ) {
        Text(
          text = if (isSelected) "✓ SEDANG DIPILIH" else "PILIH HERO INI",
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        )
      }
    }
  }
}

/**
 * Dialog Pengaturan (Settings Modal Dialog)
 */
@Composable
fun SettingsModalDialog(
  isSfxEnabled: Boolean,
  onToggleSfx: () -> Unit,
  isHapticsEnabled: Boolean,
  onToggleHaptics: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = BentoBgDark,
      border = BorderStroke(1.5.dp, BentoBorder),
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .widthIn(max = 560.dp)
        .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                BentoSurfaceElevated,
                BentoBgDark,
                BentoBgBlack
              )
            )
          )
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = BentoAccentPrimary
          ) {
            Text(
              text = "PENGATURAN GAME",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = BentoTextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Text(
          text = "Audio & Kontrol",
          style = MaterialTheme.typography.titleLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black
        )

        // SFX & Haptics Toggles
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onToggleSfx,
            modifier = Modifier.weight(1f).height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary),
            border = BorderStroke(1.dp, if (isSfxEnabled) BentoAccentIce else BentoBorder)
          ) {
            Icon(
              imageVector = if (isSfxEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
              contentDescription = null,
              tint = if (isSfxEnabled) BentoAccentIce else BentoTextMuted,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isSfxEnabled) "SFX: Aktif" else "SFX: Mati",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          OutlinedButton(
            onClick = onToggleHaptics,
            modifier = Modifier.weight(1f).height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary),
            border = BorderStroke(1.dp, if (isHapticsEnabled) BentoAccentIce else BentoBorder)
          ) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = null,
              tint = if (isHapticsEnabled) BentoAccentIce else BentoTextMuted,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isHapticsEnabled) "Getar: Aktif" else "Getar: Mati",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Panduan Kontrol & Gameplay
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = BentoSurface,
          border = BorderStroke(1.dp, BentoBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "🎮 Cara Bermain:",
              color = BentoAccentIce,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "• Gerakkan joystick virtual di sisi kiri layar untuk bergerak.\n• Hero akan otomatis menyerang musuh terdekat sesuai jangkauan senjatanya.\n• Ambil kristal EXP untuk naik level dan memilih skill pasif / pusaka abadi.",
              color = BentoTextSecondary,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth().height(42.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BentoAccentIce,
            contentColor = BentoAccentDark
          )
        ) {
          Text(text = "TUTUP PENGATURAN", fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
      }
    }
  }
}
