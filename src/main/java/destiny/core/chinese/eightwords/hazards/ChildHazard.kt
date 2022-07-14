/**
 * Created by smallufo on 2022-07-12.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.IPattern
import java.util.*


enum class Book {
  象吉通書,
  鰲頭通書,
  星平會海,
  黃曆解秘,
  生育禮俗,
  小兒關煞圖
}

sealed class ChildHazard : IPattern {

  object 百日關 : ChildHazard()

  object 千日關 : ChildHazard()

  object 閻王關 : ChildHazard()

  object 鬼門關 : ChildHazard()

  object 雞飛關 : ChildHazard()

  object 鐵蛇關 : ChildHazard()

  object 斷橋關 : ChildHazard()

  object 落井關 : ChildHazard()

  object 四柱關 : ChildHazard()

  object 短命關 : ChildHazard()

  object 浴盆關 : ChildHazard()

  object 湯火煞 : ChildHazard()

  object 水火關 : ChildHazard()

  object 深水關 : ChildHazard()

  data class 將軍箭(val arrows: Int) : ChildHazard() {
    override fun getNotes(locale: Locale): String {
      return "一箭傷人三歲死，二箭傷人六歲亡，三箭傷人九歲亡，四箭傷人十二亡。"
    }
  }

  object 桃花煞 : ChildHazard()

  //object 紅豔煞 : ChildHazard()

  object 流霞煞 : ChildHazard()

  object 夜啼關 : ChildHazard()

  object 白虎關 : ChildHazard()

  object 雷公打腦關 : ChildHazard()

  object 天狗關 : ChildHazard()

  object 四季關 : ChildHazard()

  object 急腳關 : ChildHazard()

  object 急腳煞 : ChildHazard()

  object 五鬼關 : ChildHazard()

  object 金鎖關 : ChildHazard()

  object 金鎖匙 : ChildHazard()

  object 直難關 : ChildHazard()

  object 取命關 : ChildHazard()

  object 斷腸關 : ChildHazard()

  object 埋兒關 : ChildHazard()

  object 天吊關 : ChildHazard()

  object 休庵關 : ChildHazard()

  object 撞命關 : ChildHazard()

  object 下情關 : ChildHazard()

  object 劫煞關 : ChildHazard()

  object 血刃關 : ChildHazard()

  object 基敗關 : ChildHazard()
}


fun ChildHazard.getNotes(locale: Locale, book: Book?): String {
  return ResourceBundle.getBundle(ChildHazard::class.qualifiedName!!, locale).let { resourceBundle ->
    book?.name?.let { bookName ->
      val key = "${this::class.simpleName}.${bookName}"

      try {
        resourceBundle.getString(key)
      } catch (e: MissingResourceException) {
        this::class.simpleName
      }
    } ?: this.getNotes(locale)?:""
  }
}
