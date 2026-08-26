package com.ehan.mobrush.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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
  val infiniteTransition = rememberInfiniteTransition(label = "menu_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.03f,
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
        Brush.verticalGradient(
          colors = listOf(
            BentoBgDark,
            Color(0xFF141618),
            BentoBgBlack
          )
        )
      )
      .systemBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 18.dp, vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Top Bento Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = BentoSurfaceElevated,
          border = BorderStroke(1.dp, BentoBorder)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(BentoAccentIce)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "MOB RUSH ARENA",
              color = BentoAccentIce,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(
            onClick = onToggleSfx,
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(BentoSurface)
              .border(1.dp, if (isSfxEnabled) BentoAccentIce.copy(alpha = 0.5f) else BentoBorder, RoundedCornerShape(14.dp))
          ) {
            Icon(
              imageVector = if (isSfxEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
              contentDescription = "Toggle SFX",
              tint = if (isSfxEnabled) BentoAccentIce else BentoTextMuted
            )
          }

          IconButton(
            onClick = onToggleHaptics,
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(BentoSurface)
              .border(1.dp, if (isHapticsEnabled) BentoAccentIce.copy(alpha = 0.5f) else BentoBorder, RoundedCornerShape(14.dp))
          ) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = "Toggle Haptics",
              tint = if (isHapticsEnabled) BentoAccentIce else BentoTextMuted
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Bento Hero Title Card
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = BentoSurfaceElevated,
        border = BorderStroke(1.dp, BentoBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BentoAccentPrimary
          ) {
            Text(
              text = "ROGUELIKE AUTO-BATTLE",
              color = BentoAccentIce,
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "MOB RUSH",
            style = MaterialTheme.typography.displayLarge,
            color = BentoTextPrimary,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
          Text(
            text = "Bertahan hidup dari kepungan ratusan monster ganas!",
            color = BentoTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "PILIH KARAKTER",
          color = BentoTextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 0.5.sp
        )
        Text(
          text = "2 HERO TERSEDIA",
          color = BentoTextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Bento Grid Hero Select Cards
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        HeroPresets.allHeroes.forEach { hero ->
          val isSelected = hero.id == selectedHero.id
          HeroBentoCard(
            hero = hero,
            isSelected = isSelected,
            onClick = { onSelectHero(hero) },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Bento Grid Hero Specs & Stats
      SelectedHeroBentoDetail(hero = selectedHero)

      Spacer(modifier = Modifier.height(14.dp))

      // 6. Bento Grid Game System Guide
      GameMechanicsBentoGuide()

      Spacer(modifier = Modifier.height(20.dp))

      // 7. Start Action Button
      Button(
        onClick = onStartGame,
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp)
          .scale(pulseScale)
          .testTag("start_game_button"),
        shape = RoundedCornerShape(20.dp),
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
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "MULAI PERTEMPURAN",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
    }
  }
}

@Composable
fun HeroBentoCard(
  hero: HeroConfig,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderColor = if (isSelected) BentoAccentIce else BentoBorder
  val bgColor = if (isSelected) BentoSurfaceElevated else BentoSurface

  Card(
    modifier = modifier
      .clip(RoundedCornerShape(22.dp))
      .clickable { onClick() }
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(22.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = bgColor)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(if (isSelected) BentoAccentPrimary else BentoSurfaceHighlight)
          .border(
            1.5.dp,
            if (isSelected) BentoAccentIce else BentoBorder,
            RoundedCornerShape(16.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (hero.id == HeroPresets.Knight.id) "🗡️" else "🏹",
          fontSize = 24.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = hero.name,
        color = BentoTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        textAlign = TextAlign.Center
      )

      Text(
        text = hero.title,
        color = if (isSelected) BentoAccentIce else BentoTextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) BentoAccentPrimary else BentoSurfaceHighlight
      ) {
        Text(
          text = if (isSelected) "AKTIF" else "PILIH",
          color = if (isSelected) BentoAccentIce else BentoTextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.ExtraBold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }
    }
  }
}

@Composable
fun SelectedHeroBentoDetail(hero: HeroConfig) {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = BentoSurfaceElevated,
    border = BorderStroke(1.dp, BentoBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "⚡ Senjata: ${hero.weaponName}",
            color = BentoAccentIce,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = BentoAccentPrimary
        ) {
          Text(
            text = hero.attackTypeDescription,
            color = BentoAccentIce,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4-Tile Bento Stat Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        BentoStatTile(
          icon = "❤️",
          label = "Max HP",
          value = "${hero.baseMaxHp.toInt()}",
          valueColor = BentoHealthRed,
          modifier = Modifier.weight(1f)
        )
        BentoStatTile(
          icon = "⚔️",
          label = "Serangan",
          value = "${hero.baseAttackDamage.toInt()}",
          valueColor = BentoAccentIce,
          modifier = Modifier.weight(1f)
        )
        BentoStatTile(
          icon = "⚡",
          label = "Kecepatan",
          value = "${hero.baseSpeed.toInt()}",
          valueColor = BentoAccentIceLight,
          modifier = Modifier.weight(1f)
        )
        BentoStatTile(
          icon = "💥",
          label = "Kritis",
          value = "${(hero.baseCritRate * 100).toInt()}%",
          valueColor = BentoGold,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
fun BentoStatTile(
  icon: String,
  label: String,
  value: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = BentoSurface,
    border = BorderStroke(1.dp, BentoBorderSubtle),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = icon, fontSize = 12.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Black)
      Text(text = label, color = BentoTextMuted, fontSize = 9.sp)
    }
  }
}

@Composable
fun GameMechanicsBentoGuide() {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = BentoSurface,
    border = BorderStroke(1.dp, BentoBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "SISTEM PROGRESSI BENTO",
          color = BentoAccentIce,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          letterSpacing = 0.5.sp
        )
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = BentoSurfaceHighlight
        ) {
          Text(
            text = "INFO",
            color = BentoTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = BentoSurfaceElevated,
          border = BorderStroke(1.dp, BentoBorderSubtle),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "⭐ Level Biasa", color = BentoAccentIce, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Pilih 3 Skill Pasif acak atau tingkatkan level skill hingga Lv 5.",
              color = BentoTextSecondary,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = BentoSurfaceElevated,
          border = BorderStroke(1.dp, BentoBorderSubtle),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "👑 Tiap 5 Level", color = BentoGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Pilih Relik: Pemulihan (Regen 10% HP/dtk) atau Kristal Berkat.",
              color = BentoTextSecondary,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
        }
      }
    }
  }
}
