/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.*
import java.io.Serializable

data class HouseCalContext(/** 「陰曆」的年干支 */
                           val lunarYear: StemBranch,
                           /** 「節氣」的年干支 */
                           val solarYear: StemBranch,
                           /** 「節氣」的月令 */
                           val monthBranch: Branch,
                           /** 最終依據的月令數字 */
                           val finalMonthNumForMonthStars: Int,
                           val solarTerms: SolarTerms,
                           /** lunar days */
                           val days: Int,
                           val hour: Branch,
                           /** 五行局 */
                           val state: Int,
                           val gender: Gender,
                           /** 是否是閏月 */
                           val leap: Boolean,
                           /** 「陰曆」 上個月有幾日 */
                           val prevMonthDays: Int,
                           val config: IZiweiConfig,
                           /** 預先計算好的「命宮」 */
                           val predefinedMainHouse: Branch?,
                           val purpleStarBranchImplMap: Map<PurpleStarBranch, IPurpleStarBranch>,
                           val tianyiImplMap: Map<Tianyi, ITianyi>
                           ) : Serializable {
  val year : StemBranch
    get() {
      return if (config.yearType == YearType.YEAR_LUNAR)
        lunarYear
      else
        solarYear
    }
}

interface IHouse<T> {

  val star: ZStar

  fun getBranch(t: T): Branch

  fun getBranch(context: HouseCalContext): Branch
}
