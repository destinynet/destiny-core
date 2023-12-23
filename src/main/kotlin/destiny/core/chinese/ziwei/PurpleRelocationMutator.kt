package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import jakarta.inject.Named

/**
 * 紫微星變盤
 */
@Named
class PurpleRelocationMutator(private val prevMonthDaysImpl: IPrevMonthDays) : IHouseMutator, java.io.Serializable {

  override fun mutate(plate: IPlate, config: IZiweiConfig): Set<HouseData>? {

    return config.purpleFixedBranch?.let { purpleFixedBranch ->
      val prevMonthDays = if (plate.chineseDate.leapMonth)
        prevMonthDaysImpl.getPrevMonthDays(plate.chineseDate.cycle!!, plate.chineseDate.year, plate.chineseDate.month, true)
      else
        0

      val newStarMainMap: Map<StarMain, Branch> = config.stars.filterIsInstance<StarMain>().associateWith { starMain ->
        StarMain.starFuncMap[starMain]!!.invoke(
          plate.state, plate.chineseDate.day, plate.chineseDate.leapMonth, prevMonthDays, purpleFixedBranch, PurpleStarBranchDefaultImpl()
        )
      }
      plate.houseDataSet.map { houseData ->
        val branch = houseData.stemBranch.branch
        val newStars = houseData.stars.filterNot { it is StarMain }.toMutableSet().apply {
          addAll(newStarMainMap.filter { it.value == branch }.map { it.key })
        }.toSet()
        houseData.copy(stars = newStars)
      }.toSet()
    }

  }
}
