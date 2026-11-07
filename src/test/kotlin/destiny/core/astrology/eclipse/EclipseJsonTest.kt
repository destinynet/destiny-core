/**
 * Created by smallufo on 2026-11-07.
 */
package destiny.core.astrology.eclipse

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.eclipseModule
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * [IEclipse] 的 JSON 格式。序列化結果可能長期保存，所以固定住：
 * 類別全名（多型的 `type`）、巢狀的欄位路徑、時間的兩個欄位（`julDay` 與給人看的 `gmt`）。
 * 改名、搬動子類別或調整欄位，就會在這裡失敗，要連同已保存的資料一起處理。
 */
class EclipseJsonTest {

  @Serializable
  private data class Holder(val eclipse: IEclipse)

  private val module = SerializersModule { eclipseModule() }
  private val json = Json { serializersModule = module }
  private val withDefaults = Json { serializersModule = module; encodeDefaults = true }

  /** 2000-01-01 12:00 UT 起算 [hour] 小時 */
  private fun t(hour: Int) = GmtJulDay(2451545.0 + hour / 24.0)

  private val solarPartial = AbstractSolarEclipse.SolarEclipsePartial(t(1), t(2), t(3))
  private val solarTotal = AbstractSolarEclipse.SolarEclipseTotal(solarPartial, t(4), t(5))
  private val solarTotalCentered = AbstractSolarEclipse.SolarEclipseTotalCentered(solarTotal, t(6), t(7))
  private val solarAnnular = AbstractSolarEclipse.SolarEclipseAnnular(solarTotal, t(6), t(7))
  private val solarAnnularCentered = AbstractSolarEclipse.SolarEclipseAnnularCentered(solarAnnular, t(8), t(9))
  private val solarHybrid = AbstractSolarEclipse.SolarEclipseHybrid(solarAnnularCentered)
  private val lunarPenumbra = AbstractLunarEclipse.LunarEclipsePenumbra(t(1), t(2), t(3))
  private val lunarPartial = AbstractLunarEclipse.LunarEclipsePartial(lunarPenumbra, t(4), t(5))
  private val lunarTotal = AbstractLunarEclipse.LunarEclipseTotal(lunarPartial, t(6), t(7))

  private val all: Map<String, IEclipse> = mapOf(
    "AbstractSolarEclipse.SolarEclipsePartial" to solarPartial,
    "AbstractSolarEclipse.SolarEclipseTotal" to solarTotal,
    "AbstractSolarEclipse.SolarEclipseTotalCentered" to solarTotalCentered,
    "AbstractSolarEclipse.SolarEclipseAnnular" to solarAnnular,
    "AbstractSolarEclipse.SolarEclipseAnnularCentered" to solarAnnularCentered,
    "AbstractSolarEclipse.SolarEclipseHybrid" to solarHybrid,
    "AbstractLunarEclipse.LunarEclipsePenumbra" to lunarPenumbra,
    "AbstractLunarEclipse.LunarEclipsePartial" to lunarPartial,
    "AbstractLunarEclipse.LunarEclipseTotal" to lunarTotal,
  )

  private fun encode(e: IEclipse, format: Json = json) = format.encodeToString(Holder.serializer(), Holder(e))

  private fun assertTime(text: String, path: String, hour: Int, gmt: String) {
    assertEquals(t(hour).value, JsonPath.read<Double>(text, "$path.julDay"), 1e-9, "$path in $text")
    assertEquals(gmt, JsonPath.read<String>(text, "$path.gmt"), "$path in $text")
  }

  @Test
  fun `每一種食 類別全名固定 讀回來相同 共用欄位也對`() {
    all.forEach { (name, eclipse) ->
      val text = encode(eclipse)
      assertEquals("destiny.core.astrology.eclipse.$name", JsonPath.read<String>(text, "$.eclipse.type"), text)
      val back = json.decodeFromString(Holder.serializer(), text).eclipse
      assertEquals(eclipse, back, text)
      assertEquals(eclipse::class, back::class, text)
      assertEquals(t(1), back.begin)
      assertEquals(t(2), back.max)
      assertEquals(t(3), back.end)
    }
  }

