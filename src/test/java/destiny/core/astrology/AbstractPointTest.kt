/**
 * Created by smallufo on 2022-02-16.
 */
package destiny.core.astrology

import mu.KotlinLogging
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.test.*

abstract class AbstractPointTest(private val kClass: KClass<out Point>) {

  protected val logger = KotlinLogging.logger { }

  private val locales = listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)

  init {
    logger.info { "testing $kClass" }
  }

  private fun ensureToStringLocale(point: Point) {
    locales.forEach { locale ->
      point.toString(locale).also {
        assertTrue(it.isNotBlank())
        assertNotSame('!', it[0])
        logger.info { "$locale of $point = $it" }
      }
    }
  }

  private fun ensureAbbreviation(point: Point) {
    locales.forEach { locale ->
      point.getAbbreviation(locale).also {
        assertTrue(it.isNotBlank())
        assertNotSame('!', it[0])
        logger.info { "$locale abbreviation $point = $it" }
      }
    }
  }


  @Test
  fun point_toStringLocale() {
    kClass.sealedSubclasses.map { it.objectInstance!! }
      .forEach { p: Point ->
        ensureToStringLocale(p)
        ensureAbbreviation(p)
      }
  }

  @Test
  fun iPoints_fromString() {

    val companion = kClass.companionObject

    companion?.objectInstance?.takeIf { it is IPoints<*> }
      ?.let { it as IPoints<*> }
      ?.let { iPoint ->
        kClass.sealedSubclasses.map { it.objectInstance }.forEach { p ->
          assertNotNull(p)

          locales.map { it to p.toString(it) }
            .map { (locale, string) ->
              Triple(locale, string, iPoint.fromString(string, locale))
            }.filter { (_, _, point) -> point != null }
            .map { (locale, string, point) -> Triple(locale, string, point!!) }
            .also { list ->
              assertTrue(list.isNotEmpty() , "$p(nameKey = '${p.nameKey}') 無法反查字串")
              logger.debug { "list size = ${list.size}" }
              list.forEach { (locale, string, point) ->

                assertNull(iPoint.fromString("$string X"))
                if (locale == Locale.ENGLISH) {
                  assertSame(p, iPoint.fromString(string))
                  assertSame(p, iPoint.fromString(string.lowercase(Locale.ENGLISH)))
                  assertSame(p, iPoint.fromString(string.uppercase(Locale.ENGLISH)))
                  logger.info { "OK [$locale] '$string' | '${string.lowercase(Locale.ENGLISH)}' | '${string.uppercase(Locale.ENGLISH)}' -> $point" }
                } else {
                  assertSame(p, point)
                  logger.info { "OK [$locale] '$string' -> $point" }
                }
              }
            }
        }
      }
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun iPoints_arrayShuffleSort() {
    val companion = kClass.companionObject

    companion?.objectInstance?.takeIf { it is IPoints<*> }
      .let { it as IPoints<*> }
      .let { iPoint ->
        val shuffled: List<Comparable<Point>> = kClass.sealedSubclasses
          .mapNotNull { it.objectInstance }
          .filter { it is Comparable<*> }
          .map { it as Comparable<Point> }
          .shuffled()

        sortedSetOf(*shuffled.toTypedArray()).toList().zip(iPoint.values).forEach { (p1, p2) ->
          logger.trace { "${p1::class} = $p1 , ${p2::class} = $p2" }
          assertTrue(p1 == p2)
          assertTrue(p1 === p2)
        }
      }
  }
}
