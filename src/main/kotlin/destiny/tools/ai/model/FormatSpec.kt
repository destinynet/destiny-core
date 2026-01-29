/**
 * Created by smallufo on 2025-04-19.
 */
package destiny.tools.ai.model

import destiny.tools.ai.JsonSchemaSpec
import destiny.tools.ai.ListContainer
import destiny.tools.ai.ListContainerSerializer
import destiny.tools.ai.toJsonSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.typeOf


interface FormatSpec<T : Any> {
  val serializer: KSerializer<T>
  val jsonSchema: JsonSchemaSpec
  val kClass: KClass<T>

  companion object {
    /**
     * title pattern '^[a-zA-Z0-9_-]+$'
     */
    inline fun <reified T : Any> of(
      title: String,
      description: String
    ): FormatSpec<T> {
      require(T::class != Unit::class) { "Unit type is not allowed" }
      require(title.matches(Regex("^[a-zA-Z0-9_-]+$"))) { $$"title must match pattern '^[a-zA-Z0-9_-]+$', but was: '$$title'" }

      val kType = typeOf<T>()

      // 關鍵修改：偵測 T 是否為 ListContainer
      val ser: KSerializer<T> = if (kType.classifier == ListContainer::class) {
        // 是 ListContainer，手動建構我們的自訂序列化器
        // 1. 取得 ListContainer<T> 中，泛型 T 的 KSerializer (例如 SimpleUser.serializer())
        val innerType = kType.arguments.first().type
          ?: error("Cannot get generic type argument from ListContainer.")
        val innerTypeSerializer = serializer(innerType)

        // 2. 用它來建立 ListContainerSerializer
        @Suppress("UNCHECKED_CAST")
        ListContainerSerializer(innerTypeSerializer) as KSerializer<T>
      } else {
        // 不是 ListContainer，使用預設的尋找方式
        serializer<T>()
      }

      val schema = T::class.toJsonSchema(title, description)

      return Impl(ser, schema, T::class)
    }

    @PublishedApi
    internal class Impl<T : Any>(
      override val serializer: KSerializer<T>,
      override val jsonSchema: JsonSchemaSpec,
      override val kClass: KClass<T>
    ) : FormatSpec<T> {
      override fun toString() = "FormatSpec(serializer=$serializer, jsonSchema=$jsonSchema)"
    }
  }
}

