package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.astrology.AspectData
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.eightwords.FlowPattern
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.Reacting

/**
 * 擇日、則時 Event
 */
sealed class DayHourEvent {

  abstract val gmtJulDay: GmtJulDay

  enum class Type {
    GOOD,
    BAD
  }

  abstract val type: Type

  enum class Span {
    DAY,
    HOUR, // 八字時辰 , unit = 2Hour
    INSTANT
  }

  abstract val span: Span

  sealed class EwEvent(val flowPattern: FlowPattern) : DayHourEvent() {
    abstract val outerScale: Scale
    /** 是否與本命時柱相關 */
    abstract val hourRelated: Boolean
    abstract val outer: IEightWords
    override val span: Span
      get() = when (outerScale) {
        Scale.DAY  -> Span.DAY
        Scale.HOUR -> Span.HOUR
        else       -> throw IllegalArgumentException("impossible")
      }

    /** 本命某天干, 同時受到 流日、流時 的影響 */
    data class StemAffecting(override val gmtJulDay: GmtJulDay, val pattern: FlowPattern.Affecting, override val outer: IEightWords) : EwEvent(pattern) {
      override val outerScale: Scale = Scale.HOUR
      override val hourRelated: Boolean = pattern.scale == Scale.HOUR
      override val type: Type
        get() = when (pattern.reacting) {
          Reacting.SAME, Reacting.PRODUCED                         -> Type.GOOD
          Reacting.BEATEN, Reacting.PRODUCING, Reacting.DOMINATING -> Type.BAD
        }
    }

    /** 天干五合 , 限定只比對流日、流時的天干 , 對本命哪個 天干 五合 */
    data class StemCombined(override val gmtJulDay: GmtJulDay, val pattern: FlowPattern.StemCombined, override val outer: IEightWords) : EwEvent(pattern) {
      override val type: Type = Type.GOOD
      override val hourRelated: Boolean = pattern.scale == Scale.HOUR
      override val outerScale: Scale
        get() = when (pattern.flowScale) {
          FlowScale.DAY  -> Scale.DAY
          FlowScale.HOUR -> Scale.HOUR
          else           -> throw IllegalArgumentException("impossible")
        }
    }

    /** 地支六合 , 限定只比對 流日、流時的地支 , 對本命哪個 地支 六合 */
    data class BranchCombined(override val gmtJulDay: GmtJulDay, val pattern: FlowPattern.BranchCombined, override val outer: IEightWords) : EwEvent(pattern) {
      override val type: Type = Type.GOOD
      override val hourRelated: Boolean = pattern.scale == Scale.HOUR
      override val outerScale: Scale
        get() = when (pattern.flowScale) {
          FlowScale.DAY  -> Scale.DAY
          FlowScale.HOUR -> Scale.HOUR
          else           -> throw IllegalArgumentException("impossible")
        }
    }

    /** 地支三合 , 限定比對 哪個流日 or 流時 , 對本命兩個地支造成三合 */
    data class TrilogyToFlow(override val gmtJulDay: GmtJulDay, val pattern: FlowPattern.TrilogyToFlow, override val outer: IEightWords) : EwEvent(pattern) {
      override val type: Type = Type.GOOD
      override val hourRelated: Boolean = pattern.pairs.map { pair -> pair.first }.any { scale -> scale == Scale.HOUR }
      override val outerScale: Scale
        get() = when (pattern.flow.first) {
          FlowScale.DAY  -> Scale.DAY
          FlowScale.HOUR -> Scale.HOUR
          else           -> throw IllegalArgumentException("impossible")
        }

    }

    /** 本命某地支 , 與流運 某兩柱 (其中一個必須是 DAY or HOUR) 形成三合 , 其中，限制 [FlowPattern.ToFlowTrilogy.flows] 必須至少包含 [FlowScale.DAY] or [FlowScale.HOUR] */
    data class ToFlowTrilogy(override val gmtJulDay: GmtJulDay, val pattern: FlowPattern.ToFlowTrilogy, override val outer: IEightWords) : EwEvent(pattern) {
      override val type: Type = Type.GOOD
      override val hourRelated: Boolean = pattern.scale == Scale.HOUR
      override val outerScale: Scale
        get() {
          val flowScales: List<FlowScale> = pattern.flows.map { it.first }
          return if (flowScales.contains(FlowScale.HOUR)) {
            Scale.HOUR
          } else if (flowScales.contains(FlowScale.DAY)) {
            Scale.DAY
          } else {
            throw IllegalArgumentException("impossible")
          }
        }
    }

    /** 地支正沖 , 哪個流日 or 流時 , 正沖本命哪 地支 */
    data class BranchOpposition(override val gmtJulDay: GmtJulDay, val pattern: FlowPattern.BranchOpposition, override val outer: IEightWords) : EwEvent(pattern) {
      override val type: Type = Type.BAD
      override val hourRelated: Boolean = pattern.scale == Scale.HOUR
      override val outerScale: Scale
        get() = when (pattern.flowScale) {
          FlowScale.DAY  -> Scale.DAY
          FlowScale.HOUR -> Scale.HOUR
          else           -> throw IllegalArgumentException("impossible")
        }
    }
  }

  /** 占星事件 */
  data class AstroEvent(override val type: Type, val aspectData: AspectData) : DayHourEvent() {
    override val span: Span = Span.INSTANT
    override val gmtJulDay: GmtJulDay = aspectData.gmtJulDay
  }
}
