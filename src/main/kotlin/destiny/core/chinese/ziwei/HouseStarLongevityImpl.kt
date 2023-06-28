/**
 * Created by smallufo on 2017-12-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.IYinYang
import destiny.core.chinese.StemBranch

/**
 * 長生 12 神煞
 *
 * 形容五行局在十二宮力量的強弱。這十二神煞影響著斗數十四顆主星的氣運。
 *
 * 重點是「五行局」， 而「五行局」又來自「命宮」
 * 但是「命宮」又有可能是依據上升星座而來，「可能」並非來自 陰曆 年份＋月份＋時辰來看
 */
class HouseStarLongevityImpl(star: StarLongevity) : HouseAbstractImpl<Triple<FiveElement, Gender, IYinYang>>(star) {

  override fun getBranch(t: Triple<FiveElement, Gender, IYinYang>): Branch {
    return StarLongevity.starFuncMap[star]!!.invoke(t.first, t.second, t.third)
  }

  override fun getBranch(context: HouseCalContext): Branch {
    return context.run {

      val stemOf寅 = Ziwei.getStemOf寅(context.year.stem)

      val mainHouse = predefinedMainHouse ?: Ziwei.getMainHouseBranch(finalMonthNumForMonthStars, hour)

      // 左下角，寅宮 的 干支
      val stemBranchOf寅 = StemBranch[stemOf寅, Branch.寅]
      val steps = mainHouse.getAheadOf(Branch.寅)
      val 命宮 = stemBranchOf寅.next(steps)

      //StemBranch 命宮 = IZiwei.getMainHouse(lunarYear.getStem() , finalMonthNumForMonthStars, hour);
      val (fiveElement) = Ziwei.getMainDesc(命宮)
      // 五行局數
      getBranch(Triple<FiveElement, Gender, IYinYang>(fiveElement, gender, lunarYear.stem))
    }
  }

}
