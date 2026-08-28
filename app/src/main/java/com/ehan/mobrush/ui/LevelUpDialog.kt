package com.ehan.mobrush.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ehan.mobrush.model.SkillUpgradeOption
import com.ehan.mobrush.ui.theme.*

@Composable
fun LevelUpDialog(
  currentLevel: Int,
  options: List<SkillUpgradeOption>,
  onSelectOption: (SkillUpgradeOption) -> Unit
) {
  Dialog(
    onDismissRequest = { /* Modal must be chosen */ },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = BentoBgDark,
      border = BorderStroke(1.5.dp, BentoBorder),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .widthIn(max = 760.dp)
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
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Stage / Level Badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "LEVEL UP! • LEVEL $currentLevel",
            color = BentoAccentIce,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = BentoAccentPrimary
          ) {
            Text(
              text = "PILIH SKILL PASIF",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Tingkatkan Kekuatan Hero",
          style = MaterialTheme.typography.titleLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Pilih 1 dari 3 berkah pasif untuk memperkuat serangan dan pertahanan",
          color = BentoTextSecondary,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 1.dp, bottom = 10.dp)
        )

        // Cards displayed in a horizontal Row in Landscape!
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          options.forEachIndexed { index, option ->
            SkillBentoCard(
              option = option,
              onClick = { onSelectOption(option) },
              modifier = Modifier
                .weight(1f)
                .testTag("skill_option_$index")
            )
          }
        }
      }
    }
  }
}

@Composable
fun SkillBentoCard(
  option: SkillUpgradeOption,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .border(1.dp, BentoBorder, RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = BentoSurface)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Bento Icon Squircle
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(BentoSurfaceHighlight)
          .border(1.dp, BentoBorderSubtle, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = option.type.iconSymbol, fontSize = 20.sp)
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = option.type.displayName,
        color = BentoTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (option.isNew) BentoAccentPrimary else BentoSurfaceHighlight
      ) {
        Text(
          text = if (option.isNew) "BARU" else "LV ${option.currentLevel} ➔ ${option.targetLevel}",
          color = if (option.isNew) BentoAccentIce else BentoTextPrimary,
          fontWeight = FontWeight.Black,
          fontSize = 8.sp,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = option.description,
        color = BentoAccentIce,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        lineHeight = 13.sp,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
