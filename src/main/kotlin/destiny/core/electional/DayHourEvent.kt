package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipse
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.IEvent
import destiny.core.calendar.IEventSpan
import destiny.core.calendar.eightwords.FlowPattern
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsPattern
import destiny.core.calendar.eightwords.IdentityPattern

/**
 * 擇日、擇時 Event
 */
sealed class DayHourEvent : IEvent {

  enum class Span {
    DAY,
    HOURS, // 數小時
    INSTANT
  }

  abstract val span: Span

  abstract val impact: Impact

  sealed class EwEvent : DayHourEvent() {
    abstract val pattern: IEightWordsPattern
    abstract val outer: IEightWords

    sealed class EwPersonalEvent : EwEvent() {
      override val impact: Impact = Impact.PERSONAL

      abstract val outerScale: Scale

      /** 是否與本命時柱相關 */
      abstract val hourRelated: Boolean

      override val span: Span
        get() = when (outerScale) {
          Scale.DAY  -> Span.DAY
          Scale.HOUR -> Span.HOURS
          else       -> throw IllegalArgumentException("impossible span")
        }

      /** 本命某天干, 同時受到 流日、流時 的影響 */
      data class StemAffecting(override val begin: GmtJulDay, override val pattern: FlowPattern.Affecting, override val outer: IEightWords) : EwPersonalEvent() {
        override val outerScale: Scale = Scale.HOUR
        override val hourRelated: Boolean = pattern.scale == Scale.HOUR
      }

      /** 天干五合 , 限定只比對流日、流時的天干 , 對本命哪個 天干 五合 */
      data class StemCombined(override val begin: GmtJulDay, override val pattern: FlowPattern.StemCombined, override val outer: IEightWords) : EwPersonalEvent() {
        override val hourRelated: Boolean = pattern.scale == Scale.HOUR
        override val outerScale: Scale
          get() = when (pattern.flowScale) {
            FlowScale.DAY  -> Scale.DAY
            FlowScale.HOUR -> Scale.HOUR
            else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
          }
      }

      /** 地支六合 , 限定只比對 流日、流時的地支 , 對本命哪個 地支 六合 */
      data class BranchCombined(override val begin: GmtJulDay, override val pattern: FlowPattern.BranchCombined, override val outer: IEightWords) : EwPersonalEvent() {
        override val hourRelated: Boolean = pattern.scale == Scale.HOUR
        override val outerScale: Scale
          get() = when (pattern.flowScale) {
            FlowScale.DAY  -> Scale.DAY
            FlowScale.HOUR -> Scale.HOUR
            else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
          }
      }

      /** 地支三合 , 限定比對 哪個流日 or 流時 , 對本命兩個地支造成三合 */
      data class TrilogyToFlow(override val begin: GmtJulDay, override val pattern: FlowPattern.TrilogyToFlow, override val outer: IEightWords) : EwPersonalEvent() {
        override val hourRelated: Boolean = pattern.pairs.map { pair -> pair.first }.any { scale -> scale == Scale.HOUR }
        override val outerScale: Scale
          get() = when (pattern.flow.first) {
            FlowScale.DAY  -> Scale.DAY
            FlowScale.HOUR -> Scale.HOUR
            else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
          }

      }

      /** 本命某地支 , 與流運 某兩柱 (其中一個必須是 DAY or HOUR) 形成三合 , 其中，限制 [FlowPattern.ToFlowTrilogy.flows] 必須至少包含 [FlowScale.DAY] or [FlowScale.HOUR] */
      data class ToFlowTrilogy(override val begin: GmtJulDay, override val pattern: FlowPattern.ToFlowTrilogy, override val outer: IEightWords) : EwPersonalEvent() {
        override val hourRelated: Boolean = pattern.scale == Scale.HOUR
        override val outerScale: Scale
          get() {
            val flowScales: List<FlowScale> = pattern.flows.map { it.first }
            return if (flowScales.contains(FlowScale.HOUR)) {
              Scale.HOUR
            } else if (flowScales.contains(FlowScale.DAY)) {
              Scale.DAY
            } else {
              throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
            }
          }
      }

      /** 地支正沖 , 哪個流日 or 流時 , 正沖本命哪 地支 */
      data class BranchOpposition(override val begin: GmtJulDay, override val pattern: FlowPattern.BranchOpposition, override val outer: IEightWords) : EwPersonalEvent() {
        override val hourRelated: Boolean = pattern.scale == Scale.HOUR
        override val outerScale: Scale
          get() = when (pattern.flowScale) {
            FlowScale.DAY  -> Scale.DAY
            FlowScale.HOUR -> Scale.HOUR
            else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
          }
      }

      companion object {
        private const val IMPOSSIBLE_FLOW_SCALE = "impossible flowScale"
      }
    }

    sealed class EwGlobalEvent : EwEvent() {
      override val impact: Impact = Impact.GLOBAL

