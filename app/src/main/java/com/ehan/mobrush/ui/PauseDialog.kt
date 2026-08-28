package com.ehan.mobrush.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ehan.mobrush.ui.theme.*

@Composable
fun PauseDialog(
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onQuitToMenu: () -> Unit,
  isSfxEnabled: Boolean,
  onToggleSfx: () -> Unit,
  isHapticsEnabled: Boolean,
  onToggleHaptics: () -> Unit
) {
  Dialog(
    onDismissRequest = onResume,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = BentoBgDark,
      border = BorderStroke(1.5.dp, BentoBorder),
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .widthIn(max = 520.dp)
        .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = BentoAccentPrimary
        ) {
          Text(
            text = "PERMAINAN DIJEDA",
            color = BentoAccentIce,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        // Sound / Haptic Bento Tiles
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          OutlinedButton(
            onClick = onToggleSfx,
            modifier = Modifier.weight(1f).height(40.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary),
            border = BorderStroke(1.dp, if (isSfxEnabled) BentoAccentIce else BentoBorder)
          ) {
            Icon(
              imageVector = if (isSfxEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
              contentDescription = null,
              tint = if (isSfxEnabled) BentoAccentIce else BentoTextMuted,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = if (isSfxEnabled) "SFX: On" else "SFX: Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onToggleHaptics,
            modifier = Modifier.weight(1f).height(40.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary),
            border = BorderStroke(1.dp, if (isHapticsEnabled) BentoAccentIce else BentoBorder)
          ) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = null,
              tint = if (isHapticsEnabled) BentoAccentIce else BentoTextMuted,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = if (isHapticsEnabled) "Getar: On" else "Getar: Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Button(
          onClick = onResume,
          modifier = Modifier.fillMaxWidth().height(44.dp),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BentoAccentIce,
            contentColor = BentoAccentDark
          )
        ) {
          Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = BentoAccentDark, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "LANJUTKAN", fontWeight = FontWeight.Black, fontSize = 13.sp)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          OutlinedButton(
            onClick = onRestart,
            modifier = Modifier.weight(1f).height(42.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BentoBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary)
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = BentoTextSecondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "ULANG", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          OutlinedButton(
            onClick = onQuitToMenu,
            modifier = Modifier.weight(1f).height(42.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BentoBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary)
          ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = BentoTextMuted, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "MENU", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BentoTextMuted)
          }
        }
      }
    }
  }
}
