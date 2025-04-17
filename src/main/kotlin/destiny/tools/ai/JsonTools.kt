/**
 * Created by smallufo on 2025-04-04.
 */
package destiny.tools.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf

fun KType.toJsonSchemaType(): String {
  return when {
    this.isSubtypeOf(typeOf<String>())                                          -> "string"
    this.isSubtypeOf(typeOf<Int>()) || this.isSubtypeOf(typeOf<Long>())         -> "integer"
    this.isSubtypeOf(typeOf<Float>()) || this.isSubtypeOf(typeOf<Double>())     -> "number"
    this.isSubtypeOf(typeOf<Boolean>())                                         -> "boolean"
    this.isSubtypeOf(typeOf<List<*>>()) || this.isSubtypeOf(typeOf<Array<*>>()) -> "array"
    this.isSubtypeOf(typeOf<Map<*, *>>())                                       -> "object"
    this.isSubtypeOf(typeOf<java.util.Date>()) ||
      this.isSubtypeOf(typeOf<java.time.LocalDate>()) ||
      this.isSubtypeOf(typeOf<java.time.LocalDateTime>())                       -> "string"

    this.isSubtypeOf(typeOf<java.math.BigInteger>()) ||
      this.isSubtypeOf(typeOf<java.math.BigDecimal>())                          -> "string"

    this.isSubtypeOf(typeOf<Enum<*>>())                                         -> "string"
    else                                                                        -> "object"
  }
}

fun <T : Any> KClass<T>.toJsonSchema(name: String, description: String? = null): JsonSchemaSpec {
  val schema = buildJsonObject {
    put("type", "object")
    putJsonObject("properties") {
      // Process all properties of the class
      processClassProperties(this@toJsonSchema)
    }

    // Add required fields for the top-level class
    addRequiredFields(this@toJsonSchema)
  }

  return JsonSchemaSpec(name, description, schema)
}

inline fun <reified K : Enum<K>, reified V> toEnumMapJsonSchema(
  name: String,
  description: String? = null
): JsonSchemaSpec {
  val keyClass = K::class
  val valueType = typeOf<V>()

  val schema = buildJsonObject {
    put("type", "object")
    put("description", description ?: "Map with keys from enum ${keyClass.simpleName}")

    putJsonObject("properties") {
      keyClass.java.enumConstants.forEach { enumVal ->
        putJsonObject(enumVal.name) {
          put("type", valueType.toJsonSchemaType())
        }
      }
    }

    putJsonArray("required") {
      keyClass.java.enumConstants.forEach { add(JsonPrimitive(it.name)) }
    }

    put("additionalProperties", JsonPrimitive(false))
  }

  return JsonSchemaSpec(name, description, schema)
}

// Extension function to process class properties recursively
private fun JsonObjectBuilder.processClassProperties(kClass: KClass<*>) {

  kClass.memberProperties.forEach { property ->
    val propName = property.findAnnotation<SerialName>()?.value ?: property.name
    putJsonObject(propName) {
      val propertyType = property.returnType

      val classifier = propertyType.classifier
      // 防禦性處理 generic / star-projection
      if (classifier !is KClass<*>) return@putJsonObject

      // Handle Map types
      if (propertyType.isSubtypeOf(typeOf<Map<*, *>>())) {
        handleMapType(propertyType)
      }
      // Handle List/Array types
      else if (propertyType.isSubtypeOf(typeOf<List<*>>()) || propertyType.isSubtypeOf(typeOf<Array<*>>())) {
        handleCollectionType(propertyType)
      }
      // Handle Enum types
      else if (propertyType.classifier is KClass<*> && (propertyType.classifier as KClass<*>).java.isEnum) {
        handleEnumType(propertyType.classifier as KClass<*>)
      }
      // Handle nested object types
      else if (propertyType.toJsonSchemaType() == "object" && propertyType.classifier is KClass<*>) {
        handleObjectType(propertyType.classifier as KClass<*>)
      }
      // Handle primitive types
      else {
        put("type", propertyType.toJsonSchemaType())
      }
    }
  }
}

