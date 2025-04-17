package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
  fun `simple data class`() {
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
  fun `map of list of object`() {
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
  fun `map with enum keys`() {
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
  fun `direct enum to int`() {
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
  
  // Primitive types: Float, Double, Boolean
  @Test
  fun `primitives float double boolean`() {
    data class PrimsHolder(val f: Float, val d: Double, val b: Boolean)
    val spec = PrimsHolder::class.toJsonSchema("PrimsHolder", null)
    val props = spec.schema["properties"]!!.jsonObject
    assertEquals("number", props["f"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    assertEquals("number", props["d"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    assertEquals("boolean", props["b"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    // all required since non-nullable
    val req = spec.schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
    assertEquals(setOf("f", "d", "b"), req)
  }

  // Date, LocalDate, LocalDateTime, BigInteger, BigDecimal map to string
  @Test
  fun `date and big types`() {
    data class DateHolder(
      val date: java.util.Date,
      val ld: java.time.LocalDate,
      val ldt: java.time.LocalDateTime,
      val bi: java.math.BigInteger,
      val bd: java.math.BigDecimal
    )
    val spec = DateHolder::class.toJsonSchema("DateHolder", null)
    val props = spec.schema["properties"]!!.jsonObject
    listOf("date", "ld", "ldt", "bi", "bd").forEach { name ->
      assertEquals("string", props[name]!!.jsonObject["type"]!!.jsonPrimitive.content)
    }
  }

  // Nullable field not in required
  @Test
  fun `nullable field`() {
    data class OptHolder(val req: String, val opt: Int?)
    val spec = OptHolder::class.toJsonSchema("OptHolder", null)
    val schema = spec.schema
    val props = schema["properties"]!!.jsonObject
    assertTrue(props.containsKey("req"))
    assertTrue(props.containsKey("opt"))
    val reqFields = schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
    assertEquals(setOf("req"), reqFields)
  }

  // Direct enum property
  @Test
  fun `enum property`() {
    data class HasEnum(val status: MyEnum)
    val spec = HasEnum::class.toJsonSchema("HasEnum", null)
    val prop = spec.schema["properties"]!!.jsonObject["status"]!!.jsonObject
    assertEquals("string", prop["type"]!!.jsonPrimitive.content)
    assertEquals("Enum of MyEnum", prop["description"]!!.jsonPrimitive.content)
    val enums = prop["enum"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
    assertEquals(setOf("A", "B"), enums)
    val req = spec.schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
    assertEquals(setOf("status"), req)
  }

  // List of primitives and Array<String>
  @Test
  fun `list and array of primitives`() {
    data class ListPrimsHolder(val ints: List<Int>, val strs: Array<String>)
    val spec = ListPrimsHolder::class.toJsonSchema("ListPrimsHolder", null)
    val props = spec.schema["properties"]!!.jsonObject
    val ints = props["ints"]!!.jsonObject
    assertEquals("array", ints["type"]!!.jsonPrimitive.content)
    assertEquals("integer", ints["items"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    val strs = props["strs"]!!.jsonObject
    assertEquals("array", strs["type"]!!.jsonPrimitive.content)
    assertEquals("string", strs["items"]!!.jsonObject["type"]!!.jsonPrimitive.content)
  }

  // Map<String, Boolean>
  @Test
  fun `toJsonSchema_map of primitives`() {
    data class MapPrims(val m: Map<String, Boolean>)
    val spec = MapPrims::class.toJsonSchema("MapPrims", null)
    val m = spec.schema["properties"]!!.jsonObject["m"]!!.jsonObject
    assertEquals("object", m["type"]!!.jsonPrimitive.content)
    val ap = m["additionalProperties"]!!.jsonObject
    assertEquals("boolean", ap["type"]!!.jsonPrimitive.content)
  }

  // Map<String, Foo>
  @Test
  fun `map of objects`() {
    data class MapObj(val m: Map<String, Foo>)
    val spec = MapObj::class.toJsonSchema("MapObj", null)
    val m = spec.schema["properties"]!!.jsonObject["m"]!!.jsonObject
    assertEquals("object", m["type"]!!.jsonPrimitive.content)
    val ap = m["additionalProperties"]!!.jsonObject
    assertEquals("object", ap["type"]!!.jsonPrimitive.content)
    val fprops = ap["properties"]!!.jsonObject
    assertTrue(fprops.containsKey("id"))
    assertTrue(fprops.containsKey("name"))
  }

  // Map<MyEnum, List<Foo>>
  @Test
  fun `map enum keys and list values`() {
    data class MapEnumList(val m: Map<MyEnum, List<Foo>>)
    val spec = MapEnumList::class.toJsonSchema("MapEnumList", null)
    val m = spec.schema["properties"]!!.jsonObject["m"]!!.jsonObject
    assertEquals("object", m["type"]!!.jsonPrimitive.content)
    assertEquals("Map with keys from MyEnum enum", m["description"]!!.jsonPrimitive.content)
    assertFalse(m["additionalProperties"]!!.jsonPrimitive.boolean)
    val props = m["properties"]!!.jsonObject
    props.keys.forEach { key -> assertTrue(key == "A" || key == "B") }
    val arr = props["A"]!!.jsonObject
    assertEquals("array", arr["type"]!!.jsonPrimitive.content)
    val items = arr["items"]!!.jsonObject
    assertEquals("object", items["type"]!!.jsonPrimitive.content)
    val fprops = items["properties"]!!.jsonObject
    assertTrue(fprops.containsKey("id"))
    assertTrue(fprops.containsKey("name"))
  }

  // toEnumMapJsonSchema with List<Int> values
  @Test
  fun `non primitive value`() {
    val spec = toEnumMapJsonSchema<MyEnum, List<Int>>("enumMap2", null)
    val props = spec.schema["properties"]!!.jsonObject
    assertEquals("array", props["A"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    assertEquals("array", props["B"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    val req = spec.schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
    assertEquals(setOf("A", "B"), req)
    assertFalse(spec.schema["additionalProperties"]!!.jsonPrimitive.boolean)
  }
}
