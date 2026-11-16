/**
 * Created by smallufo on 2026-11-16.
 */
package destiny.core.astrology

import destiny.core.Scale
import destiny.core.astrology.Constants.TROPICAL_YEAR_DAYS
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.GmtJulDay
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

/** 小限法以星座計，與宮制無關 */
class HoroscopeProfectionTest {

  private val birth = GmtJulDay(2451545.0)

  private fun deg(sign: ZodiacSign, d: Double) = (sign.ordinal * 30 + d).toZodiacDegree()

  /** 上升在 [cusps] 第 1 宮宮頭、太陽在第 10 宮的盤 */
  private fun model(cusps: List<ZodiacDegree>): IHoroscopeModel = mockk<IHoroscopeModel> {
    every { gmtJulDay } returns birth
    cusps.forEachIndexed { i, d -> every { getCuspDegree(i + 1) } returns d }
    every { getHouse(Axis.RISING) } returns 1
    every { getHouse(Planet.SUN) } returns 10
    every { getZodiacSign(Axis.RISING) } returns cusps[0].sign
  }

  /** 整宮制：上升巨蟹，每宮宮頭是一個星座的 0° */
  private val wholeSign = model((0 until 12).map { deg(ZodiacSign.CANCER.next(it), 0.0) })

  /**
   * 象限宮制、有截奪星座：上升巨蟹 20°，第 2 宮宮頭處女 2°（獅子被截奪）、第 8 宮宮頭雙魚 2°（水瓶被截奪），
   * 第 6、7 宮宮頭都在摩羯，第 12、1 宮宮頭都在巨蟹
   */
  private val intercepted = model(
    listOf(
      deg(ZodiacSign.CANCER, 20.0), deg(ZodiacSign.VIRGO, 2.0), deg(ZodiacSign.LIBRA, 1.0), deg(ZodiacSign.SCORPIO, 1.0),
      deg(ZodiacSign.SAGITTARIUS, 3.0), deg(ZodiacSign.CAPRICORN, 2.0), deg(ZodiacSign.CAPRICORN, 20.0), deg(ZodiacSign.PISCES, 2.0),
      deg(ZodiacSign.ARIES, 1.0), deg(ZodiacSign.TAURUS, 1.0), deg(ZodiacSign.GEMINI, 3.0), deg(ZodiacSign.CANCER, 2.0),
    )
  )

  private fun IHoroscopeModel.years() = (0 until 12).map { age -> getProfection(birth + (age + 0.5) * TROPICAL_YEAR_DAYS, Scale.YEAR).getValue(Scale.YEAR) }

  @Test
  fun `整宮制 小限星座就是第 N 宮宮頭的星座`() {
    val years = wholeSign.years()
    assertEquals((1..12).toList(), years.map { it.house })
    assertEquals((1..12).map { wholeSign.getCuspDegree(it).sign }, years.map { it.ascSign })
    // 範圍查詢、月、日同一套
    wholeSign.getRangeProfections(birth, birth + 3 * TROPICAL_YEAR_DAYS, Scale.DAY).forEach { assertEquals(wholeSign.getCuspDegree(it.house).sign, it.ascSign) }
    wholeSign.getProfection(birth + 500.0, Scale.DAY).values.forEach { assertEquals(wholeSign.getCuspDegree(it.house).sign, it.ascSign) }
  }

  @Test
  fun `有截奪星座的象限宮制 十二年走完十二個不同的星座`() {
    val years = intercepted.years()
    assertEquals((0 until 12).map { ZodiacSign.CANCER.next(it) }, years.map { it.ascSign })
    assertEquals(12, years.map { it.ascSign }.toSet().size)
    years.forEach { assertEquals(it.house, it.ascSign.placeFrom(ZodiacSign.CANCER)) }
    // 宮頭的星座會跳過獅子、水瓶，重複巨蟹、摩羯：小限不能照它
    assertEquals(10, (1..12).map { intercepted.getCuspDegree(it).sign }.toSet().size)
    assertEquals(ZodiacSign.LEO, years[1].ascSign)
    assertEquals(Planet.SUN, years[1].lord)
  }

  @Test
  fun `有截奪星座時 月與日的小限也逐星座推進`() {
    val months = intercepted.getRangeProfections(birth + 0.01, birth + TROPICAL_YEAR_DAYS - 0.01, Scale.MONTH)
    assertEquals((0 until 12).map { ZodiacSign.CANCER.next(it) }, months.map { it.ascSign })
    intercepted.getRangeProfections(birth, birth + 2 * TROPICAL_YEAR_DAYS, Scale.DAY).forEach { assertEquals(it.house, it.ascSign.placeFrom(ZodiacSign.CANCER)) }
    intercepted.getProfection(birth + 500.0, Scale.DAY).values.forEach { assertEquals(it.house, it.ascSign.placeFrom(ZodiacSign.CANCER)) }
    assertEquals(12, intercepted.getRangeProfections(birth, birth + 12 * TROPICAL_YEAR_DAYS - 1, Scale.YEAR).map { it.ascSign }.toSet().size)
  }
}
