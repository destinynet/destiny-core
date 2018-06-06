/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarLucky.*
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.StarUnlucky.火星
import destiny.core.chinese.ziwei.StarUnlucky.鈴星
import org.slf4j.LoggerFactory


/** 紫微斗數  */
interface IZiwei {


//  /**
//   * 計算本命盤
//   * @param mainAndBody 預先計算過的命宮、身宮地支
//   * @param preFinalMonthNumForMainStars 預先計算好的「最終月數」 for 14顆主星 ([StarMain]])
//   * @param lunarYear          陰曆的年干支
//   * @param solarYear          「節氣」的年干支
//   * @param lunarMonth         陰曆的月份
//   * @param monthBranch        「節氣」的月支
//   * @param optionalVageMap    預先計算好的虛歲時刻(GMT from / to)
//   */
//  fun getBirthPlate(mainAndBody: Pair<Branch, Branch>?,
//                    preFinalMonthNumForMainStars: Int?,
//                    cycle: Int,
//                    lunarYear: StemBranch,
//                    solarYear: StemBranch,
//                    lunarMonth: Int,
//                    leapMonth: Boolean,
//                    monthBranch: Branch,
//                    solarTerms: SolarTerms,
//                    lunarDays: Int,
//                    hour: Branch,
//                    stars: Collection<ZStar>,
//                    gender: Gender,
//                    optionalVageMap: Map<Int, Pair<Double, Double>>?,
//                    context: IZiweiContext): Builder

//  /** 輸入現代化的資料，計算本命盤  */
//  @Deprecated("")
//  fun getModernPlate(lmt: ChronoLocalDateTime<*>,
//                     location: ILocation,
//                     place: String?,
//                     gender: Gender,
//                     stars: Collection<ZStar>,
//                     context: IZiweiModernContext,
//                     viewSettings: ViewSettings,
//                     solarTermsImpl: ISolarTerms,
//                     yearMonthImpl: IYearMonth,
//                     dayImpl: IDay): Builder

  /** 計算 大限盤  */
  fun getFlowBig(builder: Builder, context: IZiweiContext, flowBig: StemBranch): Builder


  /** 計算 流年盤  */
  fun getFlowYear(builder: Builder, context: IZiweiContext, flowBig: StemBranch, flowYear: StemBranch): Builder

  /** 計算 流月盤 TODO : 流月必須要考慮 「是否閏月」 , 可能要拆成 Integer + Boolean  */
  fun getFlowMonth(builder: Builder, context: IZiweiContext, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch): Builder

  /** 計算 流日盤  */
  fun getFlowDay(builder: Builder, context: IZiweiContext,
                 flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, flowDay: StemBranch, flowDayNum: Int): Builder

  /** 計算 流時盤  */
  fun getFlowHour(builder: Builder, context: IZiweiContext,
                  flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, flowDay: StemBranch, flowDayNum: Int, flowHour: StemBranch): Builder

