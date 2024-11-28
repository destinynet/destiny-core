/**
 * Created by smallufo on 2022-07-12.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.chinese.eightwords.EwPattern
import java.util.*


enum class Book {
  象吉通書,
  鰲頭通書,
  星平會海,
  黃曆解秘,
  生育禮俗,
  小兒關煞圖
}

sealed class ChildHazard : EwPattern {

  init {
    @Suppress("LeakingThis")
    register(this)
  }

  protected fun readResolve(): Any {
    return getByName(this::class.simpleName!!)
      ?: throw IllegalArgumentException("${this::class.simpleName} does not exist")
  }

  data object 百日關 : ChildHazard()

  data object 千日關 : ChildHazard()

  data object 閻王關 : ChildHazard()

  data object 鬼門關 : ChildHazard()

  data object 雞飛關 : ChildHazard()

  data object 鐵蛇關 : ChildHazard()

  data object 斷橋關 : ChildHazard()

  data object 落井關 : ChildHazard()

  data object 四柱關 : ChildHazard()

  data object 短命關 : ChildHazard()

  data object 浴盆關 : ChildHazard()

  data object 湯火關 : ChildHazard()

  data object 水火關 : ChildHazard()

  data object 深水關 : ChildHazard()

  data object 夜啼關 : ChildHazard()

  data object 白虎關 : ChildHazard()

  data object 天狗關 : ChildHazard()

  data object 四季關 : ChildHazard()

  data object 急腳關 : ChildHazard()

  data object 急腳煞 : ChildHazard()

  data object 五鬼關 : ChildHazard()

  data object 金鎖關 : ChildHazard()

  data object 直難關 : ChildHazard()

  data object 取命關 : ChildHazard()

  data object 斷腸關 : ChildHazard()

  data object 埋兒關 : ChildHazard()

  data object 天吊關 : ChildHazard()

  data object 和尚關 : ChildHazard()

  data object 撞命關 : ChildHazard()

  data object 下情關 : ChildHazard()

  data object 劫煞關 : ChildHazard()

  data object 血刃關 : ChildHazard()

  data object 基敗關 : ChildHazard()

  data object 將軍箭 : ChildHazard()

  /** 雷公打腦關 */
  data object 雷公關 : ChildHazard()

  //data object 桃花煞 : ChildHazard()

  //data object 紅豔煞 : ChildHazard()

  //data object 流霞煞 : ChildHazard()


  /** 不實作 [getNotes] , 交由 [ChildHazardDescriptor.getDescription] 處理 (避免 depend on [ResourceBundle] ) */

  companion object {
    private val registry = mutableMapOf<String, ChildHazard>()

    private fun register(hazard: ChildHazard) {
      val name = hazard::class.simpleName ?: throw IllegalStateException("Unnamed ChildHazard")
      registry[name] = hazard
    }

    fun getByName(name: String): ChildHazard? = registry[name]

  }
}


fun ChildHazard.getBookNote(locale: Locale, book: Book): String? {
  return ResourceBundle.getBundle(ChildHazard::class.qualifiedName!!, locale).let { resourceBundle ->

    val key = "${this::class.simpleName}.${book.name}"
    try {
      resourceBundle.getString(key)
    } catch (e: MissingResourceException) {
      null
    }
  }
}
