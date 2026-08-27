package com.ehan.mobrush.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Home
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
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
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
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = BentoAccentPrimary
        ) {
          Text(
            text = "PERMAINAN DIJEDA",
            color = BentoAccentIce,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Sound / Haptic Bento Tiles
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onToggleSfx,
            modifier = Modifier.weight(1f).height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary),
            border = BorderStroke(1.dp, if (isSfxEnabled) BentoAccentIce else BentoBorder)
          ) {
            Icon(
              imageVector = if (isSfxEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
              contentDescription = null,
              tint = if (isSfxEnabled) BentoAccentIce else BentoTextMuted
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isSfxEnabled) "SFX: On" else "SFX: Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onToggleHaptics,
            modifier = Modifier.weight(1f).height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary),
            border = BorderStroke(1.dp, if (isHapticsEnabled) BentoAccentIce else BentoBorder)
          ) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = null,
              tint = if (isHapticsEnabled) BentoAccentIce else BentoTextMuted
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isHapticsEnabled) "Getar: On" else "Getar: Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = onResume,
          modifier = Modifier.fillMaxWidth().height(48.dp),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BentoAccentIce,
            contentColor = BentoAccentDark
          )
        ) {
          Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = BentoAccentDark)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "LANJUTKAN", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        OutlinedButton(
          onClick = onRestart,
          modifier = Modifier.fillMaxWidth().height(46.dp),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, BentoBorder),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoTextPrimary)
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = BentoTextSecondary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "ULANGI DARI AWAL", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        TextButton(
          onClick = onQuitToMenu,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = BentoTextMuted)
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "KEMBALI KE MENU UTAMA", color = BentoTextMuted, fontSize = 12.sp)
        }
      }
    }
  }
}

