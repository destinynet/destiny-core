/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.*
import destiny.core.chinese.ziwei.StarDoctor.*
import destiny.core.chinese.ziwei.StarGeneralFront.*
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun亡神
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun劫煞
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun天煞
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun將星
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun息神
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun指背
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun攀鞍
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun月煞
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun歲馹
import destiny.core.chinese.ziwei.StarGeneralFront.Companion.fun災煞
import destiny.core.chinese.ziwei.StarLongevity.*
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun冠帶
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun墓
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun帝旺
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun死
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun沐浴
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun病
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun絕
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun胎
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun臨官
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun衰
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun長生
import destiny.core.chinese.ziwei.StarLongevity.Companion.fun養
import destiny.core.chinese.ziwei.StarLucky.Companion.fun右弼_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun天鉞
import destiny.core.chinese.ziwei.StarLucky.Companion.fun天魁
import destiny.core.chinese.ziwei.StarLucky.Companion.fun左輔_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun年馬_年支
import destiny.core.chinese.ziwei.StarLucky.Companion.fun文昌
import destiny.core.chinese.ziwei.StarLucky.Companion.fun文曲
import destiny.core.chinese.ziwei.StarLucky.Companion.fun月馬_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun祿存
import destiny.core.chinese.ziwei.StarMinor.*
import destiny.core.chinese.ziwei.StarMinor.Companion.fun三台_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun傍空_A
import destiny.core.chinese.ziwei.StarMinor.Companion.fun八座_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun台輔
import destiny.core.chinese.ziwei.StarMinor.Companion.fun咸池
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天使_fixed疾厄
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天使_陽順陰逆
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天傷_fixed交友
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天傷_陽順陰逆
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天刑_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天哭
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天喜
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天壽
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天姚_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天官
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天巫_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天廚
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天才
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天月_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天福
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天空
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天虛
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天貴
import destiny.core.chinese.ziwei.StarMinor.Companion.fun孤辰
import destiny.core.chinese.ziwei.StarMinor.Companion.fun寡宿
import destiny.core.chinese.ziwei.StarMinor.Companion.fun封誥
import destiny.core.chinese.ziwei.StarMinor.Companion.fun恩光
import destiny.core.chinese.ziwei.StarMinor.Companion.fun月德
import destiny.core.chinese.ziwei.StarMinor.Companion.fun正空_A
import destiny.core.chinese.ziwei.StarMinor.Companion.fun破碎
import destiny.core.chinese.ziwei.StarMinor.Companion.fun紅艷_甲乙相同
import destiny.core.chinese.ziwei.StarMinor.Companion.fun紅艷_甲乙相異
import destiny.core.chinese.ziwei.StarMinor.Companion.fun紅鸞
import destiny.core.chinese.ziwei.StarMinor.Companion.fun華蓋
import destiny.core.chinese.ziwei.StarMinor.Companion.fun蜚廉
import destiny.core.chinese.ziwei.StarMinor.Companion.fun解神_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun陰煞_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun陰空
import destiny.core.chinese.ziwei.StarMinor.Companion.fun陽空
import destiny.core.chinese.ziwei.StarMinor.Companion.fun鳳閣
import destiny.core.chinese.ziwei.StarMinor.Companion.fun龍池
import destiny.core.chinese.ziwei.StarMinor.天德
import destiny.core.chinese.ziwei.StarUnlucky.*
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun地劫
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun地空
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun擎羊
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun火星_全書
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun火星_全集
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun鈴星_全書
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun鈴星_全集
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun陀羅
import destiny.core.chinese.ziwei.StarYearFront.*
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun吊客
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun喪門
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun官符
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun晦氣
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun歲建
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun歲破
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun白虎
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun貫索
import destiny.core.chinese.ziwei.StarYearFront.Companion.fun龍德
import org.jooq.lambda.tuple.Tuple3
import org.jooq.lambda.tuple.Tuple5
import org.slf4j.LoggerFactory

object HouseFunctions {

  private val logger = LoggerFactory.getLogger(HouseFunctions::class.java)


