/**
 * Created by smallufo on 2026-08-22.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.Gender
import destiny.core.astrology.DiceModel
import destiny.core.chinese.ziwei.Plate
import destiny.core.iching.divine.PairHexQuestion
import destiny.core.oracles.OracleQuestion
import destiny.core.tarot.TarotQuestion
import destiny.tools.KotlinLogging
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 守門測試：凡是會被 kotlinx 序列化的 [Gender] 欄位 , 都必須走 [GenderSerializer] , 而不是
 * kotlinx 內建的 enum serializer。
 *
 * 內建的 enum serializer 以「常數名稱」比對 , 於是 2025-12-28 的 commit `2b6a2b3b`
 *「Gender 男/女 -> M/F」讓所有更早產生的 `"gender":"男"` payload 全部解不開。
 * 詳見 [GenderSerializer] 的說明。
 *
 * 判準：欄位的 descriptor serialName 應為 `Gender`([GenderSerializer] 的 PrimitiveSerialDescriptor) ,
 * 而非 `destiny.core.Gender`(內建 enum serializer)。
 */
class GenderFieldWiringTest {

  private val logger = KotlinLogging.logger { }

  private fun assertGenderFieldWired(serializer: KSerializer<*>) {
    val descriptor = serializer.descriptor
    val index = (0 until descriptor.elementsCount).firstOrNull { descriptor.getElementName(it) == "gender" }
    assertNotNull(index, "${descriptor.serialName} 找不到 gender 欄位")

    val actual = descriptor.getElementDescriptor(index).serialName.removeSuffix("?")
    assertEquals(
      "Gender", actual,
      "${descriptor.serialName}.gender 沒有標 @Serializable(with = GenderSerializer::class) —— " +
        "2025-12-28 之前產生的 \"gender\":\"男\" 會解不開"
    )
  }

  @Test
  fun `DiceModel gender 必須走 GenderSerializer`() = assertGenderFieldWired(DiceModel.serializer())

  @Test
  fun `Plate gender 必須走 GenderSerializer`() = assertGenderFieldWired(Plate.serializer())

  @Test
  fun `PairHexQuestion gender 必須走 GenderSerializer`() = assertGenderFieldWired(PairHexQuestion.serializer())

  @Test
  fun `TarotQuestion gender 必須走 GenderSerializer`() = assertGenderFieldWired(TarotQuestion.serializer())

  @Test
  fun `OracleQuestion gender 必須走 GenderSerializer`() = assertGenderFieldWired(OracleQuestion.serializer())

  /** end-to-end : 舊格式的 男/女 必須解得開 , 而且重新編碼出來是 M/F */
  @Test
  fun `DiceModel 吃得下舊格式的 gender`() {
    mapOf(
      "男" to Gender.M,
      "女" to Gender.F,
      // 新格式也必須維持原樣
      "M" to Gender.M,
      "F" to Gender.F,
    ).forEach { (raw, expected) ->
      val legacyJson = """{"star":"SUN","sign":"ARIES","house":1,"gender":"$raw","question":"測試"}"""

      val model = Json.decodeFromString(DiceModel.serializer(), legacyJson)
      assertEquals(expected, model.gender, "gender = \"$raw\" 必須解成 $expected")

      Json.encodeToString(DiceModel.serializer(), model).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(expected.name, docCtx.read("$.gender"), "重新編碼必須是 M/F : $rawJson")
      }
    }
  }
}
