/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.Gender.女
import destiny.core.Gender.男
import destiny.core.astrology.IPoints
import destiny.core.astrology.Point
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarLucky.Companion.fun右弼_月支
import destiny.core.chinese.ziwei.StarLucky.Companion.fun右弼_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun左輔_月支
import destiny.core.chinese.ziwei.StarLucky.Companion.fun左輔_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun文昌
import destiny.core.chinese.ziwei.StarLucky.Companion.fun文曲
import destiny.core.chinese.ziwei.ZStar.Type.*
import java.util.*
import kotlin.reflect.KClass

/**
 * 乙級星有總共有34顆
 */
sealed class StarMinor(nameKey: String, type: Type) : ZStar(nameKey, ZStar::class.java.name, type) {

  object 天官 : StarMinor("天官", 年干) // 吉
  object 天福 : StarMinor("天福", 年干) // 吉
  object 天廚 : StarMinor("天廚", 年干)
  object 天刑 : StarMinor("天刑", 月) //   兇
  object 天姚 : StarMinor("天姚", 月) //   兇
  object 解神 : StarMinor("解神", 月) // 吉
  object 天巫 : StarMinor("天巫", 月) // 吉
  object 天月 : StarMinor("天月", 月) //   兇
  object 陰煞 : StarMinor("陰煞", 月) //   兇
  object 台輔 : StarMinor("台輔", 時) // 吉
  object 封誥 : StarMinor("封誥", 時) // 吉
  object 天空 : StarMinor("天空", 年支) //   兇
  object 天哭 : StarMinor("天哭", 年支) //   兇
  object 天虛 : StarMinor("天虛", 年支) //   兇
  object 龍池 : StarMinor("龍池", 年支) // 吉
  object 鳳閣 : StarMinor("鳳閣", 年支) // 吉
  object 紅鸞 : StarMinor("紅鸞", 年支) // 吉
  object 天喜 : StarMinor("天喜", 年支) // 吉
  object 孤辰 : StarMinor("孤辰", 年支) //   兇
  object 寡宿 : StarMinor("寡宿", 年支) //   兇
  object 蜚廉 : StarMinor("蜚廉", 年支)
  object 破碎 : StarMinor("破碎", 年支)
  object 華蓋 : StarMinor("華蓋", 年支) //   兇
  object 咸池 : StarMinor("咸池", 年支) //   兇
  object 天德 : StarMinor("天德", 年支) //   兇?
  object 月德 : StarMinor("月德", 年支) //   兇?
  object 天才 : StarMinor("天才", 年月時) // 吉
  object 天壽 : StarMinor("天壽", 年月時) // 吉

  object 三台 : StarMinor("三台", 月日) // 吉
  object 八座 : StarMinor("八座", 月日) // 吉
  object 恩光 : StarMinor("恩光", 日時) // 吉
  object 天貴 : StarMinor("天貴", 日時) // 吉

  object 天使 : StarMinor("天使", 宮位) //   兇 , 天使屬陰水 , 主災病
  object 天傷 : StarMinor("天傷", 宮位) //   兇 , 天傷屬陽水 , 主虛耗

  object 陽空 : StarMinor("陽空", 年)
  object 陰空 : StarMinor("陰空", 年)

  object 正空 : StarMinor("正空", 年干)  // 截空 陰陽相同
  object 傍空 : StarMinor("傍空", 年干)  // 截空 陰陽相反

  object 紅艷 : StarMinor("紅艷", 年干)

  override fun compareTo(other: ZStar): Int {
    return if (other is StarMinor) {
      values.indexOf(this) - values.indexOf(other)
    } else {
      super.compareTo(other)
    }
  }

