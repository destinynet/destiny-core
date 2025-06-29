/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.SynastryAspect
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.IZodiacDegreeTwoDecimalSerializer
import kotlinx.serialization.Serializable


/**
 * 代表一張在特定時間點的主限盤 (Primary Direction Chart) 模型
 */
sealed interface IPrimaryDirectionModel : ITransitModel {
  /** 本命盤的 GMT 時間 */
  val natalGmtJulDay: GmtJulDay

  /** 查看的流年時間點 (例如，今天) */
  val viewGmtJulDay: GmtJulDay

  /** 本次計算所使用的「時間鑰匙」(例如，托勒密之鑰) */
  val timeKey: ITimeKey

  /**
   * 根據 viewGmtJulDay 與 natalGmtJulDay 的時間差，以及 timeKey，所計算出的推進弧角。
   * 這個弧角通常是在赤道座標系統上的度數。
   */
  val directionArc: Double

  /**
   * 推進後，所有星體在黃道上的新位置。
   * 這是主限盤的核心數據。
   */
  val positionMap: Map<AstroPoint, IZodiacDegree>

  /** 推進後的星盤 (Directed Chart) 與本命盤 (Natal Chart) 形成的相位列表 */
  val synastryAspects: List<SynastryAspect>
}

@Serializable
data class PrimaryDirectionModel(
  override val natalGmtJulDay: GmtJulDay,
  override val viewGmtJulDay: GmtJulDay,
  override val timeKey: ITimeKey,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val directionArc: Double,
  override val positionMap: Map<AstroPoint, @Serializable(with = IZodiacDegreeTwoDecimalSerializer::class) IZodiacDegree>,
  override val synastryAspects: List<SynastryAspect>
) : IPrimaryDirectionModel {
  override val natalTime: GmtJulDay
    get() = natalGmtJulDay
  override val viewTime: GmtJulDay
    get() = viewGmtJulDay
}
