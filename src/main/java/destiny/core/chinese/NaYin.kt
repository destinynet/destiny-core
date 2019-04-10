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
enum class NaYin(override val fiveElement: FiveElement) : IFiveElement , Serializable {
  海中金(金),
  爐中火(火),
  大林木(木),
  路旁土(土),
  劍鋒金(金),
  山頭火(火),
  澗下水(水),
  城頭土(土),
  白臘金(金),
  楊柳木(木),
  井泉水(水),
  屋上土(土),
  霹靂火(火),
  松柏木(木),
  長流水(水),
  砂中金(金),
  山下火(火),
  平地木(木),
  壁上土(土),
  金箔金(金),
  覆燈火(火),
  天河水(水),
  大驛土(土),
  釵釧金(金),
  桑柘木(木),
  大溪水(水),
  砂中土(土),
  天上火(火),
  石榴木(木),
  大海水(水);

  companion object {

    private val map = mapOf(
      甲子 to 海中金,
      乙丑 to 海中金,
      丙寅 to 爐中火,
      丁卯 to 爐中火,
      戊辰 to 大林木,
      己巳 to 大林木,
      庚午 to 路旁土,
      辛未 to 路旁土,
      壬申 to 劍鋒金,
      癸酉 to 劍鋒金,
      甲戌 to 山頭火,
      乙亥 to 山頭火,
      丙子 to 澗下水,
      丁丑 to 澗下水,
      戊寅 to 城頭土,
      己卯 to 城頭土,
      庚辰 to 白臘金,
      辛巳 to 白臘金,
      壬午 to 楊柳木,
      癸未 to 楊柳木,
      甲申 to 井泉水,
      乙酉 to 井泉水,
      丙戌 to 屋上土,
      丁亥 to 屋上土,
      戊子 to 霹靂火,
      己丑 to 霹靂火,
      庚寅 to 松柏木,
      辛卯 to 松柏木,
      壬辰 to 長流水,
      癸巳 to 長流水,
      甲午 to 砂中金,
      乙未 to 砂中金,
      丙申 to 山下火,
      丁酉 to 山下火,
      戊戌 to 平地木,
      己亥 to 平地木,
      庚子 to 壁上土,
      辛丑 to 壁上土,
      壬寅 to 金箔金,
      癸卯 to 金箔金,
      甲辰 to 覆燈火,
      乙巳 to 覆燈火,
      丙午 to 天河水,
      丁未 to 天河水,
      戊申 to 大驛土,
      己酉 to 大驛土,
      庚戌 to 釵釧金,
      辛亥 to 釵釧金,
      壬子 to 桑柘木,
      癸丑 to 桑柘木,
      甲寅 to 大溪水,
      乙卯 to 大溪水,
      丙辰 to 砂中土,
      丁巳 to 砂中土,
      戊午 to 天上火,
      己未 to 天上火,
      庚申 to 石榴木,
      辛酉 to 石榴木,
      壬戌 to 大海水,
      癸亥 to 大海水
                           )

    fun getFiveElement(sb: IStemBranch): FiveElement {
      return map.getValue(StemBranch[sb.stem , sb.branch]).fiveElement
    }

    fun getNaYin(sb: IStemBranch): NaYin? {
      return getNaYin(sb.stem, sb.branch)
    }

    fun getNaYin(stem: Stem, branch: Branch): NaYin? {
      return if (stem.index % 2 == branch.index % 2) {
        NaYin.map[StemBranch[stem, branch]]
      } else
        null
    }

    /** 詳情 , 三個字 , 例如「海中金」  */
    fun getDesc(sb: StemBranch, locale: Locale): String {
      val resourceKey = map[sb]!!.name
      return ResourceBundle.getBundle(NaYin::class.java.name, locale).getString(resourceKey)
    }
  }

}
