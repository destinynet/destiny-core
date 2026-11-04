package destiny.tools.serializers

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.ZodiacSign
import destiny.core.chinese.ziwei.StarMinor
import destiny.core.chinese.ziwei.ZStar
import destiny.tools.Lang
import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.DomainSerializer
import destiny.tools.serializers.astrology.AstroPointSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * 序列化寫的是**識別字**，不是 toString。
 *
 * `Point`／`ZodiacSign` 的 toString 回傳預設語系的顯示名稱（「太陽」「獅子」），
 * 而顯示名稱不是一對一的（紫微的陽空、陰空都叫「旬空」）。這裡守住：
 * 每個 serializer 都寫 `nameKey`／`name`，讀得回同一個物件，而且不會因為 title 相同而撞在一起。
 */
class IdentifierNotTitleTest {

  @Test
  fun `AstroPoint 寫 nameKey，讀得回來`() {
    AstroPoint.values.forEach { p ->
      val json = Json.encodeToString(AstroPointSerializer, p)
      assertEquals("\"${p.nameKey}\"", json, "$p")
      assertEquals(p, Json.decodeFromString(AstroPointSerializer, json))
    }
  }

  @Test
  fun `AstroPoint 當 map key 也寫 nameKey`() {
    val map = AstroPoint.values.associateWith { 1 }
    val serializer = MapSerializer(AstroPointSerializer, Int.serializer())
    val json = Json.encodeToString(serializer, map)
    AstroPoint.values.forEach { p ->
      if (p.getTitle(Lang.ZH_TW) != p.nameKey) {
        assertEquals(false, json.contains("\"${p.getTitle(Lang.ZH_TW)}\""), "$json 出現顯示名稱 ${p.getTitle(Lang.ZH_TW)}")
      }
    }
    assertEquals(map, Json.decodeFromString(serializer, json))
  }

  @Test
  fun `ZStar 讀得回來，同名星不會合併`() {
    ZStar.values.forEach { s ->
      val json = Json.encodeToString(ZStarSerializer, s)
      assertEquals(s, Json.decodeFromString(ZStarSerializer, json), json)
    }

    // 顯示名稱相同……
    assertEquals(StarMinor.陽空.getTitle(Lang.ZH_TW), StarMinor.陰空.getTitle(Lang.ZH_TW))
    // ……但識別字與序列化結果不同
    assertNotEquals(StarMinor.陽空.nameKey, StarMinor.陰空.nameKey)
    assertNotEquals(Json.encodeToString(ZStarSerializer, StarMinor.陽空), Json.encodeToString(ZStarSerializer, StarMinor.陰空))
  }

  @Test
  fun `ZodiacSign 寫列舉名，不是 toString`() {
    ZodiacSign.entries.forEach { sign ->
      val json = Json.encodeToString(ZodiacSign.serializer(), sign)
      assertEquals("\"${sign.name}\"", json)
      assertNotEquals("\"$sign\"", json, "toString 是顯示名稱「$sign」，不該進 JSON")
      assertEquals(sign, Json.decodeFromString(ZodiacSign.serializer(), json))
    }
  }

  @Test
  fun `Domain 寫 code，讀得回來`() {
    fun leaves(k: KClass<out Domain>): List<Domain> =
      k.objectInstance?.let { listOf(it) } ?: k.sealedSubclasses.flatMap { leaves(it) }

    val all = leaves(Domain::class)
    assert(all.size > 10) { "只找到 ${all.size} 個 Domain" }
    all.forEach { d ->
      val json = Json.encodeToString(DomainSerializer, d)
      assertEquals("\"${d::class.simpleName}\"", json)
      assertEquals(d, Json.decodeFromString(DomainSerializer, json))
    }
  }
}