// Extension function to add required fields to a JsonObjectBuilder
private fun JsonObjectBuilder.addRequiredFields(kClass: KClass<*>) {
  val requiredProps = kClass.memberProperties
    .filter { !it.returnType.isMarkedNullable }
    .map { it.findAnnotation<SerialName>()?.value ?: it.name }

  if (requiredProps.isNotEmpty()) {
    putJsonArray("required") {
      requiredProps.forEach { add(JsonPrimitive(it)) }
    }
  }
}

// Handle Map type properties, including nested collections and objects
private fun JsonObjectBuilder.handleMapType(mapType: KType) {
  put("type", "object")
  val keyType = mapType.arguments.getOrNull(0)?.type
  val valueType = mapType.arguments.getOrNull(1)?.type

  if (keyType?.classifier is KClass<*> && (keyType.classifier as KClass<*>).java.isEnum) {
    val enumClass = keyType.classifier as KClass<*>
    val enumValues = enumClass.java.enumConstants

    put("description", "Map with keys from ${enumClass.simpleName} enum")
    put("additionalProperties", JsonPrimitive(false))
    putJsonObject("properties") {
      enumValues.forEach { enumValue ->
        putJsonObject(enumValue.toString()) {
          if (valueType != null) {
            when {
              valueType.isSubtypeOf(typeOf<List<*>>()) || valueType.isSubtypeOf(typeOf<Array<*>>()) ->
                handleCollectionType(valueType)
              valueType.toJsonSchemaType() == "object" && valueType.classifier is KClass<*> ->
                handleObjectType(valueType.classifier as KClass<*>)
              else ->
                put("type", valueType.toJsonSchemaType())
            }
          } else {
            put("type", "string")
          }
        }
      }
    }
  } else {
    putJsonObject("additionalProperties") {
      if (valueType != null) {
        when {
          valueType.isSubtypeOf(typeOf<List<*>>()) || valueType.isSubtypeOf(typeOf<Array<*>>()) ->
            handleCollectionType(valueType)
          valueType.toJsonSchemaType() == "object" && valueType.classifier is KClass<*> ->
            handleObjectType(valueType.classifier as KClass<*>)
          else ->
            put("type", valueType.toJsonSchemaType())
        }
      } else {
        put("type", "string")
      }
    }
  }
}

// Handle Collection type properties (List, Array)
private fun JsonObjectBuilder.handleCollectionType(collectionType: KType) {
  put("type", "array")

  // Add items schema based on the collection element type
  val elementType = collectionType.arguments.firstOrNull()?.type
  if (elementType != null) {
    putJsonObject("items") {
      if (elementType.toJsonSchemaType() == "object" && elementType.classifier is KClass<*>) {
        // It's a list of objects, detail the object structure
        val elementClass = elementType.classifier as KClass<*>
        put("type", "object")
        putJsonObject("properties") {
          processClassProperties(elementClass)
        }
        // Add required fields for the item class
        addRequiredFields(elementClass as KClass<*>)
      } else {
        // Simple type
        put("type", elementType.toJsonSchemaType())
      }
    }
  }
}

// Handle Enum type properties
private fun JsonObjectBuilder.handleEnumType(enumClass: KClass<*>) {
  put("type", "string")
  put("description", "Enum of ${enumClass.simpleName}")
  putJsonArray("enum") {
    enumClass.java.enumConstants.forEach {
      add(JsonPrimitive(it.toString()))
    }
  }
}

// Handle Object type properties
private fun JsonObjectBuilder.handleObjectType(objectClass: KClass<*>) {
  put("type", "object")
  putJsonObject("properties") {
    processClassProperties(objectClass)
  }
  // Add required fields for the nested object
  addRequiredFields(objectClass)
}
