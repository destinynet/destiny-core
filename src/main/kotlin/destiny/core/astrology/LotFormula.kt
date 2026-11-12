/**
 * Created by smallufo on 2026-11-13.
 */
package destiny.core.astrology

import destiny.core.DayNight

/**
 * Lot（[Lot]）的公式：白天是「上升 ＋ [LotOperands.plus] − [LotOperands.minus]」，夜盤兩者對調（[LotOptions.reverseByNight]）。
 *
 * 七個 Lot 都是這個形狀，差別只在兩個點，所以公式只登記這兩個點（[operands]）；位置函式與古典事實共用這一份。
 * 運算元可以是另一個 Lot（愛情點用精神點、必要點用福點），[lngOf] 會遞迴算出來。
 */
data class LotOperands(val plus: AstroPoint, val minus: AstroPoint)

/**
 * Lot 的判準。
 *
 * - [reverseByNight]：夜盤是否對調公式。Valens、Paulus 與中世紀主流對調；Ptolemy 不對調（福點日夜都是 上升＋月−日）
 *
 * [DEFAULT]：對調（使用者 2026-11-13 定）
 */
data class LotOptions(val reverseByNight: Boolean = true) {
  companion object {
    /** Valens、Paulus：夜盤對調 */
    val HELLENISTIC = LotOptions(reverseByNight = true)

    /** Ptolemy：日夜同一條公式 */
    val PTOLEMY = LotOptions(reverseByNight = false)

    val DEFAULT = HELLENISTIC
  }
}

/** 白天的兩個運算元：上升 ＋ [LotOperands.plus] − [LotOperands.minus] */
val Lot.operands: LotOperands
  get() = when (this) {
    Lot.Fortune   -> LotOperands(Planet.MOON, Planet.SUN)
    Lot.Spirit    -> LotOperands(Planet.SUN, Planet.MOON)
    Lot.Eros      -> LotOperands(Planet.VENUS, Lot.Spirit)
    Lot.Victory   -> LotOperands(Planet.JUPITER, Lot.Spirit)
    Lot.Necessity -> LotOperands(Lot.Fortune, Planet.MERCURY)
    Lot.Courage   -> LotOperands(Lot.Fortune, Planet.MARS)
    Lot.Nemesis   -> LotOperands(Lot.Fortune, Planet.SATURN)
  }

/** 這張盤（日夜為 [sect]）實際要加、要減的兩個點 */
fun Lot.operandsOf(sect: DayNight, options: LotOptions = LotOptions.DEFAULT): LotOperands =
  operands.let { if (sect == DayNight.NIGHT && options.reverseByNight) LotOperands(it.minus, it.plus) else it }

/**
 * [lngs] 取得各點黃經（要有 [Axis.RISING]）；運算元是 Lot 時遞迴算出。缺任何一個點回 null。
 *
 * 上升要用**實際的上升點**，不是第一宮頭 —— 整宮制的第一宮頭是上升星座的 0°。
 */
fun Lot.lngOf(lngs: (AstroPoint) -> ZodiacDegree?, sect: DayNight, options: LotOptions = LotOptions.DEFAULT): ZodiacDegree? {
  fun lng(p: AstroPoint): ZodiacDegree? = if (p is Lot) p.lngOf(lngs, sect, options) else lngs(p)
  val asc = lngs(Axis.RISING) ?: return null
  val (plus, minus) = operandsOf(sect, options)
  return asc + (lng(plus) ?: return null) - (lng(minus) ?: return null)
}

/** 同上，各點黃經以 map 給 */
fun Lot.lngOf(lngs: Map<out AstroPoint, ZodiacDegree>, sect: DayNight, options: LotOptions = LotOptions.DEFAULT): ZodiacDegree? =
  lngOf({ lngs[it] }, sect, options)
