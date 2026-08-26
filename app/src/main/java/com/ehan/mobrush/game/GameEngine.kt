package com.ehan.mobrush.game

import com.ehan.mobrush.audio.GameSoundManager
import com.ehan.mobrush.model.*
import com.ehan.mobrush.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*
import kotlin.random.Random

class GameEngine(
  val soundManager: GameSoundManager
) {
  // Arena bounds
  val mapWidth = 2400f
  val mapHeight = 2400f

  // Player state
  var playerX = 1200f
  var playerY = 1200f
  var playerVx = 0f
  var playerVy = 0f
  var playerFacingAngle = 0f

  var currentHp = 160f
  var maxHp = 160f
  var level = 1
  var currentXp = 0
  var requiredXp = 40

  var currentHero: HeroConfig = HeroPresets.Knight

  // Active upgrades
  val activePassives = mutableMapOf<PassiveSkillType, Int>()
  val acquiredItems = mutableSetOf<ItemId>()
  var randomCrystalPurchasedCount = 0

  // Bonus stats from Kristal Berkat drafts
  var bonusAtkPercent = 0f
  var bonusAtkSpeedPercent = 0f
  var bonusMaxHpFlat = 0f
  var bonusCritRate = 0f
  var bonusMoveSpeedPercent = 0f
  var bonusArmorPercent = 0f
  var bonusMagnetPercent = 0f

  // Combat Entities
  val mobs = mutableListOf<MobInstance>()
  val projectiles = mutableListOf<Projectile>()
  val slashWaves = mutableListOf<SlashWave>()
  val expGems = mutableListOf<ExpGem>()
  val damageIndicators = mutableListOf<DamageIndicator>()
  val particles = mutableListOf<Particle>()

  // Timers & Counters
  var survivalTimeSec = 0f
  var totalKills = 0
  var totalDamageDealt = 0f
  var gemsCollected = 0
  var bossesDefeated = 0

  var nextAttackTimer = 0f
  var spawnTimer = 0f
  var bossSpawnTimer = 60f
  var hpRegenTimer = 0f
  var playerHurtCooldown = 0f

  private var entityIdCounter = 1L

  // Phase & Dialog States
  private val _gamePhase = MutableStateFlow(GamePhase.MAIN_MENU)
  val gamePhase: StateFlow<GamePhase> = _gamePhase.asStateFlow()

  private val _skillUpgradeOptions = MutableStateFlow<List<SkillUpgradeOption>>(emptyList())
  val skillUpgradeOptions: StateFlow<List<SkillUpgradeOption>> = _skillUpgradeOptions.asStateFlow()

  private val _itemSelectionOptions = MutableStateFlow<List<ItemDefinition>>(emptyList())
  val itemSelectionOptions: StateFlow<List<ItemDefinition>> = _itemSelectionOptions.asStateFlow()

  private val _attributeDraftState = MutableStateFlow<AttributeDraftState?>(null)
  val attributeDraftState: StateFlow<AttributeDraftState?> = _attributeDraftState.asStateFlow()

  private var pendingDraftStep = 0

  fun selectHero(hero: HeroConfig) {
    currentHero = hero
  }

  fun startNewGame() {
    playerX = mapWidth / 2f
    playerY = mapHeight / 2f
    playerVx = 0f
    playerVy = 0f
    playerFacingAngle = 0f

    activePassives.clear()
    acquiredItems.clear()
    randomCrystalPurchasedCount = 0

    bonusAtkPercent = 0f
    bonusAtkSpeedPercent = 0f
    bonusMaxHpFlat = 0f
    bonusCritRate = 0f
    bonusMoveSpeedPercent = 0f
    bonusArmorPercent = 0f
    bonusMagnetPercent = 0f

    level = 1
    currentXp = 0
    requiredXp = calculateRequiredXp(level)

    recalculateStats()
    currentHp = maxHp

    mobs.clear()
    projectiles.clear()
    slashWaves.clear()
    expGems.clear()
    damageIndicators.clear()
    particles.clear()

    survivalTimeSec = 0f
    totalKills = 0
    totalDamageDealt = 0f
    gemsCollected = 0
    bossesDefeated = 0

    nextAttackTimer = 0f
    spawnTimer = 0f
    bossSpawnTimer = 45f
    hpRegenTimer = 0f
    playerHurtCooldown = 0f

    _gamePhase.value = GamePhase.PLAYING
  }

  fun pauseGame() {
    if (_gamePhase.value == GamePhase.PLAYING) {
      _gamePhase.value = GamePhase.PAUSED
    }
  }

  fun resumeGame() {
    if (_gamePhase.value == GamePhase.PAUSED) {
      _gamePhase.value = GamePhase.PLAYING
    }
  }

  fun returnToMenu() {
    _gamePhase.value = GamePhase.MAIN_MENU
  }

  private fun calculateRequiredXp(lvl: Int): Int {
    return (35 * (lvl.toDouble().pow(1.28))).toInt() + 10
  }

  fun recalculateStats() {
    val vitalityLvl = activePassives[PassiveSkillType.VITALITY] ?: 0
    val itemHpBonus = if (acquiredItems.contains(ItemId.PEMULIHAN)) 40f else 0f
    val oldMaxHp = maxHp

    maxHp = currentHero.baseMaxHp + (vitalityLvl * 25f) + itemHpBonus + bonusMaxHpFlat
    if (maxHp > oldMaxHp) {
      currentHp += (maxHp - oldMaxHp)
    }
    currentHp = currentHp.coerceIn(0f, maxHp)
  }

  fun getPlayerSpeed(): Float {
    val moveLvl = activePassives[PassiveSkillType.MOVE_SPEED] ?: 0
    return currentHero.baseSpeed * (1f + (moveLvl * 0.10f) + bonusMoveSpeedPercent)
  }

  fun getPlayerAttackDamage(): Float {
    val atkLvl = activePassives[PassiveSkillType.ATTACK_POWER] ?: 0
    return currentHero.baseAttackDamage * (1f + (atkLvl * 0.15f) + bonusAtkPercent)
  }

  fun getPlayerAttackCooldown(): Float {
    val speedLvl = activePassives[PassiveSkillType.ATTACK_SPEED] ?: 0
    val factor = 1f + (speedLvl * 0.12f) + bonusAtkSpeedPercent
    return (currentHero.baseAttackIntervalSec / factor).coerceAtLeast(0.12f)
  }

  fun getPlayerMagnetRadius(): Float {
    val magLvl = activePassives[PassiveSkillType.MAGNET_RANGE] ?: 0
    return 130f * (1f + (magLvl * 0.25f) + bonusMagnetPercent)
  }

  fun getPlayerCritRate(): Float {
    val critLvl = activePassives[PassiveSkillType.CRITICAL_STRIKE] ?: 0
    return (currentHero.baseCritRate + (critLvl * 0.07f) + bonusCritRate).coerceIn(0f, 0.90f)
  }

  fun getPlayerCritMultiplier(): Float {
    val critLvl = activePassives[PassiveSkillType.CRITICAL_STRIKE] ?: 0
    return currentHero.baseCritMultiplier + (critLvl * 0.20f)
  }

  fun getPlayerDamageReduction(): Float {
    val armorLvl = activePassives[PassiveSkillType.ARMOR_DEFENSE] ?: 0
    return ((armorLvl * 0.08f) + bonusArmorPercent).coerceIn(0f, 0.70f)
  }

  fun getPlayerAttackArea(): Float {
    val areaLvl = activePassives[PassiveSkillType.ATTACK_AREA] ?: 0
    return 1f + (areaLvl * 0.15f)
  }

  fun updateJoystickInput(dx: Float, dy: Float) {
    if (_gamePhase.value != GamePhase.PLAYING) {
      playerVx = 0f
      playerVy = 0f
      return
    }
    val len = sqrt(dx * dx + dy * dy)
    if (len > 0.01f) {
      val nx = dx / len
      val ny = dy / len
      val speed = getPlayerSpeed()
      playerVx = nx * speed
      playerVy = ny * speed
      playerFacingAngle = atan2(ny, nx)
    } else {
      playerVx = 0f
      playerVy = 0f
    }
  }

  fun update(dt: Float) {
    if (_gamePhase.value != GamePhase.PLAYING) return

    val clampedDt = dt.coerceIn(0.001f, 0.05f)
    survivalTimeSec += clampedDt

    // 1. Move Player
    playerX = (playerX + playerVx * clampedDt).coerceIn(40f, mapWidth - 40f)
    playerY = (playerY + playerVy * clampedDt).coerceIn(40f, mapHeight - 40f)

    if (playerHurtCooldown > 0f) {
      playerHurtCooldown -= clampedDt
    }

    // 2. Pemulihan Unique Passive Regeneration (10% max HP per sec)
    if (acquiredItems.contains(ItemId.PEMULIHAN)) {
      hpRegenTimer += clampedDt
      if (hpRegenTimer >= 1.0f) {
        hpRegenTimer -= 1.0f
        val regenAmount = maxHp * 0.10f
        if (currentHp < maxHp) {
          currentHp = (currentHp + regenAmount).coerceAtMost(maxHp)
          spawnDamageIndicator(playerX, playerY - 30f, "+${regenAmount.toInt()} HP", EmeraldHeal, false)
          spawnParticles(playerX, playerY, EmeraldHeal, 6)
        }
      }
    }

    // 3. Auto Weapon Attack
    nextAttackTimer -= clampedDt
    if (nextAttackTimer <= 0f) {
      triggerPlayerAttack()
      nextAttackTimer = getPlayerAttackCooldown()
    }

    // 4. Update Spawners
    updateMobSpawning(clampedDt)

    // 5. Update Mobs AI & Movement
    updateMobs(clampedDt)

    // 6. Update Projectiles & Slashes
    updateProjectiles(clampedDt)
    updateSlashes(clampedDt)

    // 7. Update XP Gems & Magnet
    updateExpGems(clampedDt)

    // 8. Update Damage indicators & particles
    updateEffects(clampedDt)

    // Check Player Death
    if (currentHp <= 0f) {
      currentHp = 0f
      _gamePhase.value = GamePhase.GAME_OVER
      soundManager.playGameOver()
    }
  }

  private fun triggerPlayerAttack() {
    val nearestMob = findNearestMob(playerX, playerY, currentHero.baseAttackRange * getPlayerAttackArea())
    val targetAngle = if (nearestMob != null) {
      atan2(nearestMob.y - playerY, nearestMob.x - playerX)
    } else {
      playerFacingAngle
    }
    playerFacingAngle = targetAngle

    val isCrit = Random.nextFloat() < getPlayerCritRate()
    val critMultiplier = if (isCrit) getPlayerCritMultiplier() else 1.0f
    val finalDmg = getPlayerAttackDamage() * critMultiplier

    when (currentHero.id) {
      HeroId.KNIGHT -> {
        // Melee Arc Cleave / Whirlwind
        val attackRadius = currentHero.baseAttackRange * getPlayerAttackArea()
        val sweep = (160.0 * PI / 180.0).toFloat()
        val slash = SlashWave(
          id = entityIdCounter++,
          x = playerX,
          y = playerY,
          angleRad = targetAngle,
          sweepRad = sweep,
          radius = attackRadius,
          damage = finalDmg,
          isCritical = isCrit
        )
        slashWaves.add(slash)
        soundManager.playSlash()
        spawnParticles(
          playerX + cos(targetAngle) * (attackRadius * 0.6f),
          playerY + sin(targetAngle) * (attackRadius * 0.6f),
          currentHero.accentColor,
          8
        )
      }
      HeroId.ARCHER -> {
        // Ranged Piercing Arrow
        val speed = 650f
        val vx = cos(targetAngle) * speed
        val vy = sin(targetAngle) * speed
        val area = getPlayerAttackArea()
        val proj = Projectile(
          id = entityIdCounter++,
          x = playerX,
          y = playerY,
          vx = vx,
          vy = vy,
          damage = finalDmg,
          radius = 12f * area,
          isPiercing = true,
          pierceCountLeft = (2 * area).toInt().coerceAtLeast(1),
          maxDistance = currentHero.baseAttackRange * 1.5f * area,
          color = currentHero.primaryColor,
          isCritical = isCrit
        )
        projectiles.add(proj)
        soundManager.playArrowShot()
      }
    }
  }

  private fun updateMobSpawning(dt: Float) {
    spawnTimer -= dt
    bossSpawnTimer -= dt

    // Difficulty scales with time survived
    val spawnInterval = (2.2f / (1f + (survivalTimeSec / 90f))).coerceAtLeast(0.45f)
    if (spawnTimer <= 0f) {
      spawnTimer = spawnInterval
      spawnMobWave()
    }

    if (bossSpawnTimer <= 0f) {
      bossSpawnTimer = 75f
      spawnBoss()
    }
  }

  private fun spawnMobWave() {
    val count = (2 + (survivalTimeSec / 35f).toInt()).coerceIn(2, 8)
    for (i in 0 until count) {
      val angle = Random.nextFloat() * 2f * PI.toFloat()
      val dist = Random.nextFloat() * 250f + 480f
      val sx = (playerX + cos(angle) * dist).coerceIn(50f, mapWidth - 50f)
      val sy = (playerY + sin(angle) * dist).coerceIn(50f, mapHeight - 50f)

      val roll = Random.nextFloat()
      val species = when {
        survivalTimeSec > 80f && roll < 0.22f -> MobSpecies.SKELETON_MAGE
        survivalTimeSec > 40f && roll < 0.45f -> MobSpecies.ORC_BRUTE
        survivalTimeSec > 20f && roll < 0.70f -> MobSpecies.SHADOW_BAT
        else -> MobSpecies.GOBLIN_SWARMER
      }

      val hpScale = 1f + (survivalTimeSec / 100f)
      val dmgScale = 1f + (survivalTimeSec / 120f)
      val mob = MobInstance(
        id = entityIdCounter++,
        species = species,
        x = sx,
        y = sy,
        hp = species.baseHp * hpScale,
        maxHp = species.baseHp * hpScale,
        speed = species.baseSpeed * (0.9f + Random.nextFloat() * 0.2f),
        damage = species.baseDamage * dmgScale,
        radius = species.radius
      )
      mobs.add(mob)
    }
  }

  private fun spawnBoss() {
    val angle = Random.nextFloat() * 2f * PI.toFloat()
    val dist = 520f
    val sx = (playerX + cos(angle) * dist).coerceIn(60f, mapWidth - 60f)
    val sy = (playerY + sin(angle) * dist).coerceIn(60f, mapHeight - 60f)

    val hpScale = 1f + (survivalTimeSec / 70f)
    val boss = MobInstance(
      id = entityIdCounter++,
      species = MobSpecies.ELITE_GOLEM_BOSS,
      x = sx,
      y = sy,
      hp = MobSpecies.ELITE_GOLEM_BOSS.baseHp * hpScale,
      maxHp = MobSpecies.ELITE_GOLEM_BOSS.baseHp * hpScale,
      speed = MobSpecies.ELITE_GOLEM_BOSS.baseSpeed,
      damage = MobSpecies.ELITE_GOLEM_BOSS.baseDamage,
      radius = MobSpecies.ELITE_GOLEM_BOSS.radius
    )
    mobs.add(boss)
  }

  private fun updateMobs(dt: Float) {
    val it = mobs.iterator()
    while (it.hasNext()) {
      val mob = it.next()
      if (mob.isDead) {
        it.remove()
        continue
      }

      if (mob.hitFlashTimer > 0f) {
        mob.hitFlashTimer -= dt
      }

      // Apply knockback decay
      mob.x += mob.knockbackVx * dt
      mob.y += mob.knockbackVy * dt
      mob.knockbackVx *= 0.85f
      mob.knockbackVy *= 0.85f

      // Chase player
      val dx = playerX - mob.x
      val dy = playerY - mob.y
      val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)

      if (dist > 1f) {
        val nx = dx / dist
        val ny = dy / dist
        mob.x = (mob.x + nx * mob.speed * dt).coerceIn(20f, mapWidth - 20f)
        mob.y = (mob.y + ny * mob.speed * dt).coerceIn(20f, mapHeight - 20f)
      }

      // Contact Damage to player
      if (dist < (mob.radius + 20f)) {
        if (playerHurtCooldown <= 0f) {
          val reduction = getPlayerDamageReduction()
          val netDamage = (mob.damage * (1f - reduction)).coerceAtLeast(1f)
          currentHp = (currentHp - netDamage).coerceAtLeast(0f)
          playerHurtCooldown = 0.55f
          soundManager.playPlayerHurt()
          spawnDamageIndicator(playerX, playerY - 35f, "-${netDamage.toInt()}", HealthRed, false)
          spawnParticles(playerX, playerY, HealthRed, 10)
        }
      }

      // Ranged Mobs shoot bullets
      if (mob.species.canRangedAttack) {
        mob.shootCooldown -= dt
        if (mob.shootCooldown <= 0f && dist < 420f) {
          mob.shootCooldown = 2.4f
          val bSpeed = 220f
          val bvx = (dx / dist) * bSpeed
          val bvy = (dy / dist) * bSpeed
          val enemyBullet = Projectile(
            id = entityIdCounter++,
            x = mob.x,
            y = mob.y,
            vx = bvx,
            vy = bvy,
            damage = mob.damage * 0.8f,
            radius = 10f,
            isPiercing = false,
            pierceCountLeft = 1,
            maxDistance = 500f,
            color = mob.species.color,
            isEnemyBullet = true
          )
          projectiles.add(enemyBullet)
        }
      }
    }
  }

  private fun updateProjectiles(dt: Float) {
    val it = projectiles.iterator()
    while (it.hasNext()) {
      val p = it.next()
      if (p.isExpired) {
        it.remove()
        continue
      }

      val stepDist = sqrt(p.vx * p.vx + p.vy * p.vy) * dt
      p.x += p.vx * dt
      p.y += p.vy * dt
      p.traveledDistance += stepDist

      if (p.traveledDistance >= p.maxDistance || p.x < 0 || p.x > mapWidth || p.y < 0 || p.y > mapHeight) {
        p.isExpired = true
        continue
      }

      if (p.isEnemyBullet) {
        // Collide with player
        val pdx = playerX - p.x
        val pdy = playerY - p.y
        val pdist = sqrt(pdx * pdx + pdy * pdy)
        if (pdist < (p.radius + 20f)) {
          val reduction = getPlayerDamageReduction()
          val netDamage = (p.damage * (1f - reduction)).coerceAtLeast(1f)
          currentHp = (currentHp - netDamage).coerceAtLeast(0f)
          soundManager.playPlayerHurt()
          spawnDamageIndicator(playerX, playerY - 35f, "-${netDamage.toInt()}", HealthRed, false)
          spawnParticles(playerX, playerY, p.color, 8)
          p.isExpired = true
        }
      } else {
        // Player projectile collides with mobs
        for (mob in mobs) {
          if (mob.isDead) continue
          val mdx = mob.x - p.x
          val mdy = mob.y - p.y
          val mdist = sqrt(mdx * mdx + mdy * mdy)
          if (mdist < (mob.radius + p.radius)) {
            applyDamageToMob(mob, p.damage, p.isCritical, p.x, p.y, p.vx * 0.3f, p.vy * 0.3f)
            p.pierceCountLeft--
            if (p.pierceCountLeft <= 0) {
              p.isExpired = true
              break
            }
          }
        }
      }
    }
  }

  private fun updateSlashes(dt: Float) {
    val it = slashWaves.iterator()
    while (it.hasNext()) {
      val slash = it.next()
      slash.lifetime += dt
      if (slash.lifetime >= slash.maxLifetime) {
        it.remove()
        continue
      }

      // Check collision with mobs in arc
      for (mob in mobs) {
        if (mob.isDead || slash.hitMobIds.contains(mob.id)) continue
        val dx = mob.x - slash.x
        val dy = mob.y - slash.y
        val dist = sqrt(dx * dx + dy * dy)
        if (dist <= (slash.radius + mob.radius)) {
          val mobAngle = atan2(dy, dx)
          var angleDiff = abs(mobAngle - slash.angleRad)
          while (angleDiff > PI) angleDiff -= (2 * PI).toFloat()
          angleDiff = abs(angleDiff)

          if (angleDiff <= (slash.sweepRad / 2f) + 0.3f) {
            slash.hitMobIds.add(mob.id)
            val knockDist = 280f
            val kx = cos(mobAngle) * knockDist
            val ky = sin(mobAngle) * knockDist
            applyDamageToMob(mob, slash.damage, slash.isCritical, mob.x, mob.y, kx, ky)
          }
        }
      }
    }
  }

  private fun applyDamageToMob(
    mob: MobInstance,
    damage: Float,
    isCrit: Boolean,
    hitX: Float,
    hitY: Float,
    kbX: Float,
    kbY: Float
  ) {
    mob.hp -= damage
    mob.hitFlashTimer = 0.15f
    mob.knockbackVx = kbX
    mob.knockbackVy = kbY
    totalDamageDealt += damage

    soundManager.playMobHit()
    spawnDamageIndicator(
      mob.x + Random.nextFloat() * 10f - 5f,
      mob.y - mob.radius - 10f,
      damage.toInt().toString(),
      if (isCrit) GoldAccent else TextPrimary,
      isCrit
    )
    spawnParticles(hitX, hitY, if (isCrit) GoldAccent else mob.species.color, if (isCrit) 8 else 4)

    if (mob.hp <= 0f) {
      mob.isDead = true
      totalKills++
      if (mob.species.isBoss) {
        bossesDefeated++
      }
      soundManager.playMobDeath()
      spawnParticles(mob.x, mob.y, mob.species.accentColor, if (mob.species.isBoss) 30 else 12)

      // Drop XP Gem
      val gemColor = when {
        mob.species.isBoss -> GoldAccent
        mob.species.xpValue >= 30 -> PurpleDark
        mob.species.xpValue >= 20 -> ExpBlue
        else -> EmeraldHeal
      }
      val gem = ExpGem(
        id = entityIdCounter++,
        x = mob.x,
        y = mob.y,
        xpValue = mob.species.xpValue,
        radius = if (mob.species.isBoss) 16f else 10f,
        color = gemColor
      )
      expGems.add(gem)
    }
  }

  private fun updateExpGems(dt: Float) {
    val magnetRadius = getPlayerMagnetRadius()
    val it = expGems.iterator()
    while (it.hasNext()) {
      val gem = it.next()
      if (gem.isCollected) {
        it.remove()
        continue
      }

      val dx = playerX - gem.x
      val dy = playerY - gem.y
      val dist = sqrt(dx * dx + dy * dy)

      if (dist < magnetRadius) {
        gem.isBeingMagnetized = true
      }

      if (gem.isBeingMagnetized) {
        gem.speed = (gem.speed + 800f * dt).coerceAtMost(700f)
        val nx = dx / dist.coerceAtLeast(1f)
        val ny = dy / dist.coerceAtLeast(1f)
        gem.x += nx * gem.speed * dt
        gem.y += ny * gem.speed * dt

        if (dist < 28f) {
          gem.isCollected = true
          addXp(gem.xpValue)
          gemsCollected++
          soundManager.playGemPickup()
          spawnParticles(playerX, playerY, gem.color, 4)
        }
      }
    }
  }

  private fun addXp(amount: Int) {
    currentXp += amount
    if (currentXp >= requiredXp) {
      currentXp -= requiredXp
      level++
      requiredXp = calculateRequiredXp(level)
      onLevelUpTriggered()
    }
  }

  private fun onLevelUpTriggered() {
    // Check if milestone level (Level 5, 10, 15, 20, ...)
    if (level % 5 == 0) {
      // Milestone level -> Item Selection (1 Item)
      soundManager.playItemFanfare()
      prepareItemSelectionOptions()
      _gamePhase.value = GamePhase.ITEM_SELECT
    } else {
      // Regular level -> Passive Skill Selection (Pick new or upgrade existing)
      soundManager.playLevelUp()
      prepareSkillUpgradeOptions()
      _gamePhase.value = GamePhase.LEVEL_UP_SKILL_SELECT
    }
  }

  private fun prepareSkillUpgradeOptions() {
    val availableTypes = PassiveSkillType.values().filter { type ->
      val curLvl = activePassives[type] ?: 0
      curLvl < type.maxLevel
    }

    val chosenTypes = availableTypes.shuffled().take(3)
    val options = chosenTypes.map { type ->
      val curLvl = activePassives[type] ?: 0
      val isNew = curLvl == 0
      val targetLvl = curLvl + 1
      SkillUpgradeOption(
        type = type,
        isNew = isNew,
        currentLevel = curLvl,
        targetLevel = targetLvl,
        description = type.descriptionTemplate(targetLvl)
      )
    }
    _skillUpgradeOptions.value = options
  }

  fun selectSkillUpgrade(option: SkillUpgradeOption) {
    val curLvl = activePassives[option.type] ?: 0
    val newLvl = (curLvl + 1).coerceAtMost(option.type.maxLevel)
    activePassives[option.type] = newLvl
    recalculateStats()

    if (option.type == PassiveSkillType.VITALITY) {
      currentHp = (currentHp + 25f).coerceAtMost(maxHp)
    }

    _skillUpgradeOptions.value = emptyList()
    _gamePhase.value = GamePhase.PLAYING
  }

  private fun prepareItemSelectionOptions() {
    // "cuma bisa pilih 1 kali per item jadi gak bisa ditumpuk (kecuali item atribut acak)"
    val availableItems = mutableListOf<ItemDefinition>()

    // Check Item 1 "Pemulihan"
    if (!acquiredItems.contains(ItemId.PEMULIHAN)) {
      availableItems.add(GameItems.Pemulihan)
    }

    // Item 2 "Kristal Berkat Acak" can be chosen infinitely
    availableItems.add(GameItems.KristalBerkat)

    _itemSelectionOptions.value = availableItems
  }

  fun selectItem(item: ItemDefinition) {
    when (item.id) {
      ItemId.PEMULIHAN -> {
        acquiredItems.add(ItemId.PEMULIHAN)
        recalculateStats()
        currentHp = (currentHp + 40f).coerceAtMost(maxHp)
        soundManager.playItemFanfare()
        _itemSelectionOptions.value = emptyList()
        _gamePhase.value = GamePhase.PLAYING
      }
      ItemId.KRISTAL_BERKAT -> {
        // "jika pilih item ini bisa pilih 4 atribut acak dan bisa pilih 4 kali"
        randomCrystalPurchasedCount++
        _itemSelectionOptions.value = emptyList()
        pendingDraftStep = 1
        prepareAttributeDraftStep(pendingDraftStep)
        _gamePhase.value = GamePhase.ATTRIBUTE_DRAFT
      }
    }
  }

  private fun prepareAttributeDraftStep(step: Int) {
    val randomOptions = AttributeOptionType.values().toList().shuffled().take(3)
    _attributeDraftState.value = AttributeDraftState(
      currentStep = step,
      totalSteps = 4,
      currentOptions = randomOptions
    )
  }

  fun selectDraftAttribute(option: AttributeOptionType) {
    when (option) {
      AttributeOptionType.ATK_BOOST -> bonusAtkPercent += 0.10f
      AttributeOptionType.ATK_SPEED_BOOST -> bonusAtkSpeedPercent += 0.08f
      AttributeOptionType.MAX_HP_BOOST -> {
        bonusMaxHpFlat += 25f
        recalculateStats()
        currentHp = (currentHp + 25f).coerceAtMost(maxHp)
      }
      AttributeOptionType.CRIT_BOOST -> bonusCritRate += 0.06f
      AttributeOptionType.SPEED_BOOST -> bonusMoveSpeedPercent += 0.08f
      AttributeOptionType.DEFENSE_BOOST -> bonusArmorPercent += 0.06f
      AttributeOptionType.MAGNET_BOOST -> bonusMagnetPercent += 0.20f
    }
    recalculateStats()
    soundManager.playGemPickup()

    if (pendingDraftStep < 4) {
      pendingDraftStep++
      prepareAttributeDraftStep(pendingDraftStep)
    } else {
      // Completed all 4 attribute drafts!
      _attributeDraftState.value = null
      _gamePhase.value = GamePhase.PLAYING
    }
  }

  private fun updateEffects(dt: Float) {
    val dIt = damageIndicators.iterator()
    while (dIt.hasNext()) {
      val d = dIt.next()
      d.lifetime += dt
      d.y += d.vy * dt
      if (d.lifetime >= d.maxLifetime) {
        dIt.remove()
      }
    }

    val pIt = particles.iterator()
    while (pIt.hasNext()) {
      val p = pIt.next()
      p.lifetime += dt
      p.x += p.vx * dt
      p.y += p.vy * dt
      if (p.lifetime >= p.maxLifetime) {
        pIt.remove()
      }
    }
  }

  private fun spawnDamageIndicator(x: Float, y: Float, text: String, color: androidx.compose.ui.graphics.Color, isCrit: Boolean) {
    val ind = DamageIndicator(
      id = entityIdCounter++,
      x = x,
      y = y,
      text = text,
      color = color,
      isCritical = isCrit
    )
    damageIndicators.add(ind)
  }

  private fun spawnParticles(x: Float, y: Float, color: androidx.compose.ui.graphics.Color, count: Int) {
    for (i in 0 until count) {
      val angle = Random.nextFloat() * 2f * PI.toFloat()
      val speed = Random.nextFloat() * 180f + 40f
      val particle = Particle(
        id = entityIdCounter++,
        x = x,
        y = y,
        vx = cos(angle) * speed,
        vy = sin(angle) * speed,
        radius = Random.nextFloat() * 4f + 3f,
        color = color,
        maxLifetime = Random.nextFloat() * 0.35f + 0.25f
      )
      particles.add(particle)
    }
  }

  private fun findNearestMob(fromX: Float, fromY: Float, maxRange: Float): MobInstance? {
    var nearest: MobInstance? = null
    var minD2 = maxRange * maxRange
    for (m in mobs) {
      if (m.isDead) continue
      val dx = m.x - fromX
      val dy = m.y - fromY
      val d2 = dx * dx + dy * dy
      if (d2 < minD2) {
        minD2 = d2
        nearest = m
      }
    }
    return nearest
  }
}