      data class StemCombined(override val begin: GmtJulDay, override val pattern: IdentityPattern.StemCombined, override val outer: IEightWords) : EwGlobalEvent() {
        override val span: Span = pattern.pillars.map { it.first }.max().let {
          when (it) {
            Scale.DAY               -> Span.DAY
            Scale.HOUR              -> Span.HOURS
            Scale.YEAR, Scale.MONTH -> throw IllegalArgumentException(NOT_SUPPORTED)
          }
        }
      }

      data class BranchCombined(override val begin: GmtJulDay, override val pattern: IdentityPattern.BranchCombined, override val outer: IEightWords) : EwGlobalEvent() {
        override val span: Span = pattern.pillars.map { it.first }.max().let {
          when (it) {
            Scale.DAY               -> Span.DAY
            Scale.HOUR              -> Span.HOURS
            Scale.YEAR, Scale.MONTH -> throw IllegalArgumentException(NOT_SUPPORTED)
          }
        }
      }

      data class Trilogy(override val begin: GmtJulDay, override val pattern: IdentityPattern.Trilogy, override val outer: IEightWords) : EwGlobalEvent() {
        override val span: Span = pattern.pillars.map { it.first }.max().let {
          when (it) {
            Scale.DAY               -> Span.DAY
            Scale.HOUR              -> Span.HOURS
            Scale.YEAR, Scale.MONTH -> throw IllegalArgumentException(NOT_SUPPORTED)
          }
        }
      }

      data class BranchOpposition(override val begin: GmtJulDay, override val pattern: IdentityPattern.BranchOpposition, override val outer: IEightWords) : EwGlobalEvent() {
        override val span: Span = pattern.pillars.map { it.first }.max().let {
          when (it) {
            Scale.DAY               -> Span.DAY
            Scale.HOUR              -> Span.HOURS
            Scale.YEAR, Scale.MONTH -> throw IllegalArgumentException(NOT_SUPPORTED)
          }
        }
      }

      data class StemRooted(override val begin: GmtJulDay, override val pattern: IdentityPattern.StemRooted, override val outer: IEightWords) : EwGlobalEvent() {
        override val span: Span
          get() = when (
            setOf(
              pattern.scale,
              pattern.roots.map { it.first }.max()
            ).max()
          ) {
            Scale.DAY               -> Span.DAY
            Scale.HOUR              -> Span.HOURS
            Scale.YEAR, Scale.MONTH -> throw IllegalArgumentException(NOT_SUPPORTED)
          }
      }

      /** 吉祥日 : 天赦日 , 玉堂日 ... */
      data class AuspiciousDay(override val begin: GmtJulDay, override val pattern : IdentityPattern.AuspiciousPattern, override val outer: IEightWords) : EwGlobalEvent() {
        override val span: Span = Span.DAY
      }

      /** 不祥日 */
      data class InauspiciousDay(override val begin: GmtJulDay, override val pattern: IdentityPattern.InauspiciousPattern, override val outer: IEightWords) : EwGlobalEvent() {
        override val span: Span = Span.DAY
      }

      companion object {
        private const val NOT_SUPPORTED = "not supported"
      }
    }
  }

  /** 占星事件 */
  sealed class AstroEvent : DayHourEvent() {

    /** 交角 */
    data class AspectEvent(val aspectData: AspectData, override val impact: Impact) : AstroEvent() {
      override val span: Span = Span.INSTANT
      override val begin: GmtJulDay = aspectData.gmtJulDay
    }

    /** 月亮空亡 */
    data class MoonVoc(val voidCourseSpan: Misc.VoidCourseSpan) : AstroEvent(), IEventSpan {
      override val begin: GmtJulDay = voidCourseSpan.begin
      override val end: GmtJulDay = voidCourseSpan.end
      override val span: Span = Span.HOURS
      override val impact: Impact = Impact.GLOBAL
    }

    /** 星體滯留 */
    data class PlanetStationary(val stationary: Stationary, val zodiacDegree: IZodiacDegree, val transitToNatalAspects: List<SynastryAspect>) : AstroEvent() {
      override val begin: GmtJulDay = stationary.begin
      override val span: Span = Span.INSTANT
      override val impact: Impact = Impact.GLOBAL
    }

    /** 當日星體逆行 */
    data class PlanetRetrograde(val planet: Planet, override val begin: GmtJulDay, val progress: Double) : AstroEvent() {
      override val span: Span = Span.DAY
      override val impact: Impact = Impact.GLOBAL
    }

    /** 日食 or 月食 */
    data class Eclipse(val eclipse: IEclipse, val zodiacDegree: IZodiacDegree, val transitToNatalAspects: List<SynastryAspect>) : AstroEvent() {
      override val span: Span = Span.HOURS
      override val impact: Impact = Impact.GLOBAL
      override val begin: GmtJulDay = eclipse.max
    }

    /** 月相 */
    data class LunarPhaseEvent(val phase: LunarPhase,
                               val zodiacDegree: IZodiacDegree,
                               override val begin: GmtJulDay,
                               val transitToNatalAspects: List<SynastryAspect>) : AstroEvent() {
      override val span: Span = Span.INSTANT
      override val impact: Impact = Impact.GLOBAL
                               }
  }

}
