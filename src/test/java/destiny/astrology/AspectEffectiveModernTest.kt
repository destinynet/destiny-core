/**
 * @author smallufo
 * Created on 2007/11/25 at 上午 2:28:05
 */
package destiny.astrology

import destiny.astrology.Aspect.*
import destiny.astrology.Planet.*
import mu.KotlinLogging
import kotlin.test.*


class AspectEffectiveModernTest  {

  private val modern: AspectEffectiveModern = AspectEffectiveModern()

  private val logger = KotlinLogging.logger {  }

  /** 測試「不考量行星」，注入內定的 AspectOrbsDefaultImpl 實作  */
  @Test
  fun testIsEffective_AspectOrbsDefaultImpl() {
    modern.aspectOrbsImpl = AspectOrbsDefaultImpl()

    //合，容許度是 11度
    assertTrue(modern.isEffective(0.0, 0.0, CONJUNCTION))
    assertTrue(modern.isEffective(0.0, 11.0, CONJUNCTION))
    assertFalse(modern.isEffective(0.0, 12.0, CONJUNCTION))

    //刑 , 容許度是 7.5度
    assertTrue(modern.isEffective(90.0, 187.5, SQUARE))
    assertFalse(modern.isEffective(90.0, 187.6, SQUARE))
    assertTrue(modern.isEffective(90.0, 172.5, SQUARE))
    assertFalse(modern.isEffective(90.0, 172.4, SQUARE))

    //三合 , 容許度是 7.5度
    assertTrue(modern.isEffective(240.0, 7.5, TRINE))
    assertFalse(modern.isEffective(240.0, 7.6, TRINE))
    assertTrue(modern.isEffective(240.0, 352.5, TRINE))
    assertFalse(modern.isEffective(240.0, 352.4, TRINE))

    //沖 , 容許度是 11度
    assertTrue(modern.isEffective(90.0, 281.0, OPPOSITION))
    assertFalse(modern.isEffective(90.0, 282.0, OPPOSITION))
    assertTrue(modern.isEffective(90.0, 259.0, OPPOSITION))
    assertFalse(modern.isEffective(90.0, 258.0, OPPOSITION))
  }


  @Test
  fun testIsEffectiveAndScore_SUN_RISING() {
    /** 日月對沖，容許度 12度  , 記錄於 [AspectOrbsPlanetDefaultImpl] 的 map 中 */
    modern.getEffectiveErrorAndScore(SUN, 0.0, MOON, 192.0, OPPOSITION).also {
      assertNotNull(it)
      assertEquals(0.6 , it.second)
    }

    /** 與東昇點，並不在 [AspectOrbsPlanetDefaultImpl] 的 map 中 , 容許度改為 [AspectOrbsDefaultImpl] 為 11度 */
    modern.getEffectiveErrorAndScore(SUN, 0.0, Axis.RISING, 192.0, OPPOSITION).also {
      assertNull(it)
    }

    modern.getEffectiveErrorAndScore(SUN, 0.0, Axis.RISING, 191.0, OPPOSITION).also {
      assertNotNull(it)
      assertEquals(0.6 , it.second)
    }
  }

