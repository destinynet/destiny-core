package destiny.tools

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LocaleComparatorTest {

  @Test
  fun testCompare() {
    val locales = arrayOf(Locale.ENGLISH, Locale.TAIWAN, Locale.SIMPLIFIED_CHINESE, Locale.FRANCE)

    println("\nDefault : " + Locale.TRADITIONAL_CHINESE)
    locales.sortedWith(LocaleComparator(Locale.TRADITIONAL_CHINESE)).also {
      assertEquals(Locale.TRADITIONAL_CHINESE , it[0])
      println(it)
    }


    println("\nDefault : " + Locale.SIMPLIFIED_CHINESE)
    locales.sortedWith(LocaleComparator(Locale.SIMPLIFIED_CHINESE)).also {
      assertEquals(Locale.SIMPLIFIED_CHINESE , it[0])
      println(it)
    }

    println("\nDefault : " + Locale.ENGLISH)
    locales.sortedWith(LocaleComparator(Locale.ENGLISH)).also {
      assertEquals(Locale.ENGLISH , it[0])
      println(it)
    }


    println("\nDefault : " + Locale.FRENCH)
    locales.sortedWith(LocaleComparator(Locale.FRENCH)).also {
      assertEquals(Locale.FRANCE , it[0])
      println(it)
    }
  }

  @Test
  fun testLangMap() {
    val map = object : HashMap<Locale, String>() {
      init {
        put(Locale.TRADITIONAL_CHINESE, "導演")
        put(Locale.SIMPLIFIED_CHINESE, "导演")
        put(Locale.ENGLISH, "Director")
      }
    }

    assert("導演" == map[map.keys.sortedWith(LocaleComparator(Locale.TRADITIONAL_CHINESE)).first()])
    assert("导演" == map[map.keys.sortedWith(LocaleComparator(Locale.SIMPLIFIED_CHINESE)).first()])
    assert("Director" == map[map.keys.sortedWith(LocaleComparator(Locale.ENGLISH)).first()])
    assert("Director" == map[map.keys.sortedWith(LocaleComparator(Locale.FRANCE)).first()])
  }
}
