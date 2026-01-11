/**
 * Created by smallufo on 2025-04-04.
 */
package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
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

private val logger = KotlinLogging.logger { }

fun <T : Any> KClass<T>.toJsonSchema(name: String, description: String? = null): JsonSchemaSpec {
  val kType = this.starProjectedType

  // 如果是 @JvmInline 的 value class，就取出裡面的唯一 property 的型別
  val effectiveType = if (this.isValue) {
    unwrapValueType(this)
  } else {
    kType
  }

  val primitiveType = effectiveType.toJsonSchemaType()

  // 若是 primitive 型別（非 object），直接回傳 primitive schema
  if (primitiveType != "object") {
    val schema = buildJsonObject {
      put("type", primitiveType)
      description?.let { put("description", it) }
    }
    return JsonSchemaSpec(name, description, schema)
  }


  // 否則當作複合型別（如 data class）處理
  // 使用 visited set 追蹤遞迴路徑，偵測循環參照
  val visited = mutableSetOf<KClass<*>>()
  val schema = buildJsonObject {
    put("type", "object")
    putJsonObject("properties") {
      processClassProperties(this@toJsonSchema, visited)
    }
    addRequiredFields(this@toJsonSchema)
    description?.let { put("description", it) }
  }

  return JsonSchemaSpec(name, description, schema)
}

fun unwrapValueType(kClass: KClass<*>): KType {
  val visited = mutableSetOf<KClass<*>>()
  var currentClass = kClass

  while (currentClass.isValue) {
    if (!visited.add(currentClass)) {
      logger.warn { "Detected value class cycle at ${currentClass.qualifiedName}, fallback to string" }
      return typeOf<String>()
    }

    val innerProp = currentClass.memberProperties.firstOrNull()
      ?: error("Value class ${currentClass.simpleName} has no properties")

    val nextType = innerProp.returnType
    val nextClassifier = nextType.classifier
    if (nextClassifier is KClass<*>) {
      currentClass = nextClassifier
    } else {
      return nextType
    }
  }
  return currentClass.starProjectedType
}


inline fun <reified K : Enum<K>, reified V> toEnumMapJsonSchema(
  name: String,
  description: String? = null
): JsonSchemaSpec {
  val keyClass = K::class
  val valueType = typeOf<V>()

  val schema = buildJsonObject {
    put("type", "object")
    put("description", description ?: "Map with keys from enum ${keyClass.simpleName}. Note : only return mentioned enum keys, Please ignore un-mentioned keys.")

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
private fun JsonObjectBuilder.processClassProperties(kClass: KClass<*>, visited: MutableSet<KClass<*>>) {
  // 加入 visited set，如果已存在表示循環參照
  if (!visited.add(kClass)) {
    logger.warn { "Detected circular reference at ${kClass.qualifiedName}, using \$ref placeholder" }
    return
  }

  try {
    kClass.memberProperties.forEach { property ->
      val propName = property.findAnnotation<SerialName>()?.value ?: property.name
      putJsonObject(propName) {
        val propertyType = property.returnType

        val classifier = propertyType.classifier
        // 防禦性處理 generic / star-projection
        if (classifier !is KClass<*>) return@putJsonObject

        // Handle Map types
        if (propertyType.isSubtypeOf(typeOf<Map<*, *>>())) {
          handleMapType(propertyType, visited)
        }
        // Handle List/Array types
        else if (propertyType.isSubtypeOf(typeOf<List<*>>()) || propertyType.isSubtypeOf(typeOf<Array<*>>())) {
          handleCollectionType(propertyType, visited)
        }
        // Handle Enum types
        else if (propertyType.classifier is KClass<*> && (propertyType.classifier as KClass<*>).java.isEnum) {
          handleEnumType(propertyType.classifier as KClass<*>)
        }
        // Handle nested object types
        else if (propertyType.toJsonSchemaType() == "object" && propertyType.classifier is KClass<*>) {
          handleObjectType(propertyType.classifier as KClass<*>, visited)
        }
        // Handle primitive types
        else {
          put("type", propertyType.toJsonSchemaType())
        }
      }
    }
  } finally {
    // 離開時從 visited 移除，允許同一類別在不同分支出現
    visited.remove(kClass)
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
private fun JsonObjectBuilder.handleMapType(mapType: KType, visited: MutableSet<KClass<*>>) {
  put("type", "object")
  val keyType = mapType.arguments.getOrNull(0)?.type
  val valueType = mapType.arguments.getOrNull(1)?.type

  if (keyType?.classifier is KClass<*> && (keyType.classifier as KClass<*>).java.isEnum) {
    val enumClass = keyType.classifier as KClass<*>
    val enumValues = enumClass.java.enumConstants

    put("description", "Map with keys from ${enumClass.simpleName} enum. Note : only return mentioned enum keys, Please ignore un-mentioned keys.")
    put("additionalProperties", JsonPrimitive(false))
    putJsonObject("properties") {
      enumValues.forEach { enumValue ->
        putJsonObject(enumValue.toString()) {
          addValueTypeSchema(valueType, visited)
        }
      }
    }
  } else {
    putJsonObject("additionalProperties") {
      addValueTypeSchema(valueType, visited)
    }
  }
}

private fun JsonObjectBuilder.addValueTypeSchema(valueType: KType?, visited: MutableSet<KClass<*>>) {
  if (valueType != null) {
    when {
      valueType.isSubtypeOf(typeOf<List<*>>()) || valueType.isSubtypeOf(typeOf<Array<*>>()) ->
        handleCollectionType(valueType, visited)

      valueType.toJsonSchemaType() == "object" && valueType.classifier is KClass<*>         ->
        handleObjectType(valueType.classifier as KClass<*>, visited)

      else                                                                                  ->
        put("type", valueType.toJsonSchemaType())
    }
  } else {
    put("type", "string")
  }
}

// Handle Collection type properties (List, Array)
private fun JsonObjectBuilder.handleCollectionType(collectionType: KType, visited: MutableSet<KClass<*>>) {
  put("type", "array")

  // Add items schema based on the collection element type
  val elementType = collectionType.arguments.firstOrNull()?.type
  if (elementType != null) {
    putJsonObject("items") {
      if (elementType.toJsonSchemaType() == "object" && elementType.classifier is KClass<*>) {
        val elementClass = elementType.classifier as KClass<*>
        // 檢查是否循環參照
        if (elementClass in visited) {
          put("\$ref", "#/definitions/${elementClass.simpleName}")
          put("description", "Circular reference to ${elementClass.simpleName}")
        } else {
          // It's a list of objects, detail the object structure
          put("type", "object")
          putJsonObject("properties") {
            processClassProperties(elementClass, visited)
          }
          // Add required fields for the item class
          addRequiredFields(elementClass)
        }
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
private fun JsonObjectBuilder.handleObjectType(objectClass: KClass<*>, visited: MutableSet<KClass<*>>) {
  // 檢查是否循環參照
  if (objectClass in visited) {
    put("\$ref", "#/definitions/${objectClass.simpleName}")
    put("description", "Circular reference to ${objectClass.simpleName}")
    return
  }

  put("type", "object")
  putJsonObject("properties") {
    processClassProperties(objectClass, visited)
  }
  // Add required fields for the nested object
  addRequiredFields(objectClass)
}
