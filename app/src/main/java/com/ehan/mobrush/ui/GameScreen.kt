package com.ehan.mobrush.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ehan.mobrush.game.GameEngine
import com.ehan.mobrush.game.GameViewModel
import com.ehan.mobrush.model.*
import com.ehan.mobrush.ui.theme.*
import kotlin.math.*

@Composable
fun GameScreen(
  viewModel: GameViewModel
) {
  val gamePhase by viewModel.gamePhase.collectAsState()
  val skillUpgradeOptions by viewModel.skillUpgradeOptions.collectAsState()
  val itemSelectionOptions by viewModel.itemSelectionOptions.collectAsState()
  val attributeDraftState by viewModel.attributeDraftState.collectAsState()
  val isSfxEnabled by viewModel.isSfxEnabled.collectAsState()
  val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsState()

  val engine = viewModel.engine

  val damageTextPaint = remember {
    Paint().apply {
      textAlign = Paint.Align.CENTER
      typeface = Typeface.DEFAULT_BOLD
      isAntiAlias = true
    }
  }

  var renderTick by remember { mutableLongStateOf(0L) }
  LaunchedEffect(gamePhase) {
    if (gamePhase == GamePhase.PLAYING) {
      while (true) {
        withFrameNanos { renderTick = it }
      }
    }
  }

  var joystickCenter by remember { mutableStateOf(Offset.Zero) }
  var joystickKnob by remember { mutableStateOf(Offset.Zero) }
  var isJoystickActive by remember { mutableStateOf(false) }

  val maxJoystickRadius = with(LocalDensity.current) { 55.dp.toPx() }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    // 1. GAME CANVAS
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(gamePhase) {
          if (gamePhase != GamePhase.PLAYING) {
            viewModel.onJoystickInput(0f, 0f)
            isJoystickActive = false
            return@pointerInput
          }

          detectDragGestures(
            onDragStart = { offset ->
              joystickCenter = offset
              joystickKnob = offset
              isJoystickActive = true
            },
            onDragEnd = {
              isJoystickActive = false
              joystickKnob = joystickCenter
              viewModel.onJoystickInput(0f, 0f)
            },
            onDragCancel = {
              isJoystickActive = false
              joystickKnob = joystickCenter
              viewModel.onJoystickInput(0f, 0f)
            },
            onDrag = { change, dragAmount ->
              change.consume()
              val currentOffset = joystickKnob + dragAmount
              val delta = currentOffset - joystickCenter
              val dist = delta.getDistance()

              if (dist > maxJoystickRadius) {
                val factor = maxJoystickRadius / dist
                joystickKnob = joystickCenter + Offset(delta.x * factor, delta.y * factor)
                viewModel.onJoystickInput(delta.x * factor, delta.y * factor)
              } else {
                joystickKnob = currentOffset
                viewModel.onJoystickInput(delta.x, delta.y)
              }
            }
          )
        }
    ) {
      // Observe frame tick to trigger vsync synchronized frame redraws
      if (renderTick >= 0) { /* state read for recomposition/invalidation */ }

      val screenWidth = size.width
      val screenHeight = size.height

      val cameraX = engine.playerX - screenWidth / 2f
      val cameraY = engine.playerY - screenHeight / 2f

      // Draw Arena Background Grid
      drawArenaGrid(cameraX, cameraY, screenWidth, screenHeight, engine.mapWidth, engine.mapHeight)

      // Draw XP Gems
      for (gem in engine.expGems) {
        if (gem.isCollected) continue
        val gx = gem.x - cameraX
        val gy = gem.y - cameraY
        if (gx >= -30 && gx <= screenWidth + 30 && gy >= -30 && gy <= screenHeight + 30) {
          drawCircle(
            color = gem.color.copy(alpha = 0.35f),
            radius = gem.radius * 1.5f,
            center = Offset(gx, gy)
          )
          drawCircle(
            color = gem.color,
            radius = gem.radius,
            center = Offset(gx, gy)
          )
          drawCircle(
            color = Color.White,
            radius = gem.radius * 0.4f,
            center = Offset(gx - 2f, gy - 2f)
          )
        }
      }

      // Draw Slashes (Knight Melee)
      for (slash in engine.slashWaves) {
        val sx = slash.x - cameraX
        val sy = slash.y - cameraY
        val sweepDeg = (slash.sweepRad * 180f / PI).toFloat()
        val startDeg = (slash.angleRad * 180f / PI).toFloat() - (sweepDeg / 2f)
        val alpha = (1f - (slash.lifetime / slash.maxLifetime)).coerceIn(0f, 1f)

        drawArc(
          color = if (slash.isCritical) GoldAccent.copy(alpha = alpha) else CrimsonGlow.copy(alpha = alpha),
          startAngle = startDeg,
          sweepAngle = sweepDeg,
          useCenter = true,
          topLeft = Offset(sx - slash.radius, sy - slash.radius),
          size = Size(slash.radius * 2f, slash.radius * 2f),
          style = Stroke(width = 18f)
        )
      }

      // Draw Projectiles (Archer & Enemy)
      for (p in engine.projectiles) {
        if (p.isExpired) continue
        val px = p.x - cameraX
        val py = p.y - cameraY
        if (px >= -30 && px <= screenWidth + 30 && py >= -30 && py <= screenHeight + 30) {
          drawCircle(
            color = p.color.copy(alpha = 0.4f),
            radius = p.radius * 1.6f,
            center = Offset(px, py)
          )
          drawCircle(
            color = if (p.isCritical) GoldAccent else p.color,
            radius = p.radius,
            center = Offset(px, py)
          )
          // Direction tail
          val speed = sqrt(p.vx * p.vx + p.vy * p.vy).coerceAtLeast(1f)
          val tailLength = 16f
          val tx = px - (p.vx / speed) * tailLength
          val ty = py - (p.vy / speed) * tailLength
          drawLine(
            color = p.color,
            start = Offset(px, py),
            end = Offset(tx, ty),
            strokeWidth = 5f,
            cap = StrokeCap.Round
          )
        }
      }

      // Draw Mobs
      for (mob in engine.mobs) {
        if (mob.isDead) continue
        val mx = mob.x - cameraX
        val my = mob.y - cameraY
        if (mx >= -60 && mx <= screenWidth + 60 && my >= -60 && my <= screenHeight + 60) {
          val mobColor = if (mob.hitFlashTimer > 0f) Color.White else mob.species.color

          // Boss glow ring
          if (mob.species.isBoss) {
            drawCircle(
              color = GoldAccent.copy(alpha = 0.35f),
              radius = mob.radius * 1.5f,
              center = Offset(mx, my),
              style = Stroke(width = 4f)
            )
          }

          // Mob Body
          drawCircle(
            color = mobColor,
            radius = mob.radius,
            center = Offset(mx, my)
          )

          // Inner Detail / Eyes
          val eyeOffsetX = cos(atan2(engine.playerY - mob.y, engine.playerX - mob.x)) * (mob.radius * 0.4f)
          val eyeOffsetY = sin(atan2(engine.playerY - mob.y, engine.playerX - mob.x)) * (mob.radius * 0.4f)
          drawCircle(
            color = Color.Black,
            radius = mob.radius * 0.25f,
            center = Offset(mx + eyeOffsetX, my + eyeOffsetY)
          )

          // Health Bar
          val hpPercent = (mob.hp / mob.maxHp).coerceIn(0f, 1f)
          val barWidth = mob.radius * 2.2f
          val barHeight = if (mob.species.isBoss) 8f else 4f
          val barTop = my - mob.radius - 12f
          val barLeft = mx - (barWidth / 2f)

          drawRect(
            color = Color(0x99000000),
            topLeft = Offset(barLeft, barTop),
            size = Size(barWidth, barHeight)
          )
          drawRect(
            color = if (mob.species.isBoss) GoldAccent else HealthRed,
            topLeft = Offset(barLeft, barTop),
            size = Size(barWidth * hpPercent, barHeight)
          )
        }
      }

      // Draw Player
      val px = engine.playerX - cameraX
      val py = engine.playerY - cameraY

      // Magnet / Attack Aura Circle
      drawCircle(
        color = engine.currentHero.primaryColor.copy(alpha = 0.06f),
        radius = engine.currentHero.baseAttackRange * engine.getPlayerAttackArea(),
        center = Offset(px, py)
      )

      // Player Aura Glow
      drawCircle(
        color = engine.currentHero.primaryColor.copy(alpha = 0.25f),
        radius = 32f,
        center = Offset(px, py)
      )

      // Player Body
      val playerColor = if (engine.playerHurtCooldown > 0f) Color.White else engine.currentHero.primaryColor
      drawCircle(
        color = playerColor,
        radius = 22f,
        center = Offset(px, py)
      )
      drawCircle(
        color = engine.currentHero.accentColor,
        radius = 12f,
        center = Offset(px, py)
      )

      // Weapon Direction Indicator
      val tipX = px + cos(engine.playerFacingAngle) * 34f
      val tipY = py + sin(engine.playerFacingAngle) * 34f
      drawLine(
        color = GoldAccent,
        start = Offset(px, py),
        end = Offset(tipX, tipY),
        strokeWidth = 6f,
        cap = StrokeCap.Round
      )

      // Draw Particles
      for (part in engine.particles) {
        val partX = part.x - cameraX
        val partY = part.y - cameraY
        val alpha = (1f - (part.lifetime / part.maxLifetime)).coerceIn(0f, 1f)
        drawCircle(
          color = part.color.copy(alpha = alpha),
          radius = part.radius * alpha,
          center = Offset(partX, partY)
        )
      }

      // Draw Damage Indicators (Native Canvas Text) using cached Paint
      drawContext.canvas.nativeCanvas.apply {
        for (dmg in engine.damageIndicators) {
          val dx = dmg.x - cameraX
          val dy = dmg.y - cameraY
          val alpha = (1f - (dmg.lifetime / dmg.maxLifetime)).coerceIn(0f, 1f)
          damageTextPaint.color = dmg.color.toArgb()
          damageTextPaint.alpha = (alpha * 255).toInt()
          damageTextPaint.textSize = if (dmg.isCritical) 38f else 26f
          drawText(dmg.text, dx, dy, damageTextPaint)
        }
      }

      // Draw Virtual Joystick (if active)
      if (isJoystickActive) {
        drawCircle(
          color = Color(0x44FFFFFF),
          radius = maxJoystickRadius,
          center = joystickCenter,
          style = Stroke(width = 4f)
        )
        drawCircle(
          color = Color(0x33E53935),
          radius = maxJoystickRadius,
          center = joystickCenter,
          style = Fill
        )
        drawCircle(
          color = CrimsonPrimary,
          radius = 28f,
          center = joystickKnob,
          style = Fill
        )
        drawCircle(
          color = GoldAccent,
          radius = 28f,
          center = joystickKnob,
          style = Stroke(width = 3f)
        )
      }
    }

    // 2. TOP HUD OVERLAY
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .systemBarsPadding()
        .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
      // Top Status Bento Bar: Level, XP Bar, Timer, Kills, Pause
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = BentoSurfaceElevated.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, BentoBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Level Badge
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = BentoAccentPrimary
            ) {
              Text(
                text = "LV ${engine.level}",
                color = BentoAccentIce,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            // XP Progress Bar
            Column(modifier = Modifier.weight(1f)) {
              val xpProgress = (engine.currentXp.toFloat() / engine.requiredXp.toFloat()).coerceIn(0f, 1f)
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(10.dp)
                  .clip(RoundedCornerShape(5.dp))
                  .background(BentoSurfaceHighlight)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(xpProgress)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                      Brush.horizontalGradient(
                        colors = listOf(BentoAccentPrimary, BentoAccentIce, BentoAccentIceLight)
                      )
                    )
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "XP: ${engine.currentXp}/${engine.requiredXp}",
                  color = BentoAccentIce,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (engine.level % 5 == 0) "👑 MILESTONE!" else "Next: Lv ${engine.level + 1}",
                  color = if (engine.level % 5 == 0) BentoGold else BentoTextMuted,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Pause Button
            IconButton(
              onClick = { viewModel.pauseGame() },
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BentoSurface)
                .border(1.dp, BentoBorder, RoundedCornerShape(10.dp))
                .testTag("pause_button")
            ) {
              Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause Game",
                tint = BentoAccentIce,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // HP Bar & Stats Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // HP Bar
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              val hpProgress = (engine.currentHp / engine.maxHp).coerceIn(0f, 1f)
              Text(text = "❤️", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(10.dp)
                  .clip(RoundedCornerShape(5.dp))
                  .background(BentoSurfaceHighlight)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(hpProgress)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                      if (hpProgress > 0.35f) BentoHealthGreen else BentoHealthRed
                    )
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${engine.currentHp.toInt()}/${engine.maxHp.toInt()}",
                color = if (hpProgress > 0.35f) BentoHealthGreen else BentoHealthRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Timer & Kills
            val minutes = (engine.survivalTimeSec / 60).toInt()
            val seconds = (engine.survivalTimeSec % 60).toInt()
            val timeFormatted = String.format("%02d:%02d", minutes, seconds)

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = BentoSurface,
                border = BorderStroke(1.dp, BentoBorderSubtle)
              ) {
                Text(
                  text = "⏱️ $timeFormatted",
                  color = BentoAccentIce,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = BentoSurface,
                border = BorderStroke(1.dp, BentoBorderSubtle)
              ) {
                Text(
                  text = "💀 ${engine.totalKills}",
                  color = BentoHealthRed,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // Active Passives & Items Row
      val hasPassives = engine.activePassives.isNotEmpty()
      val hasItems = engine.acquiredItems.isNotEmpty() || engine.randomCrystalPurchasedCount > 0

      if (hasPassives || hasItems) {
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          // Items
          if (engine.acquiredItems.contains(ItemId.PEMULIHAN)) {
            item {
              ActiveBadge(text = "💖 Pemulihan (10% HP/s)", color = BentoAccentIce)
            }
          }
          if (engine.randomCrystalPurchasedCount > 0) {
            item {
              ActiveBadge(
                text = "💎 Kristal (${engine.randomCrystalPurchasedCount}x)",
                color = BentoGold
              )
            }
          }
          // Passives
          items(engine.activePassives.entries.toList()) { entry ->
            ActiveBadge(
              text = "${entry.key.iconSymbol} Lv${entry.value}",
              color = entry.key.color
            )
          }
        }
      }
    }

    // 3. VIRTUAL JOYSTICK HINT OVERLAY (BOTTOM)
    if (!isJoystickActive && gamePhase == GamePhase.PLAYING) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 40.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = BentoSurface.copy(alpha = 0.85f),
          border = BorderStroke(1.dp, BentoBorder)
        ) {
          Text(
            text = "🕹️ Sentuh & Geser Layar untuk Bergerak",
            color = BentoTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          )
        }
      }
    }

    // 4. DIALOG OVERLAYS
    when (gamePhase) {
      GamePhase.LEVEL_UP_SKILL_SELECT -> {
        LevelUpDialog(
          currentLevel = engine.level,
          options = skillUpgradeOptions,
          onSelectOption = { option -> viewModel.onSelectSkillUpgrade(option) }
        )
      }
      GamePhase.ITEM_SELECT -> {
        ItemSelectDialog(
          currentLevel = engine.level,
          availableItems = itemSelectionOptions,
          onSelectItem = { item -> viewModel.onSelectItem(item) }
        )
      }
      GamePhase.ATTRIBUTE_DRAFT -> {
        attributeDraftState?.let { draft ->
          AttributeDraftDialog(
            draftState = draft,
            onSelectOption = { opt -> viewModel.onSelectDraftAttribute(opt) }
          )
        }
      }
      GamePhase.PAUSED -> {
        PauseDialog(
          onResume = { viewModel.resumeGame() },
          onRestart = { viewModel.startNewGame() },
          onQuitToMenu = { viewModel.returnToMenu() },
          isSfxEnabled = isSfxEnabled,
          onToggleSfx = { viewModel.toggleSfx() },
          isHapticsEnabled = isHapticsEnabled,
          onToggleHaptics = { viewModel.toggleHaptics() }
        )
      }
      GamePhase.GAME_OVER -> {
        GameOverDialog(
          statistics = GameStatistics(
            timeSurvivedSeconds = engine.survivalTimeSec,
            totalKills = engine.totalKills,
            highestLevel = engine.level,
            totalDamageDealt = engine.totalDamageDealt,
            gemsCollected = engine.gemsCollected,
            bossesDefeated = engine.bossesDefeated
          ),
          hasPemulihanItem = engine.acquiredItems.contains(ItemId.PEMULIHAN),
          crystalDraftsCount = engine.randomCrystalPurchasedCount,
          onPlayAgain = { viewModel.startNewGame() },
          onReturnToMenu = { viewModel.returnToMenu() }
        )
      }
      else -> {}
    }
  }
}

