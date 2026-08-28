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

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Pilih 1 Atribut Acak",
          style = MaterialTheme.typography.titleLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Pilihan ke-${draftState.currentStep} dari ${draftState.totalSteps} atribut acak",
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
          draftState.currentOptions.forEachIndexed { index, option ->
            AttributeBentoCard(
              option = option,
              onClick = { onSelectOption(option) },
              modifier = Modifier
                .weight(1f)
                .testTag("draft_attribute_$index")
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
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(BentoSurfaceHighlight)
          .border(1.dp, BentoBorderSubtle, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = option.iconSymbol, fontSize = 20.sp)
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = option.displayName,
        color = BentoTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = option.effectDescription,
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
