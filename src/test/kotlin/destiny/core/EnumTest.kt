package destiny.core

import destiny.tools.KotlinLogging
import destiny.tools.getDescription
import destiny.tools.getTitle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readLines
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


abstract class EnumTest {

  inline fun <reified T : Enum<T>> getEnumValues(enumClass: KClass<out Enum<T>>): Array<out Enum<T>> = enumClass.java.enumConstants

  inline fun <reified E : Enum<E>> testEnums(
    kClass: KClass<out Enum<E>>, ensureNameTitleNotEqual: Boolean = true, locales: List<Locale> =
      listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)
  ) {

    val logger = KotlinLogging.logger { }

    locales.forEach { locale ->
      getEnumValues(kClass).forEach {
        it.getTitle(locale).also { title ->
          assertNotNull(title)
          if (ensureNameTitleNotEqual) {
            if (locale.language != "en") {
              assertNotEquals(title, it.name)
            }
          }

          logger.info { "${it.name} : title($locale) = $title" }
        }

        it.getDescription(locale).also { desc ->
          assertNotNull(desc)
          if (ensureNameTitleNotEqual) {
            if (locale.language != "en") {
              assertNotEquals(desc, it.name)
            }
          }
          logger.info { "${it.name} : description($locale) = $desc" }
        }
      }
    }
  }

  /**
   * 各語系 properties 檔的 key 集合必須與 base bundle **完全相同**，且 base 要涵蓋全部 enum 常數。
   *
   * **為什麼 [testEnums] 抓不到漏翻譯**：`ResourceBundle` 找不到 key 時會 fallback 到 base
   * bundle，於是未翻譯項目的 `getTitle(ja)` 仍會回傳繁中字串 —— 非 null、也不等於 enum name，
   * 那兩條斷言都會通過。`EventType_ja.properties` 曾經整整少一個 `MAJOR_FINANCIAL_GAIN`，
   * 日文使用者看到的是繁體中文，而測試全綠。要抓到它只能比對**檔案本身**的 key 集合。
   *
   * 只檢查**已存在**的語系檔 —— 不強制每個 enum 都要有 `_ja`（目前 100 個 bundle 家族中僅 15 個有）。
   * 規則是「翻了就要翻完」，而不是「每個都要翻」。
   *
   * **與 `I18nBundlesCoverageTest` 的分工**：那支跑遍全 repo 驗語系檔與 base 的 key 集合一致，
   * 但它不知道哪個 bundle 服務哪個 enum。此處補的是那三件它做不到的事 ——
   * base 必須涵蓋每個 enum 常數、base 不得有 enum 已移除的殘留 key、至少要有一個語系檔。
   *
   * @param resourceDir base bundle 所在目錄，相對於 `src/main/resources`，例如 `destiny/core`
   * @param suffixes    要檢查的 key 後綴；預設只有 `.title`
   */
  fun assertBundleParity(
    enumName: String,
    constants: List<String>,
    resourceDir: String,
    suffixes: List<String> = listOf(".title")
  ) {
    val dir = Paths.get("src/main/resources", resourceDir)
    val base = dir.resolve("$enumName.properties")
    assertTrue(base.exists(), "base bundle 不存在：$base")

    fun keysOf(p: Path): Set<String> = p.readLines()
      .map { it.trim() }
      .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains('=') }
      .map { it.substringBefore('=').trim() }
      .toSet()

    val baseKeys = keysOf(base)

    // base 必須涵蓋每個 enum 常數
    val expected = constants.flatMap { c -> suffixes.map { "$c$it" } }.toSet()
    assertTrue(
      (expected - baseKeys).isEmpty(),
      "$enumName.properties 缺少 key：${(expected - baseKeys).sorted()}"
    )
    assertTrue(
      (baseKeys - expected).isEmpty(),
      "$enumName.properties 有多餘 key（enum 已移除？）：${(baseKeys - expected).sorted()}"
    )

    // 已存在的語系檔要與 base 完全對齊
    val variants = dir.listDirectoryEntries("$enumName" + "_*.properties")
    assertTrue(variants.isNotEmpty(), "$enumName 至少該有一個語系檔")
    for (v in variants) {
      val vk = keysOf(v)
      assertTrue((baseKeys - vk).isEmpty(), "${v.fileName} 缺少翻譯：${(baseKeys - vk).sorted()}")
      assertTrue((vk - baseKeys).isEmpty(), "${v.fileName} 有多餘 key：${(vk - baseKeys).sorted()}")
    }
  }

}


/**
 * Reflection: Access enum values and valueOf via KClass
 * https://youtrack.jetbrains.com/issue/KT-14743
 */

fun KType.enumValueOf(name: String, serializer: KSerializer<Any?> = serializer(this)): Enum<*> {
//  if (serializer.descriptor.kind != SerialKind.ENUM) {
//    throw error("enumValueOf must be used on enum")
//  }
  return Json.decodeFromString(serializer, "\"$name\"") as Enum<*>
}

fun KType.enumValuesName(serializer: KSerializer<Any?> = serializer(this)): List<String> {
//  if (serializer.descriptor.kind != SerialKind.ENUM) {
//    throw error("enumValuesName must be used on enum")
//  }
  val enumName = serializer.descriptor.serialName
  return serializer.descriptor.elementNames.map { it.removePrefix(enumName) }
}


fun KType.enumValues(serializer: KSerializer<Any?> = serializer(this)): List<Enum<*>> {
//  if (serializer.descriptor.kind != SerialKind.ENUM) {
//    throw error("enumValues must be used on enum")
//  }
  return enumValuesName(serializer).map { enumValueOf(it, serializer) }
}


abstract class AbstractEnumTest<T : Enum<*>>(val t: KType) {

  @Test
  inline fun <reified T : Enum<T>> testLocaleStrings(
    values: List<T>,
    locales: List<Locale> =
      listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)
  ) {

    val logger = KotlinLogging.logger { }

    locales.forEach { locale ->
      values.forEach {
        it.getTitle(locale).also { title ->
          assertNotNull(title)
          logger.info { "${it.name} : title($locale) = $title" }
        }

        it.getDescription(locale).also { desc ->
          assertNotNull(desc)
          logger.info { "${it.name} : description($locale) = $desc" }
        }
      }
    }
  }
}