@Composable
fun ActiveBadge(text: String, color: Color) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = DarkSurfaceElevated,
    border = BorderStroke(1.dp, color.copy(alpha = 0.7f))
  ) {
    Text(
      text = text,
      color = TextPrimary,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}

private fun DrawScope.drawArenaGrid(
  cameraX: Float,
  cameraY: Float,
  screenWidth: Float,
  screenHeight: Float,
  mapWidth: Float,
  mapHeight: Float
) {
  // Grid Lines
  val gridSize = 120f
  val startX = ((-cameraX) % gridSize)
  val startY = ((-cameraY) % gridSize)

  var x = startX
  while (x < screenWidth) {
    drawLine(
      color = Color(0x184A3B69),
      start = Offset(x, 0f),
      end = Offset(x, screenHeight),
      strokeWidth = 1.5f
    )
    x += gridSize
  }

  var y = startY
  while (y < screenHeight) {
    drawLine(
      color = Color(0x184A3B69),
      start = Offset(0f, y),
      end = Offset(screenWidth, y),
      strokeWidth = 1.5f
    )
    y += gridSize
  }

  // Arena Border Walls
  val left = -cameraX
  val top = -cameraY
  val right = mapWidth - cameraX
  val bottom = mapHeight - cameraY

  drawRect(
    color = CrimsonPrimary.copy(alpha = 0.5f),
    topLeft = Offset(left, top),
    size = Size(mapWidth, mapHeight),
    style = Stroke(width = 8f)
  )
}
