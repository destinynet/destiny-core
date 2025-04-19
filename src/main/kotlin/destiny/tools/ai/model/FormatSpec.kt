/**
 * Created by smallufo on 2025-04-19.
 */
package destiny.tools.ai.model

import destiny.tools.ai.JsonSchemaSpec
import destiny.tools.ai.toJsonSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KClass
import kotlin.reflect.typeOf


interface FormatSpec<T : Any> {
  val serializer: KSerializer<T>
  val jsonSchema: JsonSchemaSpec
  val kClass: KClass<T>

  companion object {
    inline fun <reified T : Any> of(
      title: String,
      description: String
    ): FormatSpec<T> {
      require(T::class != Unit::class) { "Unit type is not allowed" }

      val ser = EmptySerializersModule().serializerOrNull(typeOf<T>())
        ?: error("❌ ${T::class.simpleName} is not @Serializable or no contextual serializer registered")

      val schema = T::class.toJsonSchema(title, description)

      @Suppress("UNCHECKED_CAST")
      return Impl(ser as KSerializer<T>, schema, T::class)
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

