/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.ITianyi
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.chinese.ziwei.StarDoctor.*
import destiny.core.chinese.ziwei.StarGeneralFront.*
import destiny.core.chinese.ziwei.StarLongevity.*
import destiny.core.chinese.ziwei.StarLucky.Companion.fun右弼_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun天鉞
import destiny.core.chinese.ziwei.StarLucky.Companion.fun天魁
import destiny.core.chinese.ziwei.StarLucky.Companion.fun左輔_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun年馬
import destiny.core.chinese.ziwei.StarLucky.Companion.fun文昌
import destiny.core.chinese.ziwei.StarLucky.Companion.fun文曲
import destiny.core.chinese.ziwei.StarLucky.Companion.fun月馬_月數
import destiny.core.chinese.ziwei.StarLucky.Companion.fun祿存
import destiny.core.chinese.ziwei.StarMain.*
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
import mu.KotlinLogging
import org.slf4j.LoggerFactory

object HouseFunctions {

  private val logger = KotlinLogging.logger {  }


  private val house紫微: IHouse<*> = HouseMainStarImpl(紫微)

  private val house天機: IHouse<*> = HouseMainStarImpl(天機)

  private val house太陽: IHouse<*> = HouseMainStarImpl(太陽)

  private val house武曲: IHouse<*> = HouseMainStarImpl(武曲)

  private val house天同: IHouse<*> = HouseMainStarImpl(天同)

  private val house廉貞: IHouse<*> = HouseMainStarImpl(廉貞)

  private val house天府: IHouse<*> = HouseMainStarImpl(天府)

  private val house太陰: IHouse<*> = HouseMainStarImpl(太陰)

  private val house貪狼: IHouse<*> = HouseMainStarImpl(貪狼)

  private val house巨門: IHouse<*> = HouseMainStarImpl(巨門)

  private val house天相: IHouse<*> = HouseMainStarImpl(天相)

  private val house天梁: IHouse<*> = HouseMainStarImpl(天梁)

  private val house七殺: IHouse<*> = HouseMainStarImpl(七殺)

  private val house破軍: IHouse<*> = HouseMainStarImpl(破軍)


  // =======↑↑↑======= 以上 14 顆主星 =======↑↑↑=======

  // =======↓↓↓======= 以下  8 顆吉星 =======↓↓↓=======

  private val house文昌: IHouse<*> = object : HouseHourBranchImpl(StarLucky.文昌) {
    override fun getBranch(t: Branch): Branch {
      return fun文昌.invoke(t)
    }
  }

  private val house文曲: IHouse<*> = object : HouseHourBranchImpl(StarLucky.文曲) {
    override fun getBranch(t: Branch): Branch {
      return fun文曲.invoke(t)
    }
  }

  private val house左輔: IHouse<*> = object : HouseMonthImpl(StarLucky.左輔) {
    override fun getBranch(t: Int): Branch {
      return fun左輔_月數.invoke(t)
    }
  }

  private val house右弼: IHouse<*> = object : HouseMonthImpl(StarLucky.右弼) {
    override fun getBranch(t: Int): Branch {
      return fun右弼_月數.invoke(t)
    }
  }

  private val house天魁: IHouse<*> = object : HouseYearStemTianyiImpl(StarLucky.天魁) {
    override fun getBranch(t: Pair<Stem, ITianyi>): Branch {
      return fun天魁.invoke(t.first, t.second)
    }
  }

  private val house天鉞: IHouse<*> = object : HouseYearStemTianyiImpl(StarLucky.天鉞) {
    override fun getBranch(t: Pair<Stem, ITianyi>): Branch {
      return fun天鉞.invoke(t.first, t.second)
    }
  }

  private val house祿存: IHouse<*> = object : HouseYearStemImpl(StarLucky.祿存) {
    override fun getBranch(t: Stem): Branch {
      return fun祿存.invoke(t)
    }
  }

  val house年馬: IHouse<*> = object : HouseYearBranchImpl(StarLucky.年馬) {
    override fun getBranch(t: Branch): Branch {
      return fun年馬.invoke(t)
    }
  }

