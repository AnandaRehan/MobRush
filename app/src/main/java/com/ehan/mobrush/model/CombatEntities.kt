package com.ehan.mobrush.model

import androidx.compose.ui.graphics.Color

data class Projectile(
  val id: Long,
  var x: Float,
  var y: Float,
  val vx: Float,
  val vy: Float,
  val damage: Float,
  val radius: Float,
  val isPiercing: Boolean,
  var pierceCountLeft: Int,
  val maxDistance: Float,
  var traveledDistance: Float = 0f,
  val color: Color,
  val isCritical: Boolean = false,
  val isEnemyBullet: Boolean = false,
  var isExpired: Boolean = false
)

data class SlashWave(
  val id: Long,
  var x: Float,
  var y: Float,
  val angleRad: Float,
  val sweepRad: Float,
  val radius: Float,
  val damage: Float,
  val isCritical: Boolean,
  val maxLifetime: Float = 0.22f,
  var lifetime: Float = 0f,
  val hitMobIds: MutableSet<Long> = mutableSetOf()
)

data class ExpGem(
  val id: Long,
  var x: Float,
  var y: Float,
  val xpValue: Int,
  val radius: Float,
  val color: Color,
  var isBeingMagnetized: Boolean = false,
  var speed: Float = 0f,
  var isCollected: Boolean = false
)

data class DamageIndicator(
  val id: Long,
  var x: Float,
  var y: Float,
  val text: String,
  val color: Color,
  val isCritical: Boolean,
  val maxLifetime: Float = 0.8f,
  var lifetime: Float = 0f,
  val vy: Float = -45f
)

data class Particle(
  val id: Long,
  var x: Float,
  var y: Float,
  val vx: Float,
  val vy: Float,
  val radius: Float,
  val color: Color,
  val maxLifetime: Float,
  var lifetime: Float = 0f
)
