package destiny.tools.ai

import destiny.tools.KotlinLogging
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsonToolsTest {

  private val logger = KotlinLogging.logger { }

  // ========== Shared test classes ==========
  data class Foo(val id: Int, val name: String)

  enum class MyEnum { A, B }

  @JvmInline
  value class UserId(val value: String)

  @JvmInline
  value class Email(val value: String)

  @JvmInline
  value class UserEmail(val email: Email)

  // Circular reference test classes (must be at outer class level)
  data class Node(val value: String, val next: Node?)
  data class RefA(val name: String, val refB: RefB?)
  data class RefB(val id: Int, val refA: RefA?)
  data class TreeNode(val label: String, val children: List<TreeNode>)
  data class Shared(val data: String)
  data class Diamond(val left: Shared, val right: Shared)

  // ========== Nested Test Classes ==========

  @Nested
  inner class PrimitiveTypeTest {

    @Test
    fun string() {
      val spec = String::class.toJsonSchema("String", "A simple string")
      val schema = spec.schema
      logger.info { "schema: $schema" }
      assertEquals("string", schema["type"]!!.jsonPrimitive.content)
      assertEquals("A simple string", schema["description"]!!.jsonPrimitive.content)
    }

    @Test
    fun int() {
      val spec = Integer::class.toJsonSchema("Int", "A simple integer")
      val schema = spec.schema
      logger.info { "schema: $schema" }
      assertEquals("integer", schema["type"]!!.jsonPrimitive.content)
      assertEquals("A simple integer", schema["description"]!!.jsonPrimitive.content)
    }

    @Test
    fun `float double boolean`() {
      data class PrimsHolder(val f: Float, val d: Double, val b: Boolean)
      val spec = PrimsHolder::class.toJsonSchema("PrimsHolder", null)
      val props = spec.schema["properties"]!!.jsonObject
      assertEquals("number", props["f"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      assertEquals("number", props["d"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      assertEquals("boolean", props["b"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      val req = spec.schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
      assertEquals(setOf("f", "d", "b"), req)
    }

    @Test
    fun `date and big types map to string`() {
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
  }

  @Nested
  inner class ValueClassTest {

    @Test
    fun `value class support`() {
      val spec = UserId::class.toJsonSchema("UserId", "User identifier")
      val schema = spec.schema
      logger.info { "schema: $schema" }
      assertEquals("string", schema["type"]!!.jsonPrimitive.content)
      assertEquals("User identifier", schema["description"]!!.jsonPrimitive.content)
    }

    @Test
    fun `nested value class unwrap`() {
      val spec = UserEmail::class.toJsonSchema("UserEmail", "Nested email wrapper")
      val schema = spec.schema
      logger.info { "schema: $schema" }
      assertEquals("string", schema["type"]!!.jsonPrimitive.content)
      assertEquals("Nested email wrapper", schema["description"]!!.jsonPrimitive.content)
    }
  }

  @Nested
  inner class DataClassTest {

    @Test
    fun `simple data class`() {
      val spec = Foo::class.toJsonSchema("Foo", "A foo class")
      val schema = spec.schema
      logger.info { "schema: $schema" }
      assertEquals("object", schema["type"]!!.jsonPrimitive.content)
      val props = schema["properties"]!!.jsonObject
      assertTrue(props.containsKey("id"))
      assertTrue(props.containsKey("name"))
      assertEquals("integer", props["id"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      assertEquals("string", props["name"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      val req = schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
      assertEquals(setOf("id", "name"), req)
    }

    @Test
    fun `nullable field not in required`() {
      data class OptHolder(val req: String, val opt: Int?)
      val spec = OptHolder::class.toJsonSchema("OptHolder", null)
      val schema = spec.schema
      val props = schema["properties"]!!.jsonObject
      assertTrue(props.containsKey("req"))
      assertTrue(props.containsKey("opt"))
      val reqFields = schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
      assertEquals(setOf("req"), reqFields)
    }
  }

  @Nested
  inner class EnumTest {

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

    @Test
    fun `toEnumMapJsonSchema with int values`() {
      val spec = toEnumMapJsonSchema<MyEnum, Int>("enumMap", "desc")
      assertEquals("enumMap", spec.name)
      assertEquals("desc", spec.description)
      val sch = spec.schema
      logger.info { "schema: $sch" }
      assertEquals("object", sch["type"]!!.jsonPrimitive.content)
      assertEquals("desc", sch["description"]!!.jsonPrimitive.content)
      val props = sch["properties"]!!.jsonObject
      assertTrue(props.containsKey("A"))
      assertTrue(props.containsKey("B"))
      assertEquals("integer", props["A"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      val req = sch["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
      assertEquals(setOf("A", "B"), req)
      assertEquals(false, sch["additionalProperties"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `toEnumMapJsonSchema with list values`() {
      val spec = toEnumMapJsonSchema<MyEnum, List<Int>>("enumMap2", null)
      val props = spec.schema["properties"]!!.jsonObject
      assertEquals("array", props["A"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      assertEquals("array", props["B"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      val req = spec.schema["required"]!!.jsonArray.map { it.jsonPrimitive.content }.toSet()
      assertEquals(setOf("A", "B"), req)
      assertFalse(spec.schema["additionalProperties"]!!.jsonPrimitive.boolean)
    }
  }

  @Nested
  inner class CollectionTest {

    @Test
    fun `list and array of primitives`() {
      data class ListPrimsHolder(val ints: List<Int>, val strings: Array<String>) {
        override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (other !is ListPrimsHolder) return false
          if (ints != other.ints) return false
          if (!strings.contentEquals(other.strings)) return false
          return true
        }

        override fun hashCode(): Int {
          var result = ints.hashCode()
          result = 31 * result + strings.contentHashCode()
          return result
        }
      }

      val spec = ListPrimsHolder::class.toJsonSchema("ListPrimsHolder", null)
      val props = spec.schema["properties"]!!.jsonObject
      val ints = props["ints"]!!.jsonObject
      assertEquals("array", ints["type"]!!.jsonPrimitive.content)
      assertEquals("integer", ints["items"]!!.jsonObject["type"]!!.jsonPrimitive.content)
      val strs = props["strings"]!!.jsonObject
      assertEquals("array", strs["type"]!!.jsonPrimitive.content)
      assertEquals("string", strs["items"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    }
  }

  @Nested
  inner class MapTest {

    @Test
    fun `map of primitives`() {
      data class MapPrims(val m: Map<String, Boolean>)
      val spec = MapPrims::class.toJsonSchema("MapPrims", null)
      val m = spec.schema["properties"]!!.jsonObject["m"]!!.jsonObject
      assertEquals("object", m["type"]!!.jsonPrimitive.content)
      val ap = m["additionalProperties"]!!.jsonObject
      assertEquals("boolean", ap["type"]!!.jsonPrimitive.content)
    }

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

    @Test
    fun `map with enum keys`() {
      data class MapEnumHolder(val mapEnum: Map<MyEnum, String>)
      val spec = MapEnumHolder::class.toJsonSchema("EnumHolder", null)
      val schema = spec.schema
      logger.info { "schema: $schema" }
      val me = schema["properties"]!!.jsonObject["mapEnum"]!!.jsonObject
      assertEquals("object", me["type"]!!.jsonPrimitive.content)
      assertTrue { me["description"]!!.jsonPrimitive.content.startsWith("Map with keys from MyEnum enum") }
      assertEquals(false, me["additionalProperties"]!!.jsonPrimitive.boolean)
      val ep = me["properties"]!!.jsonObject
      assertTrue(ep.containsKey("A"))
      assertTrue(ep.containsKey("B"))
      assertEquals("string", ep["A"]!!.jsonObject["type"]!!.jsonPrimitive.content)
    }

    @Test
    fun `map of list of objects`() {
      data class MapListFooHolder(val mapList: Map<String, List<Foo>>)
      val spec = MapListFooHolder::class.toJsonSchema("Holder", null)
      val schema = spec.schema
      val ml = schema["properties"]!!.jsonObject["mapList"]!!.jsonObject
      assertEquals("object", ml["type"]!!.jsonPrimitive.content)
      val add = ml["additionalProperties"]!!.jsonObject
      assertEquals("array", add["type"]!!.jsonPrimitive.content)
      val items = add["items"]!!.jsonObject
      assertEquals("object", items["type"]!!.jsonPrimitive.content)
      val fprops = items["properties"]!!.jsonObject
      assertTrue(fprops.containsKey("id"))
      assertTrue(fprops.containsKey("name"))
    }

    @Test
    fun `map with enum keys and list values`() {
      data class MapEnumList(val m: Map<MyEnum, List<Foo>>)
      val spec = MapEnumList::class.toJsonSchema("MapEnumList", null)
      val m = spec.schema["properties"]!!.jsonObject["m"]!!.jsonObject
      assertEquals("object", m["type"]!!.jsonPrimitive.content)
      assertTrue { m["description"]!!.jsonPrimitive.content.startsWith("Map with keys from MyEnum enum") }
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
  }

  @Nested
  inner class CircularReferenceTest {

    @Test
    fun `self referencing class should not stackoverflow`() {
      val spec = Node::class.toJsonSchema("Node", "A linked list node")
      val schema = spec.schema
      logger.info { "Self-referencing schema: $schema" }

      assertEquals("object", schema["type"]!!.jsonPrimitive.content)
      val props = schema["properties"]!!.jsonObject
      assertTrue(props.containsKey("value"))
      assertTrue(props.containsKey("next"))
      assertEquals("string", props["value"]!!.jsonObject["type"]!!.jsonPrimitive.content)

      // The 'next' property should have $ref due to circular reference
      val nextProp = props["next"]!!.jsonObject
      assertTrue(nextProp.containsKey("\$ref"), "Should have \$ref for circular reference")
      assertEquals("#/definitions/Node", nextProp["\$ref"]!!.jsonPrimitive.content)
    }

    @Test
    fun `mutual reference should not stackoverflow`() {
      val spec = RefA::class.toJsonSchema("RefA", "Class A referencing B")
      val schema = spec.schema
      logger.info { "Mutual reference schema: $schema" }

      assertEquals("object", schema["type"]!!.jsonPrimitive.content)
      val props = schema["properties"]!!.jsonObject
      assertTrue(props.containsKey("name"))
      assertTrue(props.containsKey("refB"))

      // RefB property should be an object
      val refBProp = props["refB"]!!.jsonObject
      assertEquals("object", refBProp["type"]!!.jsonPrimitive.content)

      // RefB's refA property should have $ref due to circular reference back to RefA
      val refBProps = refBProp["properties"]!!.jsonObject
      assertTrue(refBProps.containsKey("refA"))
      val refAProp = refBProps["refA"]!!.jsonObject
      assertTrue(refAProp.containsKey("\$ref"), "RefB.refA should have \$ref for circular reference")
      assertEquals("#/definitions/RefA", refAProp["\$ref"]!!.jsonPrimitive.content)
    }

    @Test
    fun `tree structure with self referencing list should not stackoverflow`() {
      val spec = TreeNode::class.toJsonSchema("TreeNode", "A tree node with children")
      val schema = spec.schema
      logger.info { "Tree structure schema: $schema" }

      assertEquals("object", schema["type"]!!.jsonPrimitive.content)
      val props = schema["properties"]!!.jsonObject
      assertTrue(props.containsKey("label"))
      assertTrue(props.containsKey("children"))

      // children should be an array
      val childrenProp = props["children"]!!.jsonObject
      assertEquals("array", childrenProp["type"]!!.jsonPrimitive.content)

      // items should have $ref due to circular reference
      val items = childrenProp["items"]!!.jsonObject
      assertTrue(items.containsKey("\$ref"), "Items should have \$ref for circular reference")
      assertEquals("#/definitions/TreeNode", items["\$ref"]!!.jsonPrimitive.content)
    }

    @Test
    fun `diamond pattern same class in different branches should work`() {
      val spec = Diamond::class.toJsonSchema("Diamond", "Diamond pattern")
      val schema = spec.schema
      logger.info { "Diamond pattern schema: $schema" }

      assertEquals("object", schema["type"]!!.jsonPrimitive.content)
      val props = schema["properties"]!!.jsonObject
      assertTrue(props.containsKey("left"))
      assertTrue(props.containsKey("right"))

      // Both left and right should be fully expanded Shared objects (not $ref)
      val leftProp = props["left"]!!.jsonObject
      assertEquals("object", leftProp["type"]!!.jsonPrimitive.content)
      assertFalse(leftProp.containsKey("\$ref"), "left should be fully expanded, not \$ref")
      assertTrue(leftProp["properties"]!!.jsonObject.containsKey("data"))

      val rightProp = props["right"]!!.jsonObject
      assertEquals("object", rightProp["type"]!!.jsonPrimitive.content)
      assertFalse(rightProp.containsKey("\$ref"), "right should be fully expanded, not \$ref")
      assertTrue(rightProp["properties"]!!.jsonObject.containsKey("data"))
    }
  }
}