  val house月馬: IHouse<*> = object : HouseMonthImpl(StarLucky.月馬) {
    override fun getBranch(t: Int): Branch {
      return fun月馬_月數.invoke(t)
    }
  }

  private val house天馬: IHouse<*> = object : HouseYearMonthImpl(StarLucky.天馬) {
    override fun getBranch(lunarYear: StemBranch,
                           solarYear: StemBranch,
                           monthBranch: Branch,
                           finalMonthNumForMonthStars: Int,
                           solarTerms: SolarTerms,
                           days: Int,
                           hour: Branch,
                           state: Int,
                           gender: Gender,
                           leap: Boolean,
                           prevMonthDays: Int,
                           predefinedMainHouse: Branch?,
                           context: IZiweiContext): Branch {

      return when(context.skyHorse) {
        SkyHorse.YEAR -> fun年馬.invoke(lunarYear.branch)
        SkyHorse.MONTH -> fun月馬_月數.invoke(finalMonthNumForMonthStars)
      }
    }
  }

  // =======↑↑↑======= 以上  8 顆吉星 =======↑↑↑=======

  // =======↓↓↓======= 以下  6 顆兇星 =======↓↓↓=======

  private val house擎羊: IHouse<*> = object : HouseYearStemImpl(擎羊) {
    override fun getBranch(t: Stem): Branch {
      return fun擎羊.invoke(t)
    }
  }

  private val house陀羅: IHouse<*> = object : HouseYearStemImpl(陀羅) {
    override fun getBranch(t: Stem): Branch {
      return fun陀羅.invoke(t)
    }
  }

  val house火星: IHouse<*> = object : HouseYearBranchHourBranchImpl(火星) {
    override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: IZiweiContext): Branch {
      val yearBranch = if (context.yearType == YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
      return when (context.fireBell) {
        FireBell.FIREBELL_BOOK -> fun火星_全書.invoke(yearBranch)
        FireBell.FIREBELL_COLLECT -> fun火星_全集.invoke(yearBranch, hour)
      }
    }
  }

