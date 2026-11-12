/**
 * Created by smallufo on 2026-10-25.
 */
package destiny.core

import destiny.core.Gender
import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Axis
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.BirthDataGrain
import destiny.core.astrology.Lot
import destiny.core.astrology.HoroscopeAspectDto
import destiny.core.astrology.PointKind
import destiny.core.astrology.HoroscopeChartDto
import destiny.core.astrology.HoroscopeDispositorDto
import destiny.core.astrology.HoroscopeHouseDto
import destiny.core.astrology.HoroscopeMetaDto
import destiny.core.astrology.HoroscopePointDto
import destiny.core.astrology.Planet
import destiny.core.astrology.gatedBy
import destiny.core.chinese.eightwords.EwChartDto
import destiny.core.chinese.eightwords.EwMetaDto
import destiny.core.chinese.eightwords.EwPillarDto
import destiny.core.chinese.eightwords.gatedBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** 依出生時刻精度剝 DTO：[EwChartDto.gatedBy]、[HoroscopeChartDto.gatedBy] */
class GrainGatedChartDtoTest {

  private fun pillar(scale: Scale) = EwPillarDto(scale.name, "甲", "子", null, null, emptyList(), null)

  private val ew = EwChartDto(
    meta = EwMetaDto(gender = "M", mingStemBranch = "甲子", mingSign = "ARIES", dayEmpties = emptyList(), score = null,
                     pillarsOnly = false, risingSign = "ARIES"),
    pillars = listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR).map(::pillar),
    fortunes = emptyList(),
    solarTerms = null,
  )

  @Test
  fun `八字：有時辰原樣回傳，沒有時辰拿掉時柱與命宮`() {
    assertSame(ew, ew.gatedBy(BirthDataGrain.MINUTE))
    assertSame(ew, ew.gatedBy(BirthDataGrain.HOUR2))

    ew.gatedBy(BirthDataGrain.DAY).also {
      assertEquals(listOf("YEAR", "MONTH", "DAY"), it.pillars.map { p -> p.scale })
      assertNull(it.meta.mingStemBranch)
      assertNull(it.meta.mingSign)
      assertNull(it.meta.risingSign)
    }
  }

  private fun point(key: AstroPoint, kind: PointKind) = HoroscopePointDto(
    point = key, label = key.nameKey, glyph = null, zDeg = 10.0, sign = ZodiacSign.ARIES, signDegree = 10.0, house = 1, retrograde = false, kind = kind)

  private fun aspect(from: AstroPoint, to: AstroPoint) = HoroscopeAspectDto(from, to, Aspect.CONJUNCTION, 0.0, 1.0, true, null)

  private val sun = Planet.SUN
  private val moon = Planet.MOON
  private val rising = Axis.RISING

  private val horo = HoroscopeChartDto(
    meta = HoroscopeMetaDto(gender = Gender.F, ascDeg = 123.0, mcDeg = 33.0),
    points = listOf(point(sun, PointKind.PLANET), point(moon, PointKind.PLANET), point(rising, PointKind.AXIS)),
    houses = listOf(HoroscopeHouseDto(1, 123.0, ZodiacSign.LEO, Planet.SUN, "太陽")),
    aspects = listOf(aspect(sun, moon), aspect(sun, rising)),
  )

  @Test
  fun `占星：分鐘級原樣`() {
    assertEquals(horo, horo.gatedBy(BirthDataGrain.MINUTE))
  }

  @Test
  fun `占星：只知時辰 —— 拿掉軸點、宮位與各點的宮位，月亮保留`() {
    horo.gatedBy(BirthDataGrain.HOUR2).also {
      assertEquals(listOf(sun, moon), it.points.map { p -> p.point })
      assertTrue(it.points.all { p -> p.house == null })
      assertTrue(it.houses.isEmpty())
      assertEquals(0.0, it.meta.ascDeg)
      assertEquals(0.0, it.meta.mcDeg)
      assertEquals(listOf(sun to moon), it.aspects.map { a -> a.from to a.to }, "連到上升的相位一起拿掉")
    }
  }

  @Test
  fun `占星：只知日期 —— 再拿掉月亮與它的相位`() {
    horo.gatedBy(BirthDataGrain.DAY).also {
      assertEquals(listOf(sun), it.points.map { p -> p.point })
      assertTrue(it.aspects.isEmpty())
    }
  }

  private val withDispositors = horo.copy(
    dispositors = HoroscopeDispositorDto(circles = emptyList(), paths = listOf(listOf(moon, sun)), terminals = listOf(sun), isolated = emptyList())
  )

  @Test
  fun `占星：定位星關係 —— 只知時辰照留，只知日期（月亮沒了）整個拿掉`() {
    assertEquals(withDispositors.dispositors, withDispositors.gatedBy(BirthDataGrain.HOUR2).dispositors)
    assertNull(withDispositors.gatedBy(BirthDataGrain.DAY).dispositors)
  }

  @Test
  fun `占星：Lot 由上升起算 —— 只知時辰就跟軸點一起拿掉`() {
    val withLot = horo.copy(
      points = horo.points + point(Lot.Fortune, PointKind.LOT),
      aspects = horo.aspects + aspect(sun, Lot.Fortune),
    )
    assertEquals(withLot, withLot.gatedBy(BirthDataGrain.MINUTE))
    withLot.gatedBy(BirthDataGrain.HOUR2).also {
      assertTrue(it.points.none { p -> p.kind == PointKind.LOT })
      assertTrue(it.aspects.none { a -> a.to == Lot.Fortune })
    }
  }
}
