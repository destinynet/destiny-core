/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

/**
 * 十二宮位 (遷移 or 相貌)
 */
enum class House(val value: String, val abbr: Char) {
  命宮("命宮", '命'),
  兄弟("兄弟", '兄'),
  夫妻("夫妻", '偶'),

  子女("子女", '子'),
  財帛("財帛", '財'),
  疾厄("疾厄", '疾'),

  遷移("遷移", '遷'),

  /** 太乙派，沒有遷移宮 : [HouseSeqTaiyiImpl]  */
  交友("交友", '奴'), // 奴僕
  官祿("官祿", '官'),

  田宅("田宅", '田'),
  福德("福德", '福'),
  父母("父母", '父'), // 父母宮（又名相貌宮） , 但太乙派，把「父母」以及「相貌」拆成兩個宮

  相貌("相貌", '相');

  fun next(n: Int, seq: IHouseSeq): House {
    return seq.next(this, n)
  }

  fun prev(n: Int, seq: IHouseSeq): House {
    return seq.prev(this, n)
  }

}
