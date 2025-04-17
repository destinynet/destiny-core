package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsonToolsTest {
  // Simple data class
  data class Foo(val id: Int, val name: String)

  // Holder for Map<String, List<Foo>>
  data class MapListFooHolder(val mapList: Map<String, List<Foo>>)

  // Enum key
  enum class MyEnum {
    A,
    B
  }

  data class MapEnumHolder(val mapEnum: Map<MyEnum, String>)

  private val logger = KotlinLogging.logger { }

  @Test
  fun `toJsonSchema_simple data class`() {
    val spec = Foo::class.toJsonSchema("Foo", "A foo class")
    val schema = spec.schema
    logger.info { "schema: $schema" }
    // top-level object
    assertEquals("object", schema["type"]!!.jsonPrimitive.content)
    // properties id and name
    val props = schema["properties"]!!.jsonObject
    assertTrue(props.containsKey("id"))
    assertTrue(props.containsKey("name"))
    assertEquals("integer", props["id"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    assertEquals("string", props["name"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    // required fields
    val req = schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
    assertEquals(setOf("id", "name"), req)
  }

  @Test
  fun `toJsonSchema_map of list of object`() {
    val spec = MapListFooHolder::class.toJsonSchema("Holder", null)
    val schema = spec.schema
    // mapList property
    val ml = schema["properties"]!!.jsonObject["mapList"]!!.jsonObject
    assertEquals("object", ml["type"]!!.jsonPrimitive.content)
    // additionalProperties should be an array schema
    val add = ml["additionalProperties"]!!.jsonObject
    assertEquals("array", add["type"]!!.jsonPrimitive.content)
    // items should be object schema for Foo
    val items = add["items"]!!.jsonObject
    assertEquals("object", items["type"]!!.jsonPrimitive.content)
    val fprops = items["properties"]!!.jsonObject
    assertTrue(fprops.containsKey("id"))
    assertTrue(fprops.containsKey("name"))
  }

  @Test
  fun `toJsonSchema_map with enum keys`() {
    val spec = MapEnumHolder::class.toJsonSchema("EnumHolder", null)
    val schema = spec.schema
    logger.info { "schema: $schema" }
    val me = schema["properties"]!!.jsonObject["mapEnum"]!!.jsonObject
    assertEquals("object", me["type"]!!.jsonPrimitive.content)
    assertEquals("Map with keys from MyEnum enum", me["description"]!!.jsonPrimitive.content)
    // no additionalProperties allowed
    assertEquals(false, me["additionalProperties"]!!.jsonPrimitive.boolean)
    val ep = me["properties"]!!.jsonObject
    assertTrue(ep.containsKey("A"))
    assertTrue(ep.containsKey("B"))
    // each value type is string
    assertEquals("string", ep["A"]!!.jsonObject["type"]!!.jsonPrimitive.content)
  }

  @Test
  fun `toEnumMapJsonSchema_direct enum to int`() {
    // test standalone toEnumMapJsonSchema
    val spec = toEnumMapJsonSchema<MyEnum, Int>("enumMap", "desc")
    assertEquals("enumMap", spec.name)
    assertEquals("desc", spec.description)
    val sch = spec.schema
    logger.info { "schema: $sch" }
    assertEquals("object", sch["type"]!!.jsonPrimitive.content)
    assertEquals("desc", sch["description"]!!.jsonPrimitive.content)
    // properties A and B
    val props = sch["properties"]!!.jsonObject
    assertTrue(props.containsKey("A"))
    assertTrue(props.containsKey("B"))
    // type of each is integer
    assertEquals("integer", props["A"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    // required includes both
    val req = sch["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
    assertEquals(setOf("A", "B"), req)
    // no additionalProperties
    assertEquals(false, sch["additionalProperties"]!!.jsonPrimitive.boolean)
  }
}
