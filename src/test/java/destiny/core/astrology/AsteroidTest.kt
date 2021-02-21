/**
 * Created by smallufo on 2021-02-21.
 */
package destiny.core.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.*

internal class AsteroidTest {

  @Test
  fun testName_cn() {
    val locale = Locale.ENGLISH
    assertEquals("Ceres" , Asteroid.CERES.toString(locale))
    assertEquals("Pallas" , Asteroid.PALLAS.toString(locale))
    assertEquals("Juno" , Asteroid.JUNO.toString(locale))
    assertEquals("Vesta" , Asteroid.VESTA.toString(locale))
    assertEquals("Chiron" , Asteroid.CHIRON.toString(locale))
    assertEquals("Pholus" , Asteroid.PHOLUS.toString(locale))
  }

  @Test
  fun testName_zh_TW() {
    val locale = Locale.TAIWAN
    assertEquals("穀神星" , Asteroid.CERES.toString(locale))
    assertEquals("智神星" , Asteroid.PALLAS.toString(locale))
    assertEquals("婚神星" , Asteroid.JUNO.toString(locale))
    assertEquals("灶神星" , Asteroid.VESTA.toString(locale))
    assertEquals("凱龍星" , Asteroid.CHIRON.toString(locale))
    assertEquals("人龍星" , Asteroid.PHOLUS.toString(locale))
  }

  @Test
  fun testName_zh_CN() {
    val locale = Locale.SIMPLIFIED_CHINESE
    assertEquals("谷神星" , Asteroid.CERES.toString(locale))
    assertEquals("智神星" , Asteroid.PALLAS.toString(locale))
    assertEquals("婚神星" , Asteroid.JUNO.toString(locale))
    assertEquals("灶神星" , Asteroid.VESTA.toString(locale))
    assertEquals("凯龙星" , Asteroid.CHIRON.toString(locale))
    assertEquals("人龙星" , Asteroid.PHOLUS.toString(locale))
  }
}
