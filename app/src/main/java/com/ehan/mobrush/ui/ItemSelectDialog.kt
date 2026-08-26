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
              text = "PUSAKA ABADI",
              color = BentoAccentIce,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Pilih Item Baru",
          style = MaterialTheme.typography.headlineLarge,
          color = BentoTextPrimary,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Setiap 5 level, pilih satu relik pusaka berkekuatan dahsyat",
          color = BentoTextSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Bento Grid of Items
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val pemulihanItem = availableItems.firstOrNull { it.id == ItemId.PEMULIHAN }
          val kristalItem = availableItems.firstOrNull { it.id == ItemId.KRISTAL_BERKAT }

          // 1. Featured Bento Item: Pemulihan (if available)
          if (pemulihanItem != null) {
            FeaturedPemulihanBentoCard(
              item = pemulihanItem,
              onClick = { onSelectItem(pemulihanItem) },
              modifier = Modifier.testTag("item_option_0")
            )
          }

          // 2. Secondary Bento Grid Row
          if (kristalItem != null) {
            KristalBerkatBentoCard(
              item = kristalItem,
              onClick = { onSelectItem(kristalItem) },
              modifier = Modifier.testTag("item_option_1")
            )
          }

          // 3. Decorative Bento Grid Row for extra locked items
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            LockedBentoTile(
              icon = "⚔️",
              title = "Pusaka Pedang",
              modifier = Modifier.weight(1f)
            )
            LockedBentoTile(
              icon = "🛡️",
              title = "Aegis Abadi",
              modifier = Modifier.weight(1f)
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
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .clickable { onClick() }
      .border(2.dp, BentoAccentIce, RoundedCornerShape(24.dp)),
    colors = CardDefaults.cardColors(containerColor = BentoSurfaceElevated)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Watermark in background
      Text(
        text = "✚",
        color = BentoTextPrimary.copy(alpha = 0.05f),
        fontSize = 72.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.align(Alignment.TopEnd)
      )

      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(BentoAccentIce),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "❤", color = BentoAccentDark, fontSize = 24.sp, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.name,
              color = BentoTextPrimary,
              fontWeight = FontWeight.Black,
              fontSize = 17.sp
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = BentoAccentPrimary
            ) {
              Text(
                text = "UNIQUE ITEM • 1X AMBIL",
                color = BentoAccentIce,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Nambah Max HP dan memulihkan 10% Max Nyawa setiap detik.",
          color = BentoTextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
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
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "AMBIL ITEM →",
            color = BentoAccentIce,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }
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
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .clickable { onClick() }
      .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
    colors = CardDefaults.cardColors(containerColor = BentoSurface)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
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
          Text(text = "✦", color = BentoAccentIce, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.name,
            color = BentoTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = "BEBAS DIBELI TANPA BATAS",
            color = BentoGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Pilih 4 atribut acak sebanyak 4 kali pilihan berturut-turut untuk memperkuat hero.",
        color = BentoTextMuted,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "💎 4x Pilihan Atribut",
          color = BentoTextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "AMBIL ITEM →",
          color = BentoAccentIce,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun LockedBentoTile(
  icon: String,
  title: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = BentoSurface.copy(alpha = 0.5f),
    border = BorderStroke(1.dp, BentoBorderSubtle),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = icon, fontSize = 20.sp, color = BentoTextMuted)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = title, color = BentoTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Text(text = "LOCKED", color = BentoTextMuted.copy(alpha = 0.6f), fontSize = 8.sp, fontWeight = FontWeight.Black)
    }
  }
}

