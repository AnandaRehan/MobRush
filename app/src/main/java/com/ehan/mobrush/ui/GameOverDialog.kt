package com.ehan.mobrush.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ehan.mobrush.model.GameStatistics
import com.ehan.mobrush.ui.theme.*

@Composable
fun GameOverDialog(
  statistics: GameStatistics,
  hasPemulihanItem: Boolean,
  crystalDraftsCount: Int,
  onPlayAgain: () -> Unit,
  onReturnToMenu: () -> Unit
) {
  val minutes = (statistics.timeSurvivedSeconds / 60).toInt()
  val seconds = (statistics.timeSurvivedSeconds % 60).toInt()
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)

  Dialog(
    onDismissRequest = { /* Must choose action */ },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = BentoBgDark,
      border = BorderStroke(1.5.dp, BentoBorder),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .widthIn(max = 680.dp)
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
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = BentoAccentPrimary
        ) {
          Text(
            text = "PERTEMPURAN BERAKHIR",
            color = BentoAccentIce,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Hasil Ekspedisi",
          style = MaterialTheme.typography.titleLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4 Bento Stat Tiles in a Single Row (or 2x2)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          BentoStatBox(
            label = "Waktu",
            value = timeFormatted,
            valueColor = BentoAccentIce,
            modifier = Modifier.weight(1f)
          )
          BentoStatBox(
            label = "Musuh",
            value = "${statistics.totalKills}",
            valueColor = BentoHealthRed,
            modifier = Modifier.weight(1f)
          )
          BentoStatBox(
            label = "Level",
            value = "Lv. ${statistics.highestLevel}",
            valueColor = BentoGold,
            modifier = Modifier.weight(1f)
          )
          BentoStatBox(
            label = "XP Gem",
            value = "${statistics.gemsCollected}",
            valueColor = BentoAccentIceLight,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Relik summary bento banner
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = BentoSurface,
          border = BorderStroke(1.dp, BentoBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "💖 Pemulihan: ", color = BentoTextSecondary, fontSize = 10.sp)
              Text(
                text = if (hasPemulihanItem) "Aktif" else "Tidak",
                color = if (hasPemulihanItem) BentoAccentIce else BentoTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "💎 Kristal Berkat: ", color = BentoTextSecondary, fontSize = 10.sp)
              Text(
                text = if (crystalDraftsCount > 0) "$crystalDraftsCount Kali (${crystalDraftsCount * 4} Stat)" else "0 Kali",
                color = if (crystalDraftsCount > 0) BentoGold else BentoTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onPlayAgain,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("play_again_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = BentoAccentIce,
              contentColor = BentoAccentDark
            )
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = BentoAccentDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "MAIN LAGI", fontSize = 12.sp, fontWeight = FontWeight.Black)
          }

          OutlinedButton(
            onClick = onReturnToMenu,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("game_over_menu_button"),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BentoBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary)
          ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = BentoTextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "MENU UTAMA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun BentoStatBox(
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
      Text(text = value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Black)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = label, color = BentoTextMuted, fontSize = 9.sp)
    }
  }
}
