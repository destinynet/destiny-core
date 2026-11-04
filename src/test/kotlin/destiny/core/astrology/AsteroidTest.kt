/**
 * Created by smallufo on 2021-02-21.
 */
package destiny.core.astrology

import destiny.core.AbstractPointTest
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class AsteroidTest : AbstractPointTest(Asteroid::class) {

  @Test
  fun testSerialize() {
    Asteroid.values.forEach { p ->
      val rawJson = Json.encodeToString(p)
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(rawJson))
    }
  }

  @Test
  fun testName_cn() {
    val locale = Locale.ENGLISH
    assertEquals("Ceres", Asteroid.CERES.getTitle(locale))
    assertEquals("Pallas", Asteroid.PALLAS.getTitle(locale))
    assertEquals("Juno", Asteroid.JUNO.getTitle(locale))
    assertEquals("Vesta", Asteroid.VESTA.getTitle(locale))
    assertEquals("Chiron", Asteroid.CHIRON.getTitle(locale))
    assertEquals("Pholus", Asteroid.PHOLUS.getTitle(locale))
  }

  @Test
  fun testName_zh_TW() {
    val locale = Locale.TAIWAN
    assertEquals("穀神星", Asteroid.CERES.getTitle(locale))
    assertEquals("智神星", Asteroid.PALLAS.getTitle(locale))
    assertEquals("婚神星", Asteroid.JUNO.getTitle(locale))
    assertEquals("灶神星", Asteroid.VESTA.getTitle(locale))
    assertEquals("凱龍星", Asteroid.CHIRON.getTitle(locale))
    assertEquals("人龍星", Asteroid.PHOLUS.getTitle(locale))
  }

  @Test
  fun testName_zh_CN() {
    val locale = Locale.SIMPLIFIED_CHINESE
    assertEquals("谷神星", Asteroid.CERES.getTitle(locale))
    assertEquals("智神星", Asteroid.PALLAS.getTitle(locale))
    assertEquals("婚神星", Asteroid.JUNO.getTitle(locale))
    assertEquals("灶神星", Asteroid.VESTA.getTitle(locale))
    assertEquals("凯龙星", Asteroid.CHIRON.getTitle(locale))
    assertEquals("人龙星", Asteroid.PHOLUS.getTitle(locale))
  }
}