  val house鈴星: IHouse<*> = object : HouseYearBranchHourBranchImpl(鈴星) {
    override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: IZiweiContext): Branch {
      val yearBranch = if (context.yearType == YearType.YEAR_LUNAR) lunarYear.branch else solarYear.branch
      return when (context.fireBell) {
        FireBell.FIREBELL_BOOK -> fun鈴星_全書.invoke(yearBranch)
        FireBell.FIREBELL_COLLECT -> fun鈴星_全集.invoke(yearBranch, hour)
      }
    }
  }

  private val house地劫: IHouse<*> = object : HouseHourBranchImpl(地劫) {
    override fun getBranch(t: Branch): Branch {
      return fun地劫.invoke(t)
    }
  }

  private val house地空: IHouse<*> = object : HouseHourBranchImpl(地空) {
    override fun getBranch(t: Branch): Branch {
      return fun地空.invoke(t)
    }
  }


  // =======↑↑↑======= 以上  6 顆兇星 =======↑↑↑=======


  // =======↓↓↓======= 以下      雜曜 =======↓↓↓=======
  private val house天官: IHouse<*> = object : HouseYearStemImpl(天官) {
    override fun getBranch(t: Stem): Branch {
      return fun天官.invoke(t)
    }
  }

  private val house天福: IHouse<*> = object : HouseYearStemImpl(天福) {
    override fun getBranch(t: Stem): Branch {
      return fun天福.invoke(t)
    }
  }

  private val house天廚: IHouse<*> = object : HouseYearStemImpl(天廚) {
    override fun getBranch(t: Stem): Branch {
      return fun天廚.invoke(t)
    }
  }

  private val house天刑: IHouse<*> = object : HouseMonthImpl(天刑) {
    override fun getBranch(t: Int): Branch {
      return fun天刑_月數.invoke(t)
    }
  }

  private val house天姚: IHouse<*> = object : HouseMonthImpl(天姚) {
    override fun getBranch(t: Int): Branch {
      return fun天姚_月數.invoke(t)
    }
  }

  private val house解神: IHouse<*> = object : HouseMonthImpl(解神) {
    override fun getBranch(t: Int): Branch {
      return fun解神_月數.invoke(t)
    }
  }

  private val house天巫: IHouse<*> = object : HouseMonthImpl(天巫) {
    override fun getBranch(t: Int): Branch {
      return fun天巫_月數.invoke(t)
    }
  }

  private val house天月: IHouse<*> = object : HouseMonthImpl(天月) {
    override fun getBranch(t: Int): Branch {
      return fun天月_月數.invoke(t)
    }
  }

  private val house陰煞: IHouse<*> = object : HouseMonthImpl(陰煞) {
    override fun getBranch(t: Int): Branch {
      return fun陰煞_月數.invoke(t)
    }
  }

  private val house台輔: IHouse<*> = object : HouseHourBranchImpl(台輔) {
    override fun getBranch(t: Branch): Branch {
      return fun台輔.invoke(t)
    }
  }

  private val house封誥: IHouse<*> = object : HouseHourBranchImpl(封誥) {
    override fun getBranch(t: Branch): Branch {
      return fun封誥.invoke(t)
    }
  }

  private val house天空: IHouse<*> = object : HouseYearBranchImpl(天空) {
    override fun getBranch(t: Branch): Branch {
      return fun天空.invoke(t)
    }
  }

  private val house天哭: IHouse<*> = object : HouseYearBranchImpl(天哭) {
    override fun getBranch(t: Branch): Branch {
      return fun天哭.invoke(t)
    }
  }

  private val house天虛: IHouse<*> = object : HouseYearBranchImpl(天虛) {
    override fun getBranch(t: Branch): Branch {
      return fun天虛.invoke(t)
    }
  }

  private val house龍池: IHouse<*> = object : HouseYearBranchImpl(龍池) {
    override fun getBranch(t: Branch): Branch {
      return fun龍池.invoke(t)
    }
  }

  private val house鳳閣: IHouse<*> = object : HouseYearBranchImpl(鳳閣) {
    override fun getBranch(t: Branch): Branch {
      return fun鳳閣.invoke(t)
    }
  }

  private val house紅鸞: IHouse<*> = object : HouseYearBranchImpl(紅鸞) {
    override fun getBranch(t: Branch): Branch {
      return fun紅鸞.invoke(t)
    }
  }

  private val house天喜: IHouse<*> = object : HouseYearBranchImpl(天喜) {
    override fun getBranch(t: Branch): Branch {
      return fun天喜.invoke(t)
    }
  }

  private val house孤辰: IHouse<*> = object : HouseYearBranchImpl(孤辰) {
    override fun getBranch(t: Branch): Branch {
      return fun孤辰.invoke(t)
    }
  }

  private val house寡宿: IHouse<*> = object : HouseYearBranchImpl(寡宿) {
    override fun getBranch(t: Branch): Branch {
      return fun寡宿.invoke(t)
    }
  }

  private val house蜚廉: IHouse<*> = object : HouseYearBranchImpl(蜚廉) {
    override fun getBranch(t: Branch): Branch {
      return fun蜚廉.invoke(t)
    }
  }

  private val house破碎: IHouse<*> = object : HouseYearBranchImpl(破碎) {
    override fun getBranch(t: Branch): Branch {
      return fun破碎.invoke(t)
    }
  }

  private val house華蓋: IHouse<*> = object : HouseYearBranchImpl(StarMinor.華蓋) {
    override fun getBranch(t: Branch): Branch {
      return fun華蓋.invoke(t)
    }
  }

  private val house咸池: IHouse<*> = object : HouseYearBranchImpl(StarMinor.咸池) {
    override fun getBranch(t: Branch): Branch {
      return fun咸池.invoke(t)
    }
  }

  private val house天德: IHouse<*> = object : HouseYearBranchImpl(天德) {
    override fun getBranch(t: Branch): Branch {
      return StarMinor.fun天德.invoke(t)
    }
  }

  private val house月德: IHouse<*> = object : HouseYearBranchImpl(月德) {
    override fun getBranch(t: Branch): Branch {
      return fun月德.invoke(t)
    }
  }

  private val house天才: IHouse<*> = object : HouseYearBranchMonthNumHourBranchMainHouseImpl(天才) {
    override fun getBranch(t: Triple<Branch, Int, Branch>): Branch {
      return fun天才.invoke(t.first, t.second, t.third)
    }
  }

  private val house天壽: IHouse<*> = object : HouseYearBranchMonthNumHourBranchImpl(天壽) {
    override fun getBranch(t: Triple<Branch, Int, Branch>): Branch {
      return fun天壽.invoke(t.first, t.second, t.third)
    }
  }

  private val house三台: IHouse<*> = object : HouseMonthDayNumImpl(三台) {
    override fun getBranch(t: Pair<Int, Int>): Branch {
      return fun三台_月數.invoke(t.first, t.second)
    }
  }

  private val house八座: IHouse<*> = object : HouseMonthDayNumImpl(八座) {
    override fun getBranch(t: Pair<Int, Int>): Branch {
      return fun八座_月數.invoke(t.first, t.second)
    }
  }

  private val house恩光: IHouse<*> = object : HouseDayNumHourBranchImpl(恩光) {
    override fun getBranch(t: Pair<Int, Branch>): Branch {
      return fun恩光.invoke(t.first, t.second)
    }
  }

  private val house天貴: IHouse<*> = object : HouseDayNumHourBranchImpl(天貴) {
    override fun getBranch(t: Pair<Int, Branch>): Branch {
      return fun天貴.invoke(t.first, t.second)
    }
  }


  private val house天傷: IHouse<*> = object : HouseHouseDepYearStemGenderImpl(天傷) {
    override fun getBranch(lunarYear: StemBranch,
                           solarYear: StemBranch,
                           monthBranch: Branch,
                           finalMonthNumForMonthStars: Int,
                           solarTerms: SolarTerms,
                           days: Int,
                           hour: Branch,
                           state: Int,
                           gender: Gender,
                           leap: Boolean,
                           prevMonthDays: Int,
                           predefinedMainHouse: Branch?,
                           context: IZiweiContext): Branch {
      // 太乙派，沒有遷移宮
      val houseSeqImpl = HouseSeqDefaultImpl()
      val steps = houseSeqImpl.getAheadOf(House.遷移, House.命宮)

      val 遷移宮地支 = predefinedMainHouse?.prev(steps)?:Ziwei.getHouseBranch(finalMonthNumForMonthStars, hour, House.遷移, houseSeqImpl)

      return when (context.hurtAngel) {
        HurtAngel.HURT_ANGEL_FIXED -> fun天傷_fixed交友.invoke(遷移宮地支)
        HurtAngel.HURT_ANGEL_YINYANG -> fun天傷_陽順陰逆.invoke(遷移宮地支, lunarYear.stem, gender)
      }
    }
  }

  private val house天使: IHouse<*> = object : HouseHouseDepYearStemGenderImpl(天使) {
    override fun getBranch(lunarYear: StemBranch,
                           solarYear: StemBranch,
                           monthBranch: Branch,
                           finalMonthNumForMonthStars: Int,
                           solarTerms: SolarTerms,
                           days: Int,
                           hour: Branch,
                           state: Int,
                           gender: Gender,
                           leap: Boolean,
                           prevMonthDays: Int,
                           predefinedMainHouse: Branch?,
                           context: IZiweiContext): Branch {
      // 太乙派，沒有遷移宮
      val houseSeqImpl = HouseSeqDefaultImpl()
      val steps = houseSeqImpl.getAheadOf(House.遷移, House.命宮)
      val 遷移宮地支 : Branch = predefinedMainHouse?.prev(steps) ?:Ziwei.getHouseBranch(finalMonthNumForMonthStars, hour, House.遷移, houseSeqImpl)

      return when (context.hurtAngel) {
        HurtAngel.HURT_ANGEL_FIXED -> fun天使_fixed疾厄.invoke(遷移宮地支)
        HurtAngel.HURT_ANGEL_YINYANG -> fun天使_陽順陰逆.invoke(遷移宮地支, lunarYear.stem, gender)
      }
    }
  }

  private val house陽空: IHouse<*> = object : HouseYearImpl(陽空) {
    override fun getBranch(t: StemBranch): Branch {
      return fun陽空.invoke(t)
    }
  }

  private val house陰空: IHouse<*> = object : HouseYearImpl(陰空) {
    override fun getBranch(t: StemBranch): Branch {
      return fun陰空.invoke(t)
    }
  }

  /** 截空 : 正空 (截路)  */
  private val house正空: IHouse<*> = object : HouseYearStemImpl(正空) {
    override fun getBranch(t: Stem): Branch {
      return fun正空_A.invoke(t)
    }
  }

  /** 截空 : 傍空 (空亡)  */
  private val house傍空: IHouse<*> = object : HouseYearStemImpl(傍空) {
    override fun getBranch(t: Stem): Branch {
      return fun傍空_A.invoke(t)
    }
  }

  /** 紅艷  */
  private val house紅艷: IHouse<*> = object : HouseYearStemImpl(紅艷) {
    override fun getBranch(t: Stem): Branch {
      throw RuntimeException("Error")
    }

    override fun getBranch(lunarYear: StemBranch,
                           solarYear: StemBranch,
                           monthBranch: Branch,
                           finalMonthNumForMonthStars: Int,
                           solarTerms: SolarTerms,
                           days: Int,
                           hour: Branch,
                           state: Int,
                           gender: Gender,
                           leap: Boolean,
                           prevMonthDays: Int,
                           predefinedMainHouse: Branch?,
                           context: IZiweiContext): Branch {
      return when (context.redBeauty) {
        RedBeauty.RED_BEAUTY_SAME -> fun紅艷_甲乙相同.invoke(if (context.yearType == YearType.YEAR_LUNAR) lunarYear.stem else solarYear.stem)
        RedBeauty.RED_BEAUTY_DIFF -> fun紅艷_甲乙相異.invoke(if (context.yearType == YearType.YEAR_LUNAR) lunarYear.stem else solarYear.stem)
      }
    }
  }

  // =======↑↑↑======= 以上      雜曜 =======↑↑↑=======

  // =======↓↓↓======= 以下 博士12神煞 =======↓↓↓=======

  private val house博士: IHouse<*> = HouseDoctorImpl(博士)

  private val house力士: IHouse<*> = HouseDoctorImpl(力士)

  private val house青龍: IHouse<*> = HouseDoctorImpl(青龍)

  private val house小耗: IHouse<*> = HouseDoctorImpl(StarDoctor.小耗)

  private val house將軍: IHouse<*> = HouseDoctorImpl(將軍)

  private val house奏書: IHouse<*> = HouseDoctorImpl(奏書)

  private val house飛廉: IHouse<*> = HouseDoctorImpl(飛廉)

  private val house喜神: IHouse<*> = HouseDoctorImpl(喜神)

  private val house病符: IHouse<*> = HouseDoctorImpl(StarDoctor.病符)

  private val house大耗: IHouse<*> = HouseDoctorImpl(大耗)

  private val house伏兵: IHouse<*> = HouseDoctorImpl(伏兵)

  private val house官府: IHouse<*> = HouseDoctorImpl(官府)

  // =======↑↑↑======= 以上 博士12神煞 =======↑↑↑=======

  // =======↓↓↓======= 以下 長生12神煞 =======↓↓↓=======
  private val house長生: IHouse<*> = HouseStarLongevityImpl(長生)

  private val house沐浴: IHouse<*> = HouseStarLongevityImpl(沐浴)

  private val house冠帶: IHouse<*> = HouseStarLongevityImpl(冠帶)

  private val house臨官: IHouse<*> = HouseStarLongevityImpl(臨官)

  private val house帝旺: IHouse<*> = HouseStarLongevityImpl(帝旺)

  private val house衰: IHouse<*> = HouseStarLongevityImpl(衰)

  private val house病: IHouse<*> = HouseStarLongevityImpl(病)

  private val house死: IHouse<*> = HouseStarLongevityImpl(死)

  private val house墓: IHouse<*> = HouseStarLongevityImpl(墓)

  private val house絕: IHouse<*> = HouseStarLongevityImpl(絕)

  private val house胎: IHouse<*> = HouseStarLongevityImpl(胎)

  private val house養: IHouse<*> = HouseStarLongevityImpl(養)
  // =======↑↑↑======= 以上 長生12神煞 =======↑↑↑=======


  // =======↓↓↓======= 以下 將前12星 =======↓↓↓=======
  private val house將前_將星: IHouse<*> = HouseGeneralFrontImpl(將星)

  private val house將前_攀鞍: IHouse<*> = HouseGeneralFrontImpl(攀鞍)

  private val house將前_歲馹: IHouse<*> = HouseGeneralFrontImpl(歲馹)

  private val house將前_息神: IHouse<*> = HouseGeneralFrontImpl(息神)

  private val house將前_華蓋: IHouse<*> = HouseGeneralFrontImpl(StarGeneralFront.華蓋)

  private val house將前_劫煞: IHouse<*> = HouseGeneralFrontImpl(劫煞)

  private val house將前_災煞: IHouse<*> = HouseGeneralFrontImpl(災煞)

  private val house將前_天煞: IHouse<*> = HouseGeneralFrontImpl(天煞)

  private val house將前_指背: IHouse<*> = HouseGeneralFrontImpl(指背)

  private val house將前_咸池: IHouse<*> = HouseGeneralFrontImpl(StarGeneralFront.咸池)

  private val house將前_月煞: IHouse<*> = HouseGeneralFrontImpl(月煞)

  private val house將前_亡神: IHouse<*> = HouseGeneralFrontImpl(亡神)

  // =======↑↑↑======= 以上 將前12星 =======↑↑↑=======


  // =======↓↓↓======= 以下 歲前12星 =======↓↓↓=======
  private val house歲前_歲建: IHouse<*> = HouseStarYearFrontImpl(歲建)

  private val house歲前_晦氣: IHouse<*> = HouseStarYearFrontImpl(晦氣)

  private val house歲前_喪門: IHouse<*> = HouseStarYearFrontImpl(喪門)

  private val house歲前_貫索: IHouse<*> = HouseStarYearFrontImpl(貫索)

  private val house歲前_官符: IHouse<*> = HouseStarYearFrontImpl(官符)

  private val house歲前_小耗: IHouse<*> = HouseStarYearFrontImpl(StarYearFront.小耗)

  private val house歲前_歲破: IHouse<*> = HouseStarYearFrontImpl(歲破)

  private val house歲前_龍德: IHouse<*> = HouseStarYearFrontImpl(龍德)

  private val house歲前_白虎: IHouse<*> = HouseStarYearFrontImpl(白虎)

  private val house歲前_天德: IHouse<*> = HouseStarYearFrontImpl(StarYearFront.天德)

  private val house歲前_吊客: IHouse<*> = HouseStarYearFrontImpl(吊客)

  private val house歲前_病符: IHouse<*> = HouseStarYearFrontImpl(StarYearFront.病符)

  // =======↑↑↑======= 以上 歲前12星 =======↑↑↑=======


  private val iHouseSet: Set<IHouse<*>> = setOf(
    // 14主星
    house紫微, house天機, house太陽, house武曲, house天同, house廉貞, house天府, house太陰, house貪狼, house巨門, house天相, house天梁, house七殺, house破軍

    // 八吉星
    ,house文昌, house文曲, house左輔, house右弼, house天魁, house天鉞, house祿存, house天馬 //, house年馬, house月馬

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

  val map : Map<ZStar , IHouse<*>> = iHouseSet.map { it: IHouse<*> ->
    Pair(it.star,it)
  }.toMap()

//  val map: Map<ZStar, IHouse<*>> = iHouseSet.stream()
//    .collect<Map<ZStar, IHouse<*>>, Any>(Collectors.toMap<IHouse, ZStar, IHouse>(Function<IHouse, ZStar> { it.getStar() }) { iHouse -> iHouse })

}
