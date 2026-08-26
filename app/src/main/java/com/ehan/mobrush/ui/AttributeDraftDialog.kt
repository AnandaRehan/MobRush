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
import com.ehan.mobrush.model.AttributeDraftState
import com.ehan.mobrush.model.AttributeOptionType
import com.ehan.mobrush.ui.theme.*

@Composable
fun AttributeDraftDialog(
  draftState: AttributeDraftState,
  onSelectOption: (AttributeOptionType) -> Unit
) {
  Dialog(
    onDismissRequest = { /* Must finish draft */ },
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
        // Step indicator badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "DRAFT ATRIBUT (${draftState.currentStep}/${draftState.totalSteps})",
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
              text = "KRISTAL BERKAT",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Pilih 1 Atribut Acak",
          style = MaterialTheme.typography.headlineLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Pilihan ke-${draftState.currentStep} dari ${draftState.totalSteps} atribut acak",
          color = BentoTextSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          draftState.currentOptions.forEachIndexed { index, option ->
            AttributeBentoCard(
              option = option,
              onClick = { onSelectOption(option) },
              modifier = Modifier.testTag("draft_attribute_$index")
            )
          }
        }
      }
    }
  }
}

@Composable
fun AttributeBentoCard(
  option: AttributeOptionType,
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
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(BentoSurfaceHighlight)
          .border(1.dp, BentoBorderSubtle, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = option.iconSymbol, fontSize = 22.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = option.displayName,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = option.effectDescription,
          color = BentoAccentIce,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

