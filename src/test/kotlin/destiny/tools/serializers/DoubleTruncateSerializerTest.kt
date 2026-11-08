/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleTruncateSerializerTest {

  private fun KSerializer<Double>.encode(value: Double): Double = Json.encodeToString(this, value).toDouble()

  @Test
  fun `無條件捨去 不是四捨五入`() {
    assertEquals(16.57, DoubleTwoDecimalSerializer.encode(16.579))
    assertEquals(16.57, DoubleTwoDecimalSerializer.encode(16.5799999))
    assertEquals(0.0, DoubleTwoDecimalSerializer.encode(0.009))
    assertEquals(1.2345, DoubleFourDecimalSerializer.encode(1.23459))
    assertEquals(1.234567, DoubleSixDecimalSerializer.encode(1.2345678))
  }

  @Test
  fun `浮點誤差不會多捨一位`() {
    assertEquals(16.58, DoubleTwoDecimalSerializer.encode(16.58))
    assertEquals(0.29, DoubleTwoDecimalSerializer.encode(0.29))
    assertEquals(1.15, DoubleTwoDecimalSerializer.encode(1.15))
  }

  @Test
  fun `負數一樣往下捨 已經是兩位的不變`() {
    assertEquals(-1.24, DoubleTwoDecimalSerializer.encode(-1.234))
    assertEquals(-16.58, DoubleTwoDecimalSerializer.encode(-16.58))
  }

  private fun assertIdempotent(serializer: KSerializer<Double>, values: Sequence<Double>) = values.forEach { v ->
    val once = serializer.encode(v)
    assertEquals(once, serializer.encode(once), "$v 編碼兩次結果不同")
  }

  @Test
  fun `編碼兩次結果相同`() {
    assertIdempotent(DoubleTwoDecimalSerializer, (-36000..36000).asSequence().map { it / 100.0 })
    assertIdempotent(DoubleTwoDecimalSerializer, (0..36000).asSequence().map { it * 0.0137 })
    assertIdempotent(DoubleFourDecimalSerializer, (0..200000).asSequence().map { it / 10000.0 })
    assertIdempotent(DoubleSixDecimalSerializer, (0..200000).asSequence().map { 359.0 + it * 0.000001 })
  }

  @Test
  fun `已經是剛好位數的值 編碼後完全不變`() {
    (0..36000).map { it / 100.0 }.forEach { assertEquals(it, DoubleTwoDecimalSerializer.encode(it)) }
  }
}
