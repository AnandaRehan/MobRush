package com.ehan.mobrush.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
        // Stage / Level Badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "LEVEL UP! • LV. $currentLevel",
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
              text = "SKILL PASIF",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Pilih Skill Pasif",
          style = MaterialTheme.typography.headlineLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Buka skill baru atau tingkatkan kemampuan hero hingga Lv 5",
          color = BentoTextSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          options.forEachIndexed { index, option ->
            SkillBentoCard(
              option = option,
              onClick = { onSelectOption(option) },
              modifier = Modifier.testTag("skill_option_$index")
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
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .border(1.dp, BentoBorder, RoundedCornerShape(20.dp)),
    colors = CardDefaults.cardColors(containerColor = BentoSurface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Bento Icon Squircle
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(BentoSurfaceHighlight)
          .border(1.dp, BentoBorderSubtle, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = option.type.iconSymbol, fontSize = 22.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = option.type.displayName,
            color = BentoTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (option.isNew) BentoAccentPrimary else BentoSurfaceHighlight
          ) {
            Text(
              text = if (option.isNew) "BARU" else "LV ${option.currentLevel} ➔ ${option.targetLevel}",
              color = if (option.isNew) BentoAccentIce else BentoTextPrimary,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = option.description,
          color = BentoAccentIce,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

