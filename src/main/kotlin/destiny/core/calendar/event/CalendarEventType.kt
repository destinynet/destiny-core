/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core.calendar.event

import destiny.core.astrology.LunarPhase
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.calendar.SolarTerms
import destiny.tools.serializers.astrology.PlanetSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 事件所屬的體系；決定歸日的規則（八字以日柱換日、占星以當地日期） */
enum class CalendarDomain {
  ASTRO
}

/**
 * 事件種類。一個種類對應一個計算來源 [ICalendarEventSource]。
 * 新增體系的事件（八字、通書…）＝新增種類＋新增來源實作。
 */
enum class CalendarEventKind(val domain: CalendarDomain) {
  RETROGRADE(CalendarDomain.ASTRO),
  SOLAR_ECLIPSE(CalendarDomain.ASTRO),
  LUNAR_ECLIPSE(CalendarDomain.ASTRO),
  LUNATION(CalendarDomain.ASTRO),
  SOLAR_TERM(CalendarDomain.ASTRO),
  VOID_OF_COURSE(CalendarDomain.ASTRO),
}

/**
 * 事件系列：同一系列的事件彼此只差在錨點。例：水星逆行、滿月、日食、立冬、月亮空亡（中世紀派）。
 *
 * 系列帶著自己的參數 —— 例如空亡的派別是 [VoidOfCourse.impl]，
 * 所以多派並存只是多幾個系列，事件的識別仍是「系列＋錨點」。
 *
 * [code] 是穩定的字串表示（持久化用），[ofCode] 是唯一的解析點。
 */
@Serializable
sealed interface CalendarEventType {
  val kind: CalendarEventKind

  val code: String

  /** 星體逆行週期 */
  @Serializable
  @SerialName("RETROGRADE")
  data class Retrograde(@Serializable(with = PlanetSerializer::class) val planet: Planet) : CalendarEventType {
    init {
      require(planet in RETROGRADE_PLANETS) { "$planet does not retrograde" }
    }

    override val kind: CalendarEventKind get() = CalendarEventKind.RETROGRADE
    override val code: String get() = "${kind.name}$SEP${planet.nameKey}"
  }

  @Serializable
  @SerialName("SOLAR_ECLIPSE")
  data object SolarEclipse : CalendarEventType {
    override val kind: CalendarEventKind get() = CalendarEventKind.SOLAR_ECLIPSE
    override val code: String get() = kind.name
  }

  @Serializable
  @SerialName("LUNAR_ECLIPSE")
  data object LunarEclipse : CalendarEventType {
    override val kind: CalendarEventKind get() = CalendarEventKind.LUNAR_ECLIPSE
    override val code: String get() = kind.name
  }

  /** 朔、上弦、望、下弦 */
  @Serializable
  @SerialName("LUNATION")
  data class Lunation(val phase: LunarPhase) : CalendarEventType {
    override val kind: CalendarEventKind get() = CalendarEventKind.LUNATION
    override val code: String get() = "${kind.name}$SEP${phase.name}"
  }

  @Serializable
  @SerialName("SOLAR_TERM")
  data class SolarTerm(val term: SolarTerms) : CalendarEventType {
    override val kind: CalendarEventKind get() = CalendarEventKind.SOLAR_TERM
    override val code: String get() = "${kind.name}$SEP${term.name}"
  }

  /** 星體空亡；派別不同，算出來的空亡就不同，所以派別是系列的一部分 */
  @Serializable
  @SerialName("VOID_OF_COURSE")
  data class VoidOfCourse(
    @Serializable(with = PlanetSerializer::class)
    val planet: Planet = Planet.MOON,
    val impl: VoidCourseImpl = VoidCourseImpl.Medieval
  ) : CalendarEventType {
    override val kind: CalendarEventKind get() = CalendarEventKind.VOID_OF_COURSE
    override val code: String get() = "${kind.name}$SEP${planet.nameKey}$SEP${impl.name}"
  }

  companion object {
    private const val SEP = ':'

    /** 會逆行的星體（日月不逆行） */
    val RETROGRADE_PLANETS: List<Planet> = listOf(
      Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN, Planet.URANUS, Planet.NEPTUNE, Planet.PLUTO
    )

    /** 事件曆預設收錄的系列：八星逆行、日月食、四種月相、二十四節氣、月亮空亡（中世紀派） */
    val defaults: Set<CalendarEventType> by lazy {
      buildSet {
        RETROGRADE_PLANETS.forEach { add(Retrograde(it)) }
        add(SolarEclipse)
        add(LunarEclipse)
        LunarPhase.entries.forEach { add(Lunation(it)) }
        SolarTerms.entries.forEach { add(SolarTerm(it)) }
        add(VoidOfCourse())
      }
    }

    /** [code] 的反函數；不認得回 null */
    fun ofCode(code: String): CalendarEventType? {
      val parts = code.split(SEP)
      val kind = CalendarEventKind.entries.firstOrNull { it.name == parts[0] } ?: return null
      return runCatching {
        when (kind) {
          CalendarEventKind.RETROGRADE     -> parts.takeIf { it.size == 2 }?.let { Planet.valueOf(it[1]) }?.let { Retrograde(it) }
          CalendarEventKind.SOLAR_ECLIPSE  -> SolarEclipse.takeIf { parts.size == 1 }
          CalendarEventKind.LUNAR_ECLIPSE  -> LunarEclipse.takeIf { parts.size == 1 }
          CalendarEventKind.LUNATION       -> parts.takeIf { it.size == 2 }?.let { Lunation(LunarPhase.valueOf(it[1])) }
          CalendarEventKind.SOLAR_TERM     -> parts.takeIf { it.size == 2 }?.let { SolarTerm(SolarTerms.valueOf(it[1])) }
          CalendarEventKind.VOID_OF_COURSE -> parts.takeIf { it.size == 3 }?.let { p ->
            Planet.valueOf(p[1])?.let { VoidOfCourse(it, VoidCourseImpl.valueOf(p[2])) }
          }
        }
      }.getOrNull()
    }
  }
}
