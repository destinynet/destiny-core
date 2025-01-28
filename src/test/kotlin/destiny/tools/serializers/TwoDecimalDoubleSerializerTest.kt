/**
 * Created by smallufo on 2025-01-26.
 */
package destiny.tools.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test

class TwoDecimalDoubleSerializerTest {

  @Serializable
  data class MyClass(
    val value: Double, val nested: NestedClass
  )

  @Serializable
  data class NestedClass(
    val innerValue: Double
  )


  private val json = Json {
    prettyPrint = true
    serializersModule = SerializersModule {
      contextual(Double::class, TwoDecimalDoubleSerializer)
    }
  }

  @Test
  fun testSerialize() {

    val someObject = MyClass(123.456, NestedClass(789.012))

    val jsonString = json.encodeToString(someObject)
    println(jsonString)
  }
}
