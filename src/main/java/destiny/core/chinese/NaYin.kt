/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese

import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.StemBranch.*
import java.io.Serializable
import java.util.*

/**
 * 納音五行
 * 甲子乙丑海中金　丙寅丁卯爐中火　戊辰己巳大林木　庚午辛未路旁土
 * 壬申癸酉劍鋒金　甲戌乙亥山頭火　丙子丁丑澗下水　戊寅己卯城頭土
 * 庚辰辛巳白臘金　壬午癸未楊柳木　甲申乙酉井泉水　丙戌丁亥屋上土
 * 戊子己丑霹靂火　庚寅辛卯松柏木　壬辰癸巳長流水　甲午乙未砂中金
 * 丙申丁酉山下火　戊戌己亥平地木　庚子辛丑壁上土　壬寅癸卯金箔金
 * 甲辰乙巳覆燈火　丙午丁未天河水　戊申己酉大驛土　庚戌辛亥釵釧金
 * 壬子癸丑桑柘木　甲寅乙卯大溪水　丙辰丁巳砂中土　戊午己未天上火
 * 庚申辛酉石榴木　壬戌癸亥大海水
 */
class NaYin : Serializable {
  companion object {

    private val map = mapOf(
        甲子 to Pair(金, "海中金"),
        乙丑 to Pair(金, "海中金"),
        丙寅 to Pair(火, "爐中火"),
        丁卯 to Pair(火, "爐中火"),
        戊辰 to Pair(木, "大林木"),
        己巳 to Pair(木, "大林木"),
        庚午 to Pair(土, "路旁土"),
        辛未 to Pair(土, "路旁土"),
        壬申 to Pair(金, "劍鋒金"),
        癸酉 to Pair(金, "劍鋒金"),
        甲戌 to Pair(火, "山頭火"),
        乙亥 to Pair(火, "山頭火"),
        丙子 to Pair(水, "澗下水"),
        丁丑 to Pair(水, "澗下水"),
        戊寅 to Pair(土, "城頭土"),
        己卯 to Pair(土, "城頭土"),
        庚辰 to Pair(金, "白臘金"),
        辛巳 to Pair(金, "白臘金"),
        壬午 to Pair(木, "楊柳木"),
        癸未 to Pair(木, "楊柳木"),
        甲申 to Pair(水, "井泉水"),
        乙酉 to Pair(水, "井泉水"),
        丙戌 to Pair(土, "屋上土"),
        丁亥 to Pair(土, "屋上土"),
        戊子 to Pair(火, "霹靂火"),
        己丑 to Pair(火, "霹靂火"),
        庚寅 to Pair(木, "松柏木"),
        辛卯 to Pair(木, "松柏木"),
        壬辰 to Pair(水, "長流水"),
        癸巳 to Pair(水, "長流水"),
        甲午 to Pair(金, "砂中金"),
        乙未 to Pair(金, "砂中金"),
        丙申 to Pair(火, "山下火"),
        丁酉 to Pair(火, "山下火"),
        戊戌 to Pair(木, "平地木"),
        己亥 to Pair(木, "平地木"),
        庚子 to Pair(土, "壁上土"),
        辛丑 to Pair(土, "壁上土"),
        壬寅 to Pair(金, "金箔金"),
        癸卯 to Pair(金, "金箔金"),
        甲辰 to Pair(火, "覆燈火"),
        乙巳 to Pair(火, "覆燈火"),
        丙午 to Pair(水, "天河水"),
        丁未 to Pair(水, "天河水"),
        戊申 to Pair(土, "大驛土"),
        己酉 to Pair(土, "大驛土"),
        庚戌 to Pair(金, "釵釧金"),
        辛亥 to Pair(金, "釵釧金"),
        壬子 to Pair(木, "桑柘木"),
        癸丑 to Pair(木, "桑柘木"),
        甲寅 to Pair(水, "大溪水"),
        乙卯 to Pair(水, "大溪水"),
        丙辰 to Pair(土, "砂中土"),
        丁巳 to Pair(土, "砂中土"),
        戊午 to Pair(火, "天上火"),
        己未 to Pair(火, "天上火"),
        庚申 to Pair(木, "石榴木"),
        辛酉 to Pair(木, "石榴木"),
        壬戌 to Pair(水, "大海水"),
        癸亥 to Pair(水, "大海水")
    )

    fun getFiveElement(sb: StemBranch): FiveElement {
      return map[sb]!!.first
    }

    /** 詳情 , 三個字 , 例如「海中金」  */
    fun getDesc(sb: StemBranch, locale: Locale): String {
      val resourceKey = map[sb]!!.second
      return ResourceBundle.getBundle(NaYin::class.java.name, locale).getString(resourceKey)
    }
  }

}
