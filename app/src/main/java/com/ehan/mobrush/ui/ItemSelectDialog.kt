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
import com.ehan.mobrush.model.ItemDefinition
import com.ehan.mobrush.model.ItemId
import com.ehan.mobrush.ui.theme.*

@Composable
fun ItemSelectDialog(
  currentLevel: Int,
  availableItems: List<ItemDefinition>,
  onSelectItem: (ItemDefinition) -> Unit
) {
  Dialog(
    onDismissRequest = { /* Must select */ },
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
        // Top Stage Info & Milestone Badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "STAGE 1 • LEVEL $currentLevel",
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
              text = "PUSAKA MILESTONE",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Pilih Pusaka Abadi",
          style = MaterialTheme.typography.titleLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Setiap 5 level, pilih satu relik pusaka berkekuatan dahsyat",
          color = BentoTextSecondary,
          fontSize = 11.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 1.dp, bottom = 10.dp)
        )

        // Side-by-Side Bento Cards in Landscape
        val pemulihanItem = availableItems.firstOrNull { it.id == ItemId.PEMULIHAN }
        val kristalItem = availableItems.firstOrNull { it.id == ItemId.KRISTAL_BERKAT }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (pemulihanItem != null) {
            FeaturedPemulihanBentoCard(
              item = pemulihanItem,
              onClick = { onSelectItem(pemulihanItem) },
              modifier = Modifier
                .weight(1f)
                .testTag("item_option_0")
            )
          }

          if (kristalItem != null) {
            KristalBerkatBentoCard(
              item = kristalItem,
              onClick = { onSelectItem(kristalItem) },
              modifier = Modifier
                .weight(1f)
                .testTag("item_option_1")
            )
          }
        }
      }
    }
  }
}

@Composable
fun FeaturedPemulihanBentoCard(
  item: ItemDefinition,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .border(2.dp, BentoAccentIce, RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = BentoSurfaceElevated)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BentoAccentIce),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "❤", color = BentoAccentDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.name,
            color = BentoTextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
          Text(
            text = "UNIQUE • 1X AMBIL",
            color = BentoAccentIce,
            fontWeight = FontWeight.Black,
            fontSize = 8.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Nambah Max HP dan memulihkan 10% Max Nyawa setiap detik.",
        color = BentoTextSecondary,
        fontSize = 10.sp,
        lineHeight = 14.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "✨ ${item.baseAttributeDescription}",
          color = BentoAccentIce,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "AMBIL →",
          color = BentoAccentIce,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}

@Composable
fun KristalBerkatBentoCard(
  item: ItemDefinition,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .border(1.dp, BentoGold, RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = BentoSurface)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BentoGold),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "💎", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.name,
            color = BentoTextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
          Text(
            text = "DAPAT DIULANG",
            color = BentoGold,
            fontWeight = FontWeight.Black,
            fontSize = 8.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Mendapatkan 3x kesempatan memilih peningkatan atribut acak.",
        color = BentoTextSecondary,
        fontSize = 10.sp,
        lineHeight = 14.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "🎲 3x Atribut Acak",
          color = BentoGold,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "AMBIL →",
          color = BentoGold,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}
