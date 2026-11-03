/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.Axis
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign

/**
 * 區分（sect）：日夜盤把七顆古典行星分成兩隊。
 *
 * - [sect]：盤的日夜
 * - [light]：區分光體（日盤太陽、夜盤月亮）
 * - [inSect]：與盤同一隊的星（含光體；水星依 [mercurySect] 歸隊）
 * - [contrary]：另一隊
 * - [beneficOfSect]：同區分的吉星（日盤木星、夜盤金星）—— 最能給予的那顆
 * - [maleficContrary]：異區分的凶星（日盤火星、夜盤土星）—— 最難纏的那顆
 * - [mercurySect]：水星這張盤歸哪一隊（先於太陽升起 ＝ 東出 ＝ 晝星；之後升起 ＝ 夜星）
 */
data class Sect(
  val sect: DayNight,
  val light: Planet,
  val inSect: List<Planet>,
  val contrary: List<Planet>,
  val beneficOfSect: Planet,
  val maleficContrary: Planet,
  val mercurySect: DayNight?,
) : ClassicalFact

/**
 * 得時（hayz）：[planet] 的晝夜歸屬（[planetSect]）、它在地平線上下（[aboveHorizon]）、所在星座的陰陽（[sign]）三者都合宜。
 *
 * - [chartSect]：盤的日夜
 * - [planetSect]：這顆星的晝夜歸屬（水星依東出西入）
 * - [aboveHorizon]：在地平線上（以上升／下降點的黃經判，不看宮位制）
 * - [masculineSign]：所在星座是陽性；[wantMasculine] 是這顆星要的陰陽（由 [HayzOptions.gender] 決定）
 */
data class Hayz(
  val planet: Planet,
  val chartSect: DayNight,
  val planetSect: DayNight,
  val aboveHorizon: Boolean,
  val sign: ZodiacSign,
  val masculineSign: Boolean,
  val wantMasculine: Boolean,
) : ClassicalFact

/**
 * 得時的判準（定義分歧，見 KDoc；實務只用一組）。
 *
 * - [hemisphere]：地平線上下怎麼算合宜
 * - [gender]：星座陰陽依什麼對照
 * - [mercury]：水星算不算（依東出西入決定它的晝夜與陰陽）
 */
data class HayzOptions(
  val hemisphere: Hemisphere,
  val gender: Gender,
  val mercury: Boolean,
) {
  enum class Hemisphere {
    /** ḥalb：晝星日盤在地平上、夜盤在地平下；夜星日盤在地平下、夜盤在地平上 —— 也就是「與盤同隊就在地平上，異隊就在地平下」 */
    HALB,
    /** 只收與盤同隊、且在地平上的星（舊版 core 的判法） */
    IN_SECT_ABOVE,
  }

  enum class Gender {
    /** 依星本身的陰陽：日木土火為陽、月金為陰（火星是夜星卻是陽性） */
    PLANET,
    /** 依晝夜歸屬：晝星要陽性星座、夜星要陰性星座 */
    SECT,
  }

  companion object {
    /** Abu Ma'shar《Great Introduction》：ḥalb 加上星座陰陽與星的陰陽相合；水星東出為晝、陽 */
    val ABU_MASHAR = HayzOptions(Hemisphere.HALB, Gender.PLANET, mercury = true)

    /** 舊版 core 的判法：與盤同隊、在地平上、晝星陽性星座／夜星陰性星座；不判水星 */
    val IN_SECT_ABOVE = HayzOptions(Hemisphere.IN_SECT_ABOVE, Gender.SECT, mercury = false)
  }
}

/**
 * 區分與得時。盤的日夜一律由呼叫端給（日夜的判定要看出生時刻精度，見 DayNightSourceResolver），這裡不判
 */
interface ISect {

  /** 區分；盤上缺日、月、水星的位置時為 null */
  fun getSect(h: IHoroscopeModel, sect: DayNight): Sect?

  /** [planet] 得時；不得時（或缺上升點）為 null */
  fun getHayz(h: IHoroscopeModel, sect: DayNight, planet: Planet): Hayz?

  /** 全盤得時的星，依古典行星順序 */
  fun getHayzResults(h: IHoroscopeModel, sect: DayNight): List<Hayz> =
    Planet.classicalList.mapNotNull { getHayz(h, sect, it) }
}

class SectImpl(
  val options: HayzOptions = HayzOptions.ABU_MASHAR,
) : ISect {

  override fun getSect(h: IHoroscopeModel, sect: DayNight): Sect? {
    val mercurySect = mercurySect(h) ?: return null
    val teamOf = { p: Planet -> if (p === Planet.MERCURY) mercurySect else p.sect!! }
    val (inSect, contrary) = Planet.classicalList.partition { teamOf(it) == sect }
    return when (sect) {
      DayNight.DAY   -> Sect(sect, Planet.SUN, inSect, contrary, Planet.JUPITER, Planet.MARS, mercurySect)
      DayNight.NIGHT -> Sect(sect, Planet.MOON, inSect, contrary, Planet.VENUS, Planet.SATURN, mercurySect)
    }
  }

  override fun getHayz(h: IHoroscopeModel, sect: DayNight, planet: Planet): Hayz? {
    if (planet !in Planet.classicalList) return null
    if (planet === Planet.MERCURY && !options.mercury) return null
    val planetSect = (if (planet === Planet.MERCURY) mercurySect(h) else planet.sect) ?: return null
    val deg = h.positionMap[planet]?.lngDeg ?: return null
    val asc = h.positionMap[Axis.RISING]?.lngDeg ?: return null
    // 從上升點順黃道走 0–180° 是地平線下（第 1–6 宮那一半），180–360° 是地平線上
    val above = deg.aheadOf(asc) > 180.0
    val inSect = planetSect == sect
    val hemisphereOk = when (options.hemisphere) {
      HayzOptions.Hemisphere.HALB          -> above == inSect
      HayzOptions.Hemisphere.IN_SECT_ABOVE -> inSect && above
    }
    if (!hemisphereOk) return null
    val wantMasculine = when (options.gender) {
      HayzOptions.Gender.PLANET -> when (planet) {
        Planet.MOON, Planet.VENUS -> false
        Planet.MERCURY            -> planetSect == DayNight.DAY
        else                      -> true
      }
      HayzOptions.Gender.SECT   -> planetSect == DayNight.DAY
    }
    val sign = deg.sign
    if (sign.booleanValue != wantMasculine) return null
    return Hayz(planet, sect, planetSect, above, sign, sign.booleanValue, wantMasculine)
  }

  /** 水星先於太陽升起（黃經在太陽之前、180° 內）＝ 東出 ＝ 晝星 */
  private fun mercurySect(h: IHoroscopeModel): DayNight? {
    val me = h.positionMap[Planet.MERCURY]?.lngDeg ?: return null
    val sun = h.positionMap[Planet.SUN]?.lngDeg ?: return null
    return if (me.isOriental(sun)) DayNight.DAY else DayNight.NIGHT
  }
}