  /**
   * 測試「考量行星」的實作，注入內定的 [AspectOrbsPlanetDefaultImpl] 實作，該實作只對日月交角有更高的容許度
   */
  @Test
  fun testIsEffective_AspectOrbsPlanetDefaultImpl() {
    modern.aspectOrbsPlanetImpl = AspectOrbsPlanetDefaultImpl()

    // 合相 容許度 , 基本為 11 度 , 而此實作，日月合相的容許度為 12 度
    assertTrue(modern.isEffective(SUN, 0.0, MOON, 11.0, CONJUNCTION))
    assertTrue(modern.isEffective(SUN, 0.0, MOON, 12.0, CONJUNCTION))
    assertFalse(modern.isEffective(SUN, 0.0, MOON, 13.0, CONJUNCTION))
    // 其他星體的「相合」容許度，仍為 11度
    assertTrue(modern.isEffective(SUN, 0.0, VENUS, 11.0, CONJUNCTION))
    assertFalse(modern.isEffective(SUN, 0.0, VENUS, 12.0, CONJUNCTION))

    // 對沖 容許度 , 基本為 11 度 , 而此實作，日月對沖容許度為 12 度
    assertTrue(modern.isEffective(SUN, 0.0, MOON, 191.0, OPPOSITION))
    assertTrue(modern.isEffective(SUN, 0.0, MOON, 192.0, OPPOSITION))

    assertFalse(modern.isEffective(SUN, 0.0, MOON, 193.0, OPPOSITION))

    modern.getEffectiveErrorAndScore(SUN, 0.0, MOON, 192.0, OPPOSITION).also {
      assertNotNull(it)
      assertEquals(0.6 , it.second)
    }

    modern.getEffectiveErrorAndScore(SUN, 0.0, MOON, 180.0, OPPOSITION).also {
      assertNotNull(it)
      assertEquals(1.0 , it.second)
    }

    modern.getEffectiveErrorAndScore(SUN, 0.0, MOON, 193.0, OPPOSITION).also {
      assertNull(it)
    }



    // 其他星體的「對沖」容許度，仍為 11度
    assertTrue(modern.isEffective(MOON, 0.0, VENUS, 191.0, OPPOSITION))
    assertFalse(modern.isEffective(MOON, 0.0, VENUS, 192.0, OPPOSITION))

    // 三合 容許度 , 基本為 7.5 度 , 而此實作，日月三合的容許度為 8 度
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 112.5, TRINE))
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 112.0, TRINE))
    assertFalse(modern.isEffective(MOON, 0.0, SUN, 111.0, TRINE))

    // 相刑 容許度 , 基本為 7.5 度 , 而此實作，日月相刑的容許度為 8 度
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 97.5, SQUARE))
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 98.0, SQUARE))
    assertFalse(modern.isEffective(MOON, 0.0, SUN, 99.0, SQUARE))

    // 六分 容許度 , 基本為 4.5 度 , 而此實作，日月六分容許度為 5 度
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 304.5, SEXTILE))
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 305.0, SEXTILE))
    assertFalse(modern.isEffective(MOON, 0.0, SUN, 306.0, SEXTILE))

    // 半刑 容許度 , 基本為 2 度 , 而此實作，日月半刑容許度為 2.5 度
    assertTrue(modern.isEffective(SUN, 0.0, MOON, 47.0, SEMISQUARE))
    assertTrue(modern.isEffective(SUN, 0.0, MOON, 47.5, SEMISQUARE))
    assertFalse(modern.isEffective(SUN, 0.0, MOON, 48.0, SEMISQUARE))

    // 補八分相(135) 容許度 , 基本為 2 度 , 而此實作，日月補八分相的容許度為 2.5 度
    assertTrue(modern.isEffective(SUN, 90.0, MOON, 227.0, SESQUIQUADRATE))
    assertTrue(modern.isEffective(SUN, 90.0, MOON, 227.5, SESQUIQUADRATE))
    assertFalse(modern.isEffective(SUN, 90.0, MOON, 228.0, SESQUIQUADRATE))

    // 半六合(30) 容許度 , 基本為 1.5 度 , 而此實作，日月半六合容許度為 2 度
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 331.5, SEMISEXTILE))
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 332.0, SEMISEXTILE))
    assertFalse(modern.isEffective(MOON, 0.0, SUN, 333.0, SEMISEXTILE))

    // 補十二分相(150) 容許度 , 基本為 2 度 , 而此實作，日月補十二分相容許度為 2.5 度
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 152.0, QUINCUNX))
    assertTrue(modern.isEffective(MOON, 0.0, SUN, 152.5, QUINCUNX))
    assertFalse(modern.isEffective(MOON, 0.0, SUN, 153.0, QUINCUNX))
  }

  /** 多重角度比對，只要一個成立，就該傳回 true  */
  @Test
  fun testIsEffective_multipleAspects() {
    modern.aspectOrbsPlanetImpl = AspectOrbsPlanetDefaultImpl()

    // 合相 容許度 , 基本為 11 度
    assertTrue(modern.isEffective(SUN, 0.0, MOON, 11.0, CONJUNCTION, OPPOSITION))
    assertFalse(modern.isEffective(SUN, 0.0, MOON, 11.0, SQUARE, OPPOSITION))
    assertFalse(modern.isEffective(SUN, 0.0, MOON, 13.0, CONJUNCTION, SQUARE))

    // 其他星體的「對沖」容許度，仍為 11度
    assertTrue(modern.isEffective(MOON, 0.0, VENUS, 191.0, OPPOSITION))
    assertTrue(modern.isEffective(MOON, 0.0, VENUS, 191.0, CONJUNCTION, OPPOSITION))
    assertFalse(modern.isEffective(MOON, 0.0, VENUS, 192.0, CONJUNCTION, OPPOSITION))
  }
}