  companion object {

    val logger = LoggerFactory.getLogger(IZiwei::class.java)

    /**
     * 命宮 : (月數 , 時支) -> 地支
     * 寅宮開始，順數月數，再逆數時支
     *
     * 假設我的出生月日是農曆 7月6日 巳時，順數生月，那就是從寅宮開始順時針走七步 , 因為是農曆七月，所以經由 順數生月，所以我們找到了申宮
     *
     * 找到申宮之後再 逆數生時 找到了卯宮，所以卯就是你的命宮
     */
    fun getMainHouseBranch(finalMonthNum: Int, hour: Branch): Branch {
      return 寅.next(finalMonthNum - 1).prev(hour.index)
    }

    /**
     * 承上 , 以五虎遁，定寅宮的天干，然後找到命宮天干
     *
     * 甲年 or 己年生 = 丙寅宮
     * 乙年 or 庚年生 = 戊寅宮
     * 丙年 or 辛年生 = 庚寅宮
     * 丁年 or 壬年生 = 壬寅宮
     * 戊年 or 癸年生 = 甲寅宮
     */
    fun getMainHouse(year: Stem, finalMonthNum: Int, hour: Branch): StemBranch {
      // 寅 的天干
      val stemOf寅 = getStemOf寅(year)

      val branch = getMainHouseBranch(finalMonthNum, hour)
      return getStemBranchOf(branch, stemOf寅)
    } // 取得命宮

    /** 承上 , 找到命宮的 干支 ，可以取得「五行、第幾局」  */
    fun getMainDesc(mainHouse: StemBranch): Pair<FiveElement, Int> {
      // 五行
      val fiveElement = NaYin.getFiveElement(mainHouse)
      // 第幾局
      val state: Int
      state = when (fiveElement) {
        水 -> 2
        土 -> 5
        木 -> 3
        火 -> 6
        金 -> 4
      }
      return Pair(fiveElement, state)
    }


    /**
     * 身宮 (月數 , 時支) -> 地支
     * 順數生月，順數生時 就可以找到身宮
     */
    fun getBodyHouseBranch(finalMonthNum: Int, hour: Branch): Branch {
      return 寅.next(finalMonthNum - 1).next(hour.index)
    }

    /** 承上， 身宮 的干支  */
    fun getBodyHouse(year: Stem, finalMonthNum: Int, hour: Branch): StemBranch {
      // 寅 的天干
      val stemOf寅 = getStemOf寅(year)

      val branch = getBodyHouseBranch(finalMonthNum, hour)
      return getStemBranchOf(branch, stemOf寅)
    }

    /**
     * 從命宮開始，逆時針，飛佈 兄弟、夫妻...
     */
    fun getHouseBranch(month: Int, hour: Branch, house: House, seq: IHouseSeq): Branch {
      // 命宮 的地支
      val branchOfFirstHouse = getMainHouseBranch(month, hour)
      val steps = seq.getAheadOf(house, House.命宮)
      return branchOfFirstHouse.prev(steps)
    }

    /**
     * 承上 , 取得該宮位的「天干」＋「地支」組合
     */
    @Deprecated("")
    fun getHouse(year: Stem, month: Int, hour: Branch, house: House, seq: IHouseSeq): StemBranch {
      // 寅 的天干
      val stemOf寅 = getStemOf寅(year)
      // 先取得 該宮位的地支
      val branch = getHouseBranch(month, hour, house, seq)
      return getStemBranchOf(branch, stemOf寅)
    }


    fun getStemBranchOf(branch: Branch, stemOf寅: Stem): StemBranch {
      // 左下角，寅宮 的 干支
      val stemBranchOf寅 = StemBranch.get(stemOf寅, 寅)
      val steps = branch.getAheadOf(寅)
      return stemBranchOf寅.next(steps)
    }

    /**
     * 左下角，寅宮 的天干
     */
    fun getStemOf寅(year: Stem): Stem {
      return when (year) {
        甲, 己 -> 丙
        乙, 庚 -> 戊
        丙, 辛 -> 庚
        丁, 壬 -> 壬
        戊, 癸 -> 甲
      }
    }

    /** 流年斗君
     * flowYear -> 流年 , anchor -> 錨 , 意為： 以此為當年度之「定錨」（亦為一月), 推算流月、甚至流日、流時
     */
    fun getFlowYearAnchor(flowYear: Branch, birthMonth: Int, birthHour: Branch): Branch {
      return flowYear                     // 以流年地支為起點
        .prev(birthMonth - 1)             // 從1 逆數至「出生月」
        .next(birthHour.getAheadOf(子))   // 再順數至「出生時」
    }

    /** 命主 : 命宮所在地支安星  */
    fun getMainStar(branch: Branch): ZStar {
      return when (branch) {
        子 -> 貪狼
        丑, 亥 -> 巨門
        寅, 戌 -> 祿存
        卯, 酉 -> 文曲
        辰, 申 -> 廉貞
        巳, 未 -> 武曲
        午 -> 破軍
      }
    }


    /** 身主 : 以出生年之地支安星  */
    fun getBodyStar(branch: Branch): ZStar {
      return when (branch) {
        子 -> 火星
        丑, 未 -> 天相
        寅, 申 -> 天梁
        卯, 酉 -> 天同
        辰, 戌 -> 文昌
        巳, 亥 -> 天機
        午 -> 鈴星
      }
    }
  }
}
