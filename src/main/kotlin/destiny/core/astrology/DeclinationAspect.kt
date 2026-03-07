package destiny.core.astrology

import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.Serializable

/**
 * 赤緯相位類型
 * - PARALLEL: 兩星赤緯相近（同側），功能類似合相
 * - CONTRA_PARALLEL: 兩星赤緯等值但異側，功能類似對沖
 */
@Serializable
enum class DeclinationAspectType {
  PARALLEL,
  CONTRA_PARALLEL
}

/**
 * 赤緯相位（Parallel / Contra-parallel），獨立於黃經相位系統。
 * 用於標注 OOB Ingress 事件發生時，過運星體與本命星體的赤緯關聯。
 */
@Serializable
data class DeclinationAspect(
  val transitPoint: AstroPoint,
  val natalPoint: AstroPoint,
  val type: DeclinationAspectType,
  /** 過運星體在事件發生時的赤緯 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val transitDeclination: Double,
  /** 本命星體的赤緯 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val natalDeclination: Double,
  /** 容許度（度） */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
) {
  companion object {
    /** 預設容許度 1.5°（OOB crossing 固定在 ±23.44°，範圍窄，需稍寬容許度） */
    const val DEFAULT_ORB = 1.5

    /**
     * 計算過運星體與本命星體之間的赤緯相位。
     * @return Parallel 或 Contra-parallel（若在容許度內），否則 null
     */
    fun calculate(
      transitPoint: AstroPoint,
      natalPoint: AstroPoint,
      transitDecl: Double,
      natalDecl: Double,
      orb: Double = DEFAULT_ORB,
    ): DeclinationAspect? {
      val parallelOrb = kotlin.math.abs(transitDecl - natalDecl)
      val contraParallelOrb = kotlin.math.abs(transitDecl + natalDecl)
      return when {
        parallelOrb <= orb -> DeclinationAspect(transitPoint, natalPoint, DeclinationAspectType.PARALLEL, transitDecl, natalDecl, parallelOrb)
        contraParallelOrb <= orb -> DeclinationAspect(transitPoint, natalPoint, DeclinationAspectType.CONTRA_PARALLEL, transitDecl, natalDecl, contraParallelOrb)
        else -> null
      }
    }
  }
}
