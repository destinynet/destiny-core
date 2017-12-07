/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import java.util.*

/**
 * 十二宮位 (遷移 or 相貌)
 */
enum class House constructor(private val value: String) {
  命宮("命宮"),
  兄弟("兄弟"),
  夫妻("夫妻"),

  子女("子女"),
  財帛("財帛"),
  疾厄("疾厄"),

  遷移("遷移"), /** 太乙派，沒有遷移宮 : [HouseSeqTaiyiImpl]  */
  交友("交友"),
  官祿("官祿"),

  田宅("田宅"),
  福德("福德"),
  父母("父母"), // 父母宮（又名相貌宮） , 但太乙派，把「父母」以及「相貌」拆成兩個宮

  相貌("相貌");

  fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(House::class.java.name, locale).getString(value)
  }

  fun next(n: Int, seq: IHouseSeq): House {
    return seq.next(this, n)
  }

  fun prev(n: Int, seq: IHouseSeq): House {
    return seq.prev(this, n)
  }

}// 太乙派使用