  val house紫微: IHouse<*> = object : HouseMainStarImpl(StarMain.紫微) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {

      return StarMain.fun紫微.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house天機: IHouse<*> = object : HouseMainStarImpl(StarMain.天機) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun天機.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house太陽: IHouse<*> = object : HouseMainStarImpl(StarMain.太陽) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun太陽.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house武曲: IHouse<*> = object : HouseMainStarImpl(StarMain.武曲) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun武曲.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house天同: IHouse<*> = object : HouseMainStarImpl(StarMain.天同) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun天同.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house廉貞: IHouse<*> = object : HouseMainStarImpl(StarMain.廉貞) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun廉貞.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house天府: IHouse<*> = object : HouseMainStarImpl(StarMain.天府) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun天府.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }


  val house太陰: IHouse<*> = object : HouseMainStarImpl(StarMain.太陰) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun太陰.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house貪狼: IHouse<*> = object : HouseMainStarImpl(StarMain.貪狼) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun貪狼.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house巨門: IHouse<*> = object : HouseMainStarImpl(StarMain.巨門) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun巨門.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house天相: IHouse<*> = object : HouseMainStarImpl(StarMain.天相) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun天相.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house天梁: IHouse<*> = object : HouseMainStarImpl(StarMain.天梁) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun天梁.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house七殺: IHouse<*> = object : HouseMainStarImpl(StarMain.七殺) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun七殺.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }

  val house破軍: IHouse<*> = object : HouseMainStarImpl(StarMain.破軍) {
    override fun getBranch(t: Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>): Branch {
      return StarMain.fun破軍.invoke(t.v1(), t.v2(), t.v3(), t.v4(), t.v5())
    }
  }


  // =======↑↑↑======= 以上 14 顆主星 =======↑↑↑=======

  // =======↓↓↓======= 以下  8 顆吉星 =======↓↓↓=======

  val house文昌: IHouse<*> = object : HouseHourBranchImpl(StarLucky.文昌) {
    override fun getBranch(branch: Branch): Branch {
      return fun文昌.invoke(branch)
    }
  }

  val house文曲: IHouse<*> = object : HouseHourBranchImpl(StarLucky.文曲) {
    override fun getBranch(branch: Branch): Branch {
      return fun文曲.invoke(branch)
    }
  }

  val house左輔: IHouse<*> = object : HouseMonthImpl(StarLucky.左輔) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun左輔_月數.invoke(finalMonthNum)
    }
  }

  val house右弼: IHouse<*> = object : HouseMonthImpl(StarLucky.右弼) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun右弼_月數.invoke(finalMonthNum)
    }
  }

  val house天魁: IHouse<*> = object : HouseYearStemTianyiImpl(StarLucky.天魁) {
    override fun getBranch(tuple: Pair<Stem, TianyiIF>): Branch {
      return fun天魁.invoke(tuple.first, tuple.second)
    }
  }

  val house天鉞: IHouse<*> = object : HouseYearStemTianyiImpl(StarLucky.天鉞) {
    override fun getBranch(tuple: Pair<Stem, TianyiIF>): Branch {
      return fun天鉞.invoke(tuple.first, tuple.second)
    }
  }

  val house祿存: IHouse<*> = object : HouseYearStemImpl(StarLucky.祿存) {
    override fun getBranch(stem: Stem): Branch {
      return fun祿存.invoke(stem)
    }
  }

  val house年馬: IHouse<*> = object : HouseYearBranchImpl(StarLucky.年馬) {
    override fun getBranch(branch: Branch): Branch {
      return fun年馬_年支.invoke(branch)
    }
  }

  val house月馬: IHouse<*> = object : HouseMonthImpl(StarLucky.月馬) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun月馬_月數.invoke(finalMonthNum)
    }
  }

  // =======↑↑↑======= 以上  8 顆吉星 =======↑↑↑=======

  // =======↓↓↓======= 以下  6 顆兇星 =======↓↓↓=======

  val house擎羊: IHouse<*> = object : HouseYearStemImpl(擎羊) {
    override fun getBranch(stem: Stem): Branch {
      return fun擎羊.invoke(stem)
    }
  }

  val house陀羅: IHouse<*> = object : HouseYearStemImpl(陀羅) {
    override fun getBranch(stem: Stem): Branch {
      return fun陀羅.invoke(stem)
    }
  }

  val house火星: IHouse<*> = object : HouseYearBranchHourBranchImpl(火星) {
    override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
      val yearBranch = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
      return when (context.fireBell) {
        ZContext.FireBell.FIREBELL_BOOK -> fun火星_全書.invoke(yearBranch)
        ZContext.FireBell.FIREBELL_COLLECT -> fun火星_全集.invoke(yearBranch, hour)
      }
    }
  }

  val house鈴星: IHouse<*> = object : HouseYearBranchHourBranchImpl(鈴星) {
    override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
      val yearBranch = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
      return when (context.fireBell) {
        ZContext.FireBell.FIREBELL_BOOK -> fun鈴星_全書.invoke(yearBranch)
        ZContext.FireBell.FIREBELL_COLLECT -> fun鈴星_全集.invoke(yearBranch, hour)
      }
    }
  }

  val house地劫: IHouse<*> = object : HouseHourBranchImpl(地劫) {
    override fun getBranch(branch: Branch): Branch {
      return fun地劫.invoke(branch)
    }
  }

  val house地空: IHouse<*> = object : HouseHourBranchImpl(地空) {
    override fun getBranch(branch: Branch): Branch {
      return fun地空.invoke(branch)
    }
  }


  // =======↑↑↑======= 以上  6 顆兇星 =======↑↑↑=======


  // =======↓↓↓======= 以下      雜曜 =======↓↓↓=======
  val house天官: IHouse<*> = object : HouseYearStemImpl(天官) {
    override fun getBranch(stem: Stem): Branch {
      return fun天官.invoke(stem)
    }
  }

  val house天福: IHouse<*> = object : HouseYearStemImpl(天福) {
    override fun getBranch(stem: Stem): Branch {
      return fun天福.invoke(stem)
    }
  }

  val house天廚: IHouse<*> = object : HouseYearStemImpl(天廚) {
    override fun getBranch(stem: Stem): Branch {
      return fun天廚.invoke(stem)
    }
  }

  val house天刑: IHouse<*> = object : HouseMonthImpl(天刑) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun天刑_月數.invoke(finalMonthNum)
    }
  }

  val house天姚: IHouse<*> = object : HouseMonthImpl(天姚) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun天姚_月數.invoke(finalMonthNum)
    }
  }

  val house解神: IHouse<*> = object : HouseMonthImpl(解神) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun解神_月數.invoke(finalMonthNum)
    }
  }

  val house天巫: IHouse<*> = object : HouseMonthImpl(天巫) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun天巫_月數.invoke(finalMonthNum)
    }
  }

  val house天月: IHouse<*> = object : HouseMonthImpl(天月) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun天月_月數.invoke(finalMonthNum)
    }
  }

  val house陰煞: IHouse<*> = object : HouseMonthImpl(陰煞) {
    override fun getBranch(finalMonthNum: Int): Branch {
      return fun陰煞_月數.invoke(finalMonthNum)
    }
  }

  val house台輔: IHouse<*> = object : HouseHourBranchImpl(台輔) {
    override fun getBranch(branch: Branch): Branch {
      return fun台輔.invoke(branch)
    }
  }

  val house封誥: IHouse<*> = object : HouseHourBranchImpl(封誥) {
    override fun getBranch(branch: Branch): Branch {
      return fun封誥.invoke(branch)
    }
  }

  val house天空: IHouse<*> = object : HouseYearBranchImpl(天空) {
    override fun getBranch(branch: Branch): Branch {
      return fun天空.invoke(branch)
    }
  }

  val house天哭: IHouse<*> = object : HouseYearBranchImpl(天哭) {
    override fun getBranch(branch: Branch): Branch {
      return fun天哭.invoke(branch)
    }
  }

  val house天虛: IHouse<*> = object : HouseYearBranchImpl(天虛) {
    override fun getBranch(branch: Branch): Branch {
      return fun天虛.invoke(branch)
    }
  }

  val house龍池: IHouse<*> = object : HouseYearBranchImpl(龍池) {
    override fun getBranch(branch: Branch): Branch {
      return fun龍池.invoke(branch)
    }
  }

  val house鳳閣: IHouse<*> = object : HouseYearBranchImpl(鳳閣) {
    override fun getBranch(branch: Branch): Branch {
      return fun鳳閣.invoke(branch)
    }
  }

  val house紅鸞: IHouse<*> = object : HouseYearBranchImpl(紅鸞) {
    override fun getBranch(branch: Branch): Branch {
      return fun紅鸞.invoke(branch)
    }
  }

  val house天喜: IHouse<*> = object : HouseYearBranchImpl(天喜) {
    override fun getBranch(branch: Branch): Branch {
      return fun天喜.invoke(branch)
    }
  }

  val house孤辰: IHouse<*> = object : HouseYearBranchImpl(孤辰) {
    override fun getBranch(branch: Branch): Branch {
      return fun孤辰.invoke(branch)
    }
  }

  val house寡宿: IHouse<*> = object : HouseYearBranchImpl(寡宿) {
    override fun getBranch(branch: Branch): Branch {
      return fun寡宿.invoke(branch)
    }
  }

  val house蜚廉: IHouse<*> = object : HouseYearBranchImpl(蜚廉) {
    override fun getBranch(branch: Branch): Branch {
      return fun蜚廉.invoke(branch)
    }
  }

  val house破碎: IHouse<*> = object : HouseYearBranchImpl(破碎) {
    override fun getBranch(branch: Branch): Branch {
      return fun破碎.invoke(branch)
    }
  }

  val house華蓋: IHouse<*> = object : HouseYearBranchImpl(StarMinor.華蓋) {
    override fun getBranch(branch: Branch): Branch {
      return fun華蓋.invoke(branch)
    }
  }

  val house咸池: IHouse<*> = object : HouseYearBranchImpl(StarMinor.咸池) {
    override fun getBranch(branch: Branch): Branch {
      return fun咸池.invoke(branch)
    }
  }

  val house天德: IHouse<*> = object : HouseYearBranchImpl(天德) {
    override fun getBranch(branch: Branch): Branch {
      return StarMinor.fun天德.invoke(branch)
    }
  }

  val house月德: IHouse<*> = object : HouseYearBranchImpl(月德) {
    override fun getBranch(branch: Branch): Branch {
      return fun月德.invoke(branch)
    }
  }

  val house天才: IHouse<*> = object : HouseYearBranchMonthNumHourBranchMainHouseImpl(天才) {
    override fun getBranch(t: Tuple3<Branch, Int, Branch>): Branch {
      return fun天才.invoke(t.v1(), t.v2(), t.v3())
    }
  }

  val house天壽: IHouse<*> = object : HouseYearBranchMonthNumHourBranchImpl(天壽) {
    override fun getBranch(t: Triple<Branch, Int, Branch>): Branch {
      return fun天壽.invoke(t.first, t.second, t.third)
    }
  }

  val house三台: IHouse<*> = object : HouseMonthDayNumImpl(三台) {
    override fun getBranch(t: Pair<Int, Int>): Branch {
      return fun三台_月數.invoke(t.first, t.second)
    }
  }

  val house八座: IHouse<*> = object : HouseMonthDayNumImpl(八座) {
    override fun getBranch(t: Pair<Int, Int>): Branch {
      return fun八座_月數.invoke(t.first, t.second)
    }
  }

  val house恩光: IHouse<*> = object : HouseDayNumHourBranchImpl(恩光) {
    override fun getBranch(t: Pair<Int, Branch>): Branch {
      return fun恩光.invoke(t.first, t.second)
    }
  }

  val house天貴: IHouse<*> = object : HouseDayNumHourBranchImpl(天貴) {
    override fun getBranch(t: Pair<Int, Branch>): Branch {
      return fun天貴.invoke(t.first, t.second)
    }
  }


  val house天傷: IHouse<*> = object : HouseHouseDepYearStemGenderImpl(天傷) {
    override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
      // 太乙派，沒有遷移宮
      val houseSeqImpl = HouseSeqDefaultImpl()
      val steps = houseSeqImpl.getAheadOf(House.遷移, House.命宮)

      val 遷移宮地支 = predefinedMainHouse?.prev(steps)?:IZiwei.getHouseBranch(finalMonthNumForMonthStars, hour, House.遷移, houseSeqImpl)

      return when (context.hurtAngel) {
        ZContext.HurtAngel.HURT_ANGEL_FIXED -> fun天傷_fixed交友.invoke(遷移宮地支)
        ZContext.HurtAngel.HURT_ANGEL_YINYANG -> fun天傷_陽順陰逆.invoke(遷移宮地支, lunarYear.stem, gender)
      }
    }
  }

  val house天使: IHouse<*> = object : HouseHouseDepYearStemGenderImpl(天使) {
    override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
      // 太乙派，沒有遷移宮
      val houseSeqImpl = HouseSeqDefaultImpl()
      val steps = houseSeqImpl.getAheadOf(House.遷移, House.命宮)
      val 遷移宮地支 : Branch = predefinedMainHouse?.prev(steps) ?:IZiwei.getHouseBranch(finalMonthNumForMonthStars, hour, House.遷移, houseSeqImpl)

      return when (context.hurtAngel) {
        ZContext.HurtAngel.HURT_ANGEL_FIXED -> fun天使_fixed疾厄.invoke(遷移宮地支)
        ZContext.HurtAngel.HURT_ANGEL_YINYANG -> fun天使_陽順陰逆.invoke(遷移宮地支, lunarYear.stem, gender)
      }
    }
  }

  val house陽空: IHouse<*> = object : HouseYearImpl(陽空) {
    override fun getBranch(sb: StemBranch): Branch {
      return fun陽空.invoke(sb)
    }
  }

  val house陰空: IHouse<*> = object : HouseYearImpl(陰空) {
    override fun getBranch(sb: StemBranch): Branch {
      return fun陰空.invoke(sb)
    }
  }

  /** 截空 : 正空 (截路)  */
  val house正空: IHouse<*> = object : HouseYearStemImpl(正空) {
    override fun getBranch(stem: Stem): Branch {
      return fun正空_A.invoke(stem)
    }
  }

  /** 截空 : 傍空 (空亡)  */
  val house傍空: IHouse<*> = object : HouseYearStemImpl(傍空) {
    override fun getBranch(stem: Stem): Branch {
      return fun傍空_A.invoke(stem)
    }
  }

  /** 紅艷  */
  val house紅艷: IHouse<*> = object : HouseYearStemImpl(紅艷) {
    override fun getBranch(stem: Stem): Branch {
      throw RuntimeException("Error")
    }

    override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
      return when (context.redBeauty) {
        ZContext.RedBeauty.RED_BEAUTY_SAME -> fun紅艷_甲乙相同.invoke(if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.stem else solarYear.stem)
        ZContext.RedBeauty.RED_BEAUTY_DIFF -> fun紅艷_甲乙相異.invoke(if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.stem else solarYear.stem)
      }
    }
  }

  // =======↑↑↑======= 以上      雜曜 =======↑↑↑=======

  // =======↓↓↓======= 以下 博士12神煞 =======↓↓↓=======

  val house博士: IHouse<*> = HouseDoctorImpl(博士)

  val house力士: IHouse<*> = HouseDoctorImpl(力士)

  val house青龍: IHouse<*> = HouseDoctorImpl(青龍)

  val house小耗: IHouse<*> = HouseDoctorImpl(StarDoctor.小耗)

  val house將軍: IHouse<*> = HouseDoctorImpl(將軍)

  val house奏書: IHouse<*> = HouseDoctorImpl(奏書)

  val house飛廉: IHouse<*> = HouseDoctorImpl(飛廉)

  val house喜神: IHouse<*> = HouseDoctorImpl(喜神)

  val house病符: IHouse<*> = HouseDoctorImpl(StarDoctor.病符)

  val house大耗: IHouse<*> = HouseDoctorImpl(大耗)

  val house伏兵: IHouse<*> = HouseDoctorImpl(伏兵)

  val house官府: IHouse<*> = HouseDoctorImpl(官府)

  // =======↑↑↑======= 以上 博士12神煞 =======↑↑↑=======

  // =======↓↓↓======= 以下 長生12神煞 =======↓↓↓=======
  val house長生: IHouse<*> = object : HouseFiveGenderYinYangImpl(長生) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun長生.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house沐浴: IHouse<*> = object : HouseFiveGenderYinYangImpl(沐浴) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun沐浴.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house冠帶: IHouse<*> = object : HouseFiveGenderYinYangImpl(冠帶) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun冠帶.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house臨官: IHouse<*> = object : HouseFiveGenderYinYangImpl(臨官) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun臨官.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house帝旺: IHouse<*> = object : HouseFiveGenderYinYangImpl(帝旺) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun帝旺.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house衰: IHouse<*> = object : HouseFiveGenderYinYangImpl(衰) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun衰.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house病: IHouse<*> = object : HouseFiveGenderYinYangImpl(病) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun病.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house死: IHouse<*> = object : HouseFiveGenderYinYangImpl(死) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun死.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house墓: IHouse<*> = object : HouseFiveGenderYinYangImpl(墓) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun墓.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house絕: IHouse<*> = object : HouseFiveGenderYinYangImpl(絕) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun絕.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house胎: IHouse<*> = object : HouseFiveGenderYinYangImpl(胎) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun胎.invoke(t3.first, t3.second, t3.third)
    }
  }

  val house養: IHouse<*> = object : HouseFiveGenderYinYangImpl(養) {
    override fun getBranch(t3: Triple<FiveElement, Gender, YinYangIF>): Branch {
      return fun養.invoke(t3.first, t3.second, t3.third)
    }
  }
  // =======↑↑↑======= 以上 長生12神煞 =======↑↑↑=======


  // =======↓↓↓======= 以下 將前12星 =======↓↓↓=======
  val house將前_將星: IHouse<*> = object : HouseYearBranchImpl(將星) {
    override fun getBranch(branch: Branch): Branch {
      return fun將星.invoke(branch)
    }
  }
  val house將前_攀鞍: IHouse<*> = object : HouseYearBranchImpl(攀鞍) {
    override fun getBranch(branch: Branch): Branch {
      return fun攀鞍.invoke(branch)
    }
  }
  val house將前_歲馹: IHouse<*> = object : HouseYearBranchImpl(歲馹) {
    override fun getBranch(branch: Branch): Branch {
      return fun歲馹.invoke(branch)
    }
  }
  val house將前_息神: IHouse<*> = object : HouseYearBranchImpl(息神) {
    override fun getBranch(branch: Branch): Branch {
      return fun息神.invoke(branch)
    }
  }
  val house將前_華蓋: IHouse<*> = object : HouseYearBranchImpl(StarGeneralFront.華蓋) {
    override fun getBranch(branch: Branch): Branch {
      return StarGeneralFront.fun華蓋.invoke(branch)
    }
  }
  val house將前_劫煞: IHouse<*> = object : HouseYearBranchImpl(劫煞) {
    override fun getBranch(branch: Branch): Branch {
      return fun劫煞.invoke(branch)
    }
  }
  val house將前_災煞: IHouse<*> = object : HouseYearBranchImpl(災煞) {
    override fun getBranch(branch: Branch): Branch {
      return fun災煞.invoke(branch)
    }
  }
  val house將前_天煞: IHouse<*> = object : HouseYearBranchImpl(天煞) {
    override fun getBranch(branch: Branch): Branch {
      return fun天煞.invoke(branch)
    }
  }
  val house將前_指背: IHouse<*> = object : HouseYearBranchImpl(指背) {
    override fun getBranch(branch: Branch): Branch {
      return fun指背.invoke(branch)
    }
  }
  val house將前_咸池: IHouse<*> = object : HouseYearBranchImpl(StarGeneralFront.咸池) {
    override fun getBranch(branch: Branch): Branch {
      return StarGeneralFront.fun咸池.invoke(branch)
    }
  }
  val house將前_月煞: IHouse<*> = object : HouseYearBranchImpl(月煞) {
    override fun getBranch(branch: Branch): Branch {
      return fun月煞.invoke(branch)
    }
  }
  val house將前_亡神: IHouse<*> = object : HouseYearBranchImpl(亡神) {
    override fun getBranch(branch: Branch): Branch {
      return fun亡神.invoke(branch)
    }
  }

  // =======↑↑↑======= 以上 將前12星 =======↑↑↑=======


  // =======↓↓↓======= 以下 歲前12星 =======↓↓↓=======
  val house歲前_歲建: IHouse<*> = object : HouseYearBranchImpl(歲建) {
    override fun getBranch(branch: Branch): Branch {
      return fun歲建.invoke(branch)
    }
  }

  val house歲前_晦氣: IHouse<*> = object : HouseYearBranchImpl(晦氣) {
    override fun getBranch(branch: Branch): Branch {
      return fun晦氣.invoke(branch)
    }
  }

  val house歲前_喪門: IHouse<*> = object : HouseYearBranchImpl(喪門) {
    override fun getBranch(branch: Branch): Branch {
      return fun喪門.invoke(branch)
    }
  }

  val house歲前_貫索: IHouse<*> = object : HouseYearBranchImpl(貫索) {
    override fun getBranch(branch: Branch): Branch {
      return fun貫索.invoke(branch)
    }
  }

  val house歲前_官符: IHouse<*> = object : HouseYearBranchImpl(官符) {
    override fun getBranch(branch: Branch): Branch {
      return fun官符.invoke(branch)
    }
  }

  val house歲前_小耗: IHouse<*> = object : HouseYearBranchImpl(StarYearFront.小耗) {
    override fun getBranch(branch: Branch): Branch {
      return StarYearFront.fun小耗.invoke(branch)
    }
  }

  val house歲前_歲破: IHouse<*> = object : HouseYearBranchImpl(歲破) {
    override fun getBranch(branch: Branch): Branch {
      return fun歲破.invoke(branch)
    }
  }

  val house歲前_龍德: IHouse<*> = object : HouseYearBranchImpl(龍德) {
    override fun getBranch(branch: Branch): Branch {
      return fun龍德.invoke(branch)
    }
  }

  val house歲前_白虎: IHouse<*> = object : HouseYearBranchImpl(白虎) {
    override fun getBranch(branch: Branch): Branch {
      return fun白虎.invoke(branch)
    }
  }

  val house歲前_天德: IHouse<*> = object : HouseYearBranchImpl(StarYearFront.天德) {
    override fun getBranch(branch: Branch): Branch {
      return StarYearFront.fun天德.invoke(branch)
    }
  }

  val house歲前_吊客: IHouse<*> = object : HouseYearBranchImpl(吊客) {
    override fun getBranch(branch: Branch): Branch {
      return fun吊客.invoke(branch)
    }
  }

  val house歲前_病符: IHouse<*> = object : HouseYearBranchImpl(StarYearFront.病符) {
    override fun getBranch(branch: Branch): Branch {
      return StarYearFront.fun病符.invoke(branch)
    }
  }

  // =======↑↑↑======= 以上 歲前12星 =======↑↑↑=======


  val iHouseSet: Set<IHouse<*>> = setOf(
    // 14主星
    house紫微, house天機, house太陽, house武曲, house天同, house廉貞, house天府, house太陰, house貪狼, house巨門, house天相, house天梁, house七殺, house破軍

    /** 八(+1)吉星 ( [house年馬] 與 [house月馬] 其實就是 [StarLucky.年馬] 及 [StarLucky.月馬] )   */
    ,house文昌, house文曲, house左輔, house右弼, house天魁, house天鉞, house祿存, house年馬, house月馬

    // 六兇星
    ,house擎羊, house陀羅, house火星, house鈴星, house地劫, house地空

    // 雜曜
    ,house天官, house天福, house天廚, house天刑, house天姚, house解神, house天巫, house天月
    ,house陰煞, house台輔, house封誥, house天空, house天哭, house天虛, house龍池, house鳳閣
    ,house紅鸞, house天喜, house孤辰, house寡宿, house蜚廉, house破碎, house華蓋, house咸池
    ,house天德, house月德, house天才, house天壽, house三台, house八座, house恩光, house天貴
    ,house天傷, house天使
    ,house陽空, house陰空
    ,house正空, house傍空
    ,house紅艷

    // 博士12神煞
    ,house博士, house力士, house青龍, house小耗, house將軍, house奏書, house飛廉, house喜神, house病符, house大耗, house伏兵, house官府

    // 長生12神煞
    ,house長生, house沐浴, house冠帶, house臨官, house帝旺, house衰, house病, house死, house墓, house絕, house胎, house養

    // 將前12星
    ,house將前_將星, house將前_攀鞍, house將前_歲馹, house將前_息神, house將前_華蓋, house將前_劫煞, house將前_災煞, house將前_天煞, house將前_指背, house將前_咸池, house將前_月煞, house將前_亡神

    // 歲前12星
    ,house歲前_歲建, house歲前_晦氣, house歲前_喪門, house歲前_貫索, house歲前_官符, house歲前_小耗, house歲前_歲破, house歲前_龍德, house歲前_白虎, house歲前_天德, house歲前_吊客, house歲前_病符
  )

  val map : Map<ZStar , IHouse<*>> = iHouseSet.map {
    Pair(it.star,it)
  }.toMap()

//  val map: Map<ZStar, IHouse<*>> = iHouseSet.stream()
//    .collect<Map<ZStar, IHouse<*>>, Any>(Collectors.toMap<IHouse, ZStar, IHouse>(Function<IHouse, ZStar> { it.getStar() }) { iHouse -> iHouse })

}
