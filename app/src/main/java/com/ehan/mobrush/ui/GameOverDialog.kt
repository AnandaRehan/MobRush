package com.ehan.mobrush.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = BentoBgDark,
      border = BorderStroke(1.5.dp, BentoBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 12.dp)
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
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = BentoAccentPrimary
        ) {
          Text(
            text = "PERTEMPURAN BERAKHIR",
            color = BentoAccentIce,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Hasil Ekspedisi",
          style = MaterialTheme.typography.headlineLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Pahlawanmu telah berjuang gagah berani di arena!",
          color = BentoTextSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        // 2x2 Bento Stat Tiles
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          BentoStatBox(
            label = "Waktu Bertahan",
            value = timeFormatted,
            valueColor = BentoAccentIce,
            modifier = Modifier.weight(1f)
          )
          BentoStatBox(
            label = "Musuh Dibasmi",
            value = "${statistics.totalKills}",
            valueColor = BentoHealthRed,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          BentoStatBox(
            label = "Tingkat Level",
            value = "Lv. ${statistics.highestLevel}",
            valueColor = BentoGold,
            modifier = Modifier.weight(1f)
          )
          BentoStatBox(
            label = "Permata XP",
            value = "${statistics.gemsCollected}",
            valueColor = BentoAccentIceLight,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Relik summary bento banner
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = BentoSurface,
          border = BorderStroke(1.dp, BentoBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "💖 Pemulihan", color = BentoTextSecondary, fontSize = 11.sp)
              Text(
                text = if (hasPemulihanItem) "Aktif (10% HP/s)" else "Belum",
                color = if (hasPemulihanItem) BentoAccentIce else BentoTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "💎 Kristal Berkat", color = BentoTextSecondary, fontSize = 11.sp)
              Text(
                text = if (crystalDraftsCount > 0) "$crystalDraftsCount Kali (${crystalDraftsCount * 4} Atribut)" else "0 Kali",
                color = if (crystalDraftsCount > 0) BentoGold else BentoTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Buttons
        Button(
          onClick = onPlayAgain,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("play_again_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BentoAccentIce,
            contentColor = BentoAccentDark
          )
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = BentoAccentDark)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "MAIN LAGI", fontSize = 14.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onReturnToMenu,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("game_over_menu_button"),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, BentoBorder),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary)
        ) {
          Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = BentoTextSecondary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "MENU UTAMA", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
    shape = RoundedCornerShape(16.dp),
    color = BentoSurface,
    border = BorderStroke(1.dp, BentoBorderSubtle),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Black)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = label, color = BentoTextMuted, fontSize = 10.sp)
    }
  }
}