  @Test
  fun `日食的欄位路徑`() {
    val partial = encode(solarPartial)
    assertTime(partial, "$.eclipse.begin", 1, "2000-01-01T13:00")
    assertTime(partial, "$.eclipse.max", 2, "2000-01-01T14:00")
    assertTime(partial, "$.eclipse.end", 3, "2000-01-01T15:00")

    val total = encode(solarTotal)
    assertTime(total, "$.eclipse.partial.begin", 1, "2000-01-01T13:00")
    assertTime(total, "$.eclipse.totalBegin", 4, "2000-01-01T16:00")
    assertTime(total, "$.eclipse.totalEnd", 5, "2000-01-01T17:00")

    val centered = encode(solarTotalCentered)
    assertTime(centered, "$.eclipse.total.partial.max", 2, "2000-01-01T14:00")
    assertTime(centered, "$.eclipse.centerBegin", 6, "2000-01-01T18:00")
    assertTime(centered, "$.eclipse.centerEnd", 7, "2000-01-01T19:00")

    val annular = encode(solarAnnular)
    assertTime(annular, "$.eclipse.total.totalBegin", 4, "2000-01-01T16:00")
    assertTime(annular, "$.eclipse.annularBegin", 6, "2000-01-01T18:00")
    assertTime(annular, "$.eclipse.annularEnd", 7, "2000-01-01T19:00")

    val annularCentered = encode(solarAnnularCentered)
    assertTime(annularCentered, "$.eclipse.annular.total.partial.end", 3, "2000-01-01T15:00")
    assertTime(annularCentered, "$.eclipse.centerBegin", 8, "2000-01-01T20:00")

    val hybrid = encode(solarHybrid)
    assertTime(hybrid, "$.eclipse.annularCentered.annular.total.partial.begin", 1, "2000-01-01T13:00")
    assertTime(hybrid, "$.eclipse.annularCentered.centerEnd", 9, "2000-01-01T21:00")
  }

  @Test
  fun `月食的欄位路徑`() {
    val penumbra = encode(lunarPenumbra)
    assertTime(penumbra, "$.eclipse.begin", 1, "2000-01-01T13:00")
    assertTime(penumbra, "$.eclipse.max", 2, "2000-01-01T14:00")
    assertTime(penumbra, "$.eclipse.end", 3, "2000-01-01T15:00")

    val partial = encode(lunarPartial)
    assertTime(partial, "$.eclipse.penumbra.max", 2, "2000-01-01T14:00")
    assertTime(partial, "$.eclipse.partialBegin", 4, "2000-01-01T16:00")
    assertTime(partial, "$.eclipse.partialEnd", 5, "2000-01-01T17:00")

    val total = encode(lunarTotal)
    assertTime(total, "$.eclipse.partial.penumbra.end", 3, "2000-01-01T15:00")
    assertTime(total, "$.eclipse.partial.partialBegin", 4, "2000-01-01T16:00")
    assertTime(total, "$.eclipse.totalBegin", 6, "2000-01-01T18:00")
    assertTime(total, "$.eclipse.totalEnd", 7, "2000-01-01T19:00")
  }

  @Test
  fun `種類欄位只在 encodeDefaults 時寫出 讀回來都一樣`() {
    val plain = encode(lunarTotal)
    assertFailsWith<PathNotFoundException> { JsonPath.read<String>(plain, "$.eclipse.lunarType") }
    assertEquals(LunarType.TOTAL, (json.decodeFromString(Holder.serializer(), plain).eclipse as ILunarEclipse).lunarType)

    val lunar = encode(lunarTotal, withDefaults)
    assertEquals("TOTAL", JsonPath.read<String>(lunar, "$.eclipse.lunarType"))
    assertEquals(lunarTotal, withDefaults.decodeFromString(Holder.serializer(), lunar).eclipse)

    val solar = encode(solarAnnular, withDefaults)
    assertEquals("ANNULAR", JsonPath.read<String>(solar, "$.eclipse.solarType"))
    assertEquals(solarAnnular, withDefaults.decodeFromString(Holder.serializer(), solar).eclipse)
  }

  @Test
  fun `已保存的 JSON 讀得回來`() {
    val saved = """
      {"eclipse":{"type":"destiny.core.astrology.eclipse.AbstractLunarEclipse.LunarEclipsePartial",
       "penumbra":{"begin":{"julDay":2451545.25,"gmt":"2000-01-01T18:00"},"max":{"julDay":2451545.5,"gmt":"2000-01-02T00:00"},"end":{"julDay":2451545.75,"gmt":"2000-01-02T06:00"},"lunarType":"PENUMBRA"},
       "partialBegin":{"julDay":2451545.375,"gmt":"2000-01-01T21:00"},"partialEnd":{"julDay":2451545.625,"gmt":"2000-01-02T03:00"},"lunarType":"PARTIAL"}}
    """.trimIndent()
    val expected = AbstractLunarEclipse.LunarEclipsePartial(
      AbstractLunarEclipse.LunarEclipsePenumbra(GmtJulDay(2451545.25), GmtJulDay(2451545.5), GmtJulDay(2451545.75)),
      GmtJulDay(2451545.375), GmtJulDay(2451545.625),
    )
    assertEquals(expected, json.decodeFromString(Holder.serializer(), saved).eclipse)
  }

  @Test
  fun `沒註冊 eclipseModule 讀寫都丟例外`() {
    assertFailsWith<SerializationException> { Json.encodeToString(Holder.serializer(), Holder(lunarTotal)) }
    assertFailsWith<SerializationException> { Json.decodeFromString(Holder.serializer(), encode(lunarTotal)) }
  }
}
