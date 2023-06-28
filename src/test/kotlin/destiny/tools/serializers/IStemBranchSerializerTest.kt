/**
 * Created by smallufo on 2022-07-03.
 */
package destiny.tools.serializers

import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUnconstrained
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class IStemBranchSerializerTest {

  @Test
  fun testSerializeStemBranch() {
    val sb = StemBranch.甲子
    val encoded = Json.encodeToString(sb)
    assertEquals("\"甲子\"", encoded)
    assertSame(sb, Json.decodeFromString(encoded))
  }

  @Test
  fun testSerializeStemBranchUnconstrained() {
    val sb = StemBranchUnconstrained.甲丑
    val encoded = Json.encodeToString(sb)
    assertEquals("\"甲丑\"", encoded)
    assertSame(sb, Json.decodeFromString(encoded))
  }
}