  companion object : IPoints<StarMinor> {

    override val type: KClass<out Point> = StarMinor::class

    override val values by lazy {
      arrayOf(天官, 天福, 天廚, 天刑, 天姚, 解神, 天巫, 天月, 陰煞, 台輔, 封誥, 天空, 天哭, 天虛, 龍池, 鳳閣, 紅鸞, 天喜, 孤辰, 寡宿, 蜚廉, 破碎, 華蓋, 咸池, 天德, 月德,
              天才, 天壽, 三台, 八座, 恩光, 天貴, 天使, 天傷, 陽空, 陰空, 正空, 傍空, 紅艷)
    }

    override fun fromString(value: String, locale: Locale): StarMinor? {
      return values.firstOrNull {
        it.nameKey == value
      }
    }

    /** 天官 : 年干 -> 地支  */
    val fun天官 = { year: Stem ->
      when (year) {
        甲 -> 未
        乙 -> 辰
        丙 -> 巳
        丁 -> 寅
        戊 -> 卯
        己, 辛 -> 酉
        庚 -> 亥
        壬 -> 戌
        癸 -> 午
      }
    }

    /** 天福 : 年干 -> 地支  */
    val fun天福 = { year: Stem ->
      when (year) {
        甲 -> 酉
        乙 -> 申
        丙 -> 子
        丁 -> 亥
        戊 -> 卯
        己 -> 寅
        庚, 壬 -> 午
        辛, 癸 -> 巳
      }
    }

    /**
     * 天廚 : 年干 -> 地支
     * 安天廚訣曰：『甲丁食蛇口，乙戊辛馬方，丙從鼠口得，己食於猴房，庚食虎頭上，壬雞癸豬堂。』
     */
    val fun天廚 = { year: Stem ->
      when (year) {
        甲, 丁 -> 巳
        乙, 戊, 辛 -> 午
        丙 -> 子
        己 -> 申
        庚 -> 寅
        壬 -> 酉
        癸 -> 亥
      }
    }

    /** 天刑 : 月數 -> 地支  */
    val fun天刑_月數 = { month: Int -> Branch[month + 8] }

    /** 天刑 : 月支 -> 地支  */
    val fun天刑_月支 = { month: Branch -> Branch[month.index + 7] }

    /** 天刑(整合版) : (月數 或 月支) -> 地支  */
    //  public final static Function3<ZContext.MonthType , Integer , Branch , Branch> fun天刑 = (type , monthNum , monthBranch) -> {
    //    switch (type) {
    //      case MONTH_LUNAR: return fun天刑_月數.invoke(monthNum);
    //      case MONTH_SOLAR: return fun天刑_月支.invoke(monthBranch);
    //      default: throw new AssertionError("Error : " + type);
    //    }
    //  };


    /** 天姚 : 月數 -> 地支  */
    val fun天姚_月數 = { monthNum: Int -> Branch[monthNum] }

    /** 天姚 : 月支 -> 地支  */
    val fun天姚_月支 = { month: Branch -> month.next(11) }

    /** 天姚(整合版) : (月數 或 月支) -> 地支  */
    //  public final static Function3<ZContext.MonthType , Integer , Branch , Branch> fun天姚 = (type , monthNum , monthBranch) -> {
    //    switch (type) {
    //      case MONTH_LUNAR: return fun天姚_月數.invoke(monthNum);
    //      case MONTH_SOLAR: return fun天姚_月支.invoke(monthBranch);
    //      default: throw new AssertionError("Error : " + type);
    //    }
    //  };


    /** 解神 : 月數 -> 地支  */
    val fun解神_月數 = { month: Int ->
      when (month) {
        1, 2 -> 申
        3, 4 -> 戌
        5, 6 -> 子
        7, 8 -> 寅
        9, 10 -> 辰
        11, 12 -> 午
        else -> throw AssertionError(month)
      }
    }

    /** 解神 : 月支 -> 地支  */
    val fun解神_月支 = { month: Branch ->
      when (month) {
        寅, 卯 -> 申
        辰, 巳 -> 戌
        午, 未 -> 子
        申, 酉 -> 寅
        戌, 亥 -> 辰
        子, 丑 -> 午
      }
    }

    /** 解神(整合版) : (月數 或 月支) -> 地支  */
    //  public final static Function3<ZContext.MonthType , Integer , Branch , Branch> fun解神 = (type , monthNum , monthBranch) -> {
    //    switch (type) {
    //      case MONTH_LUNAR: return fun解神_月數.invoke(monthNum);
    //      case MONTH_SOLAR: return fun解神_月支.invoke(monthBranch);
    //      default: throw new AssertionError("Error : " + type);
    //    }
    //  };


    /** 天巫 : 月數 -> 地支  */
    val fun天巫_月數 = { month: Int ->
      when (month) {
        1, 5, 9 -> 巳
        2, 6, 10 -> 申
        3, 7, 11 -> 寅
        4, 8, 12 -> 亥
        else -> throw AssertionError(month)
      }
    }

    /** 天巫 : 月支 -> 地支  */
    val fun天巫_月支 = { month: Branch ->
      when (BranchTools.trilogy(month)) {
        火 -> 巳
        木 -> 申
        水 -> 寅
        金 -> 亥
        else -> throw AssertionError(month)
      }
    }

    /** 天巫(整合版) : (月數 或 月支) -> 地支  */
    //  public final static Function3<ZContext.MonthType , Integer , Branch , Branch> fun天巫 = (type , monthNum , monthBranch) -> {
    //    switch (type) {
    //      case MONTH_LUNAR: return fun天巫_月數.invoke(monthNum);
    //      case MONTH_SOLAR: return fun天巫_月支.invoke(monthBranch);
    //      default: throw new AssertionError("Error : " + type);
    //    }
    //  };


    /** 天月 : 月數 -> 地支  */
    val fun天月_月數 = { month: Int ->
      when (month) {
        1 -> 戌
        2 -> 巳
        3 -> 辰
        4, 9 -> 寅
        5, 8 -> 未
        6 -> 卯
        7 -> 亥
        10, 12 -> 午
        11 -> 戌
        else -> throw AssertionError(month)
      }
    }

    /** 天月 : 月支 -> 地支  */
    val fun天月_月支 = { month: Branch ->
      when (month) {
        寅 -> 戌
        卯 -> 巳
        辰 -> 辰
        巳, 戌 -> 寅
        午, 酉 -> 未
        未 -> 卯
        申 -> 亥
        亥, 丑 -> 午
        子 -> 戌
      }
    }

    /** 天月(整合版) : (月數 或 月支) -> 地支  */
    //  public final static Function3<ZContext.MonthType , Integer , Branch , Branch> fun天月 = (type , monthNum , monthBranch) -> {
    //    switch (type) {
    //      case MONTH_LUNAR: return fun天月_月數.invoke(monthNum);
    //      case MONTH_SOLAR: return fun天月_月支.invoke(monthBranch);
    //      default: throw new AssertionError("Error : " + type);
    //    }
    //  };


    /** 陰煞 : 月數 -> 地支  */
    val fun陰煞_月數 = { month: Int ->
      when (month) {
        1, 7 -> 寅
        2, 8 -> 子
        3, 9 -> 戌
        4, 10 -> 申
        5, 11 -> 午
        6, 12 -> 辰
        else -> throw AssertionError(month)
      }
    }

    /** 陰煞 : 月支 -> 地支  */
    val fun陰煞_月支 = { month: Branch ->
      when (month) {
        寅, 申 -> 寅
        卯, 酉 -> 子
        辰, 戌 -> 戌
        巳, 亥 -> 申
        午, 子 -> 午
        未, 丑 -> 辰
      }
    }

    /** 陰煞(整合版) : (月數 或 月支) -> 地支  */
    //  public final static Function3<ZContext.MonthType , Integer , Branch , Branch> fun陰煞 = (type , monthNum , monthBranch) -> {
    //    switch (type) {
    //      case MONTH_LUNAR: return fun陰煞_月數.invoke(monthNum);
    //      case MONTH_SOLAR: return fun陰煞_月支.invoke(monthBranch);
    //      default: throw new AssertionError("Error : " + type);
    //    }
    //  };


    /** 台輔 : 時支 -> 地支  */
    val fun台輔 = { hour: Branch -> Branch[hour.index + 6] }

    /** 封誥 : 時支 -> 地支  */
    val fun封誥 = { hour: Branch -> Branch[hour.index + 2] }

    /** 天空 : 年支 -> 地支. 注意其與 [StarUnlucky.fun地空] 是不同的星/演算法  */
    val fun天空 = { year: Branch -> Branch[year.index + 1] }

    /** 天哭 : 年支 -> 地支  */
    val fun天哭 = { year: Branch -> Branch[6 - year.index] }

    /** 天虛 : 年支 -> 地支  */
    val fun天虛 = { year: Branch -> Branch[year.index + 6] }

    /** 龍池 : 年支 -> 地支  */
    val fun龍池 = { year: Branch -> Branch[year.index + 4] }

    /** 鳳閣 : 年支 -> 地支  */
    val fun鳳閣 = { year: Branch -> Branch[10 - year.index] }

    /** 紅鸞 : 年支 -> 地支  */
    val fun紅鸞 = { year: Branch -> Branch[3 - year.index] }

    /** 天喜 : 年支 -> 地支  */
    val fun天喜 = { year: Branch -> Branch[9 - year.index] }

    /** 孤辰 : 年支 -> 地支  */
    val fun孤辰 = { year: Branch ->
      when (BranchTools.direction(year)) {
        水 -> 寅
        木 -> 巳
        火 -> 申
        金 -> 亥
        else -> throw AssertionError(year)
      }
    }

    /** 寡宿 : 年支 -> 地支  */
    val fun寡宿 = { year: Branch ->
      when (BranchTools.direction(year)) {
        水 -> 戌
        木 -> 丑
        火 -> 辰
        金 -> 未
        else -> throw AssertionError(year)
      }
    }

    /** 蜚廉 : 年支 -> 地支  */
    val fun蜚廉 = { year: Branch ->
      when (year) {
        子 -> 申
        丑 -> 酉
        寅 -> 戌
        卯 -> 巳
        辰 -> 午
        巳 -> 未
        午 -> 寅
        未 -> 卯
        申 -> 辰
        酉 -> 亥
        戌 -> 子
        亥 -> 丑
      }
    }

    /** 破碎 : 年支 -> 地支  */
    val fun破碎 = { year: Branch ->
      when (year) {
        子, 午, 卯, 酉 -> 巳
        寅, 巳, 申, 亥 -> 酉
        辰, 戌, 丑, 未 -> 丑
      }
    }


    /** 華蓋 : 年支 -> 地支
     * 子辰申年在辰, 丑巳酉年在丑, 寅午戍年在戍, 卯未亥年在未
     */
    val fun華蓋 = { year: Branch ->
      when (BranchTools.trilogy(year)) {
        水 -> 辰
        金 -> 丑
        火 -> 戌
        木 -> 未
        else -> throw AssertionError(year)
      }
    }

    /** 咸池 : 年支 -> 地支
     * 子辰申年在酉, 丑巳酉年在午, 寅午戍年在卯, 卯未亥年在子
     */
    val fun咸池 = { year: Branch -> Characters.getPeach(year) }

    /** 天德 : 年支 -> 地支
     * 天德星從酉上起子，順數至流年太歲上是也。
     * 出生年 子..丑..寅..卯..辰..巳..午..未..申..酉..戌..亥
     * 天德宫 酉..戌..亥..子..丑..寅..卯..辰..巳..午..未..申
     */
    val fun天德 = { year: Branch -> Branch[year.index + 9] }

    /** 月德 : 年支 -> 地支
     * 月德星從子上起，順至流年太歲上是也。
     * 出生年 子..丑..寅..卯..辰..巳..午..未..申..酉..戌..亥
     * 月德宫 巳..午..未..申..酉..戌..亥..子..丑..寅..卯..辰
     */
    val fun月德 = { year: Branch -> Branch[year.index + 5] }

    /**
     * 天才 (年支 , 月數 , 時支) -> 地支
     * 天才由命宮起子, 順行至本生 「年支」安之.
     */
    val fun天才 = { year: Branch, month: Int, hour: Branch -> Ziwei.getMainHouseBranch(month, hour).next(year.index) }

    /**
     * 天壽 (年支 , 月數 , 時支) -> 地支
     * 天壽由身宮起子, 順行至本生 「年支」安之  */
    val fun天壽 = { year: Branch, month: Int, hour: Branch -> Ziwei.getBodyHouseBranch(month, hour).next(year.index) }


    /** 三台 : (月數,日數) -> 地支. 從「左輔」取初一，順行，數到本日生  */
    val fun三台_月數 = { month: Int, day: Int -> fun左輔_月數.invoke(month).next(day - 1) }

    /** 三台 : (月支,日數) -> 地支. 從「左輔」取初一，順行，數到本日生  */
    val fun三台_月支 = { month: Branch, day: Int -> fun左輔_月支.invoke(month).next(day - 1) }

    /** 八座 : (月數,日數) -> 地支. 從「右弼」取初一，逆行，數到本日生  */
    val fun八座_月數 = { month: Int, day: Int -> fun右弼_月數.invoke(month).prev(day - 1) }

    /** 八座 : (月支,日數) -> 地支. 從「右弼」取初一，逆行，數到本日生  */
    val fun八座_月支 = { month: Branch, day: Int -> fun右弼_月支.invoke(month).prev(day - 1) }

    /** 恩光 : (日數,時支) -> 地支. 從「文昌」上取初一，順行，數到本日生，再後退一步  */
    val fun恩光 = { day : Int, hour : Branch -> fun文昌.invoke(hour).next(day - 2) }

    /** 天貴 : (日數,時支) -> 地支. 從「文曲」上取初一，順行，數到本日生，再後退一步
     * NOTE : 有的書寫「逆行」，跟據比對，應該是錯誤  */
    val fun天貴 = { day : Int, hour : Branch -> fun文曲.invoke(hour).next(day - 2) }

    /** 天傷 : 兩種算法，第 1 種 : 固定於交友宮 (亦即：遷移宮地支-1)  */
    val fun天傷_fixed交友 = { 遷移宮地支 : Branch -> 遷移宮地支.prev(1) }

    /** 天使 : 兩種算法，第 1 種 : 固定於疾厄宮（亦即：遷移宮地支+1） */
    val fun天使_fixed疾厄 = { 遷移宮地支 : Branch -> 遷移宮地支.next(1) }

    /** 天傷 : 兩種算法，第 2 種 : 陽男陰女順行，安天傷於交友宮 (亦即：遷移宮地支-1)  */
    val fun天傷_陽順陰逆 = { 遷移宮地支 : Branch , 年干 : Stem , gender : Gender ->
      if (年干.booleanValue && gender === 男 || !年干.booleanValue && gender === 女) {
        遷移宮地支.prev(1)
      } else {
        遷移宮地支.next(1)
      }
    }

    /** 天使 : 兩種算法，第 2 種 : 陽男陰女順行，安天使於疾厄宮 (亦即：遷移宮地支+1)  */
    val fun天使_陽順陰逆 = { 遷移宮地支 : Branch , 年干 : Stem , gender : Gender ->
      if (年干.booleanValue && gender === 男 || !年干.booleanValue && gender === 女) {
        遷移宮地支.next(1)
      } else {
        遷移宮地支.prev(1)
      }
    }

    /** 干支 -> 地支 */
    val fun陽空 = { stemBranch : StemBranch ->
      stemBranch.empties.first { b -> b.indexFromOne % 2 == 1 }
    }

    /** 干支 -> 地支 */
    val fun陰空 = { stemBranch : StemBranch ->
      stemBranch.empties.first { b -> b.indexFromOne %2 == 0 }
    }


    /**
     * 截空
     * 截空星的安法.截路字義上就是截斷路.就現今社會要截斷路.用重機械.挖土機.很簡單.
     * 但是紫微斗數是在一千都年前所有的.在那一個時代能夠有能力截路的.就是大自然界的水.
     * 所以截空星的位置.都會出現在命盤中.天干屬水的位置.也就是.壬.癸的位置.
     * 例如.
     * 甲年.己年生人.命盤一月為丙寅月.七月為壬申月.八月為癸酉月.所以
     * 甲年生人截空就在壬申的位置.
     * 己年生人就會在癸酉的位置.
     * 但這其中.因為派別的關係.戊.癸.年生人就有不一樣.
     * 我派是會出現在壬戌.和癸亥.這兩個位置.
     * 有一派會出現在甲子.和乙丑.的位置.
     *
     * 截空星的用法.截空星是空宮位.例如進入疾厄宮.就會減低生病的機會.
     *
     * 甲己之年空申酉
     * 乙庚之年午未求
     * 丙辛生人空辰巳
     * 丁壬寅卯深作憂
     * 戊 生人空子丑!
     *
     * 甲or己年出生者，正截空在申宮，傍截空在酉宮。
     * 乙or庚年出生者，正截空在午宮，傍截空在未宮。
     * 丙or辛年出生者，正截空在辰宮，傍截空在巳宮。
     * 丁or壬年出生者，正截空在寅宮，傍截空在卯宮。
     * 戊or癸年出生者，正截空在子宮，傍截空在丑宮。
     *
     * 見此圖 ： http://imgur.com/peeBDQ5
     * 戊癸年， A法空子丑， B法空戌亥
     * (好像B法比較有道理)
     */
    val fun正空_A = { stem : Stem ->
      when (stem) {
        甲 -> 申
        己 -> 酉
        乙 -> 午
        庚 -> 未
        丙 -> 辰
        辛 -> 巳
        丁 -> 寅
        壬 -> 卯

        戊 -> 子
        癸 -> 丑
      }
    }

    val fun正空_B = { stem : Stem ->
      when (stem) {
        甲 -> 申
        己 -> 酉
        乙 -> 午
        庚 -> 未
        丙 -> 辰
        辛 -> 巳
        丁 -> 寅
        壬 -> 卯

        戊 -> 戌
        癸 -> 亥
      }
    }


    /** 傍空 : 陰陽相反  */
    val fun傍空_A = { stem : Stem ->
      when (stem) {
        甲 -> 酉
        己 -> 申
        乙 -> 未
        庚 -> 午
        丙 -> 巳
        辛 -> 辰
        丁 -> 卯
        壬 -> 寅

        戊 -> 丑
        癸 -> 子
      }
    }

    val fun傍空_B = { stem : Stem ->
      when (stem) {
        甲 -> 酉
        己 -> 申
        乙 -> 未
        庚 -> 午
        丙 -> 巳
        辛 -> 辰
        丁 -> 卯
        壬 -> 寅

        戊 -> 亥
        癸 -> 戌
      }
    }

    /**
     * 紅艷星
     *
     * 參考設定： http://imgur.com/a/oXhRC
     * 甲、乙 採取不同設定
     */
    val fun紅艷_甲乙相同 = { stem : Stem ->
      when (stem) {
        甲 -> 午
        乙 -> 申
        丙 -> 寅
        丁 -> 未
        戊, 己 -> 辰
        庚 -> 戌
        辛 -> 酉
        壬 -> 子
        癸 -> 申
      }
    }

    /**
     * 紅艷星
     * 採取「李韶堯」先生「人生哲學。命理探討系列。紫微斗數」一書設定
     */
    val fun紅艷_甲乙相異 = { stem : Stem ->
      when (stem) {
        甲, 乙 -> 午
        丙 -> 寅
        丁 -> 未
        戊, 己 -> 辰
        庚 -> 戌
        辛 -> 酉
        壬 -> 子
        癸 -> 申
      }
    }
  }


}
