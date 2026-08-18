/**
 * Created by smallufo on 2017-03-22.
 */
package destiny.core.calendar

import destiny.core.calendar.GoogleMapsUrlBuilder.Companion.fixed6
import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleMapsUrlBuilderTest {

  private val logger = KotlinLogging.logger { }

  private val builder = GoogleMapsUrlBuilder()

  /**
   * 兩個 overload（吃 lat/lng 與吃 [ILocation]）必須產生相同的 URL；
   * 順帶釘住西經（負經度）的輸出格式。原本三行都只有 logger.info。
   */
  @Test
  fun getUrl() {
    listOf(Locale.TAIWAN, Locale.of("zh", "HK"), Locale.US).forEach { locale ->
      val location = locationOf(locale)
      assertEquals(
        builder.getUrl(location.lat.value, location.lng.value),
        builder.getUrl(location),
        "$locale 的兩個 overload 應一致"
      )
    }

    // 紐約：經度為負
    assertEquals(
      "https://www.google.com/maps?&z=10&q=40.758899+-73.985131&ll=40.758899+-73.985131&z=14",
      builder.getUrl(locationOf(Locale.US))
    )
  }

  /** 明確釘住 URL 格式 —— 原本只有 logger.info，格式跑掉不會被測出來 */
  @Test
  fun `getUrl 輸出格式`() {
    assertEquals(
      "https://www.google.com/maps?&z=10&q=25.039059+121.517675&ll=25.039059+121.517675&z=14",
      builder.getUrl(25.039059, 121.517675)
    )
    // 南半球 / 補零
    assertEquals(
      "https://www.google.com/maps?&z=10&q=-33.868800+151.209300&ll=-33.868800+151.209300&z=14",
      builder.getUrl(-33.8688, 151.2093)
    )
  }

  /**
   * [fixed6] 必須與 `String.format(Locale.ROOT, "%f", x)` 完全等價。
   * 這是把 String.format(JVM-only) 換掉時的行為保證。
   */
  @Test
  fun `fixed6 等價於 %f`() {
    val samples = doubleArrayOf(
      0.0, -0.0, 1.0, -1.0,
      25.039059, 121.517675, -33.8688, 151.2093,
      180.0, -180.0, 90.0, -90.0,
      1e-7, -1e-7,                 // 小於半個最末位 → 進位成 0
      0.0000005, -0.0000005,       // 進位臨界
      0.9999995, 123.4567895, 59.9999999
    )
    for (d in samples) {
      assertEquals(String.format(Locale.ROOT, "%f", d), d.fixed6(), "fixed6($d)")
    }

    // 經緯度值域隨機掃描（固定 seed，可重現）
    val rnd = Random(42)
    repeat(200_000) {
      val d = rnd.nextDouble() * 360.0 - 180.0
      assertEquals(String.format(Locale.ROOT, "%f", d), d.fixed6(), "fixed6($d)")
    }
  }
}
