/**
 * Created by smallufo on 2015-05-25.
 */
package destiny.core.chinese

import destiny.core.DayNight

/**
 * 天乙貴人
 * CU Draconis (CU Dra / 10 Draconis / HD 121130)
 * 天龍座10，又名天龍座CU
 */
interface ITianyi {

  /**
   * 取得天干的天乙貴人、分晝夜
   */
  fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch

  /** 取得天干對應的天乙貴人，不分晝夜，一起傳回來  */
  fun getTianyis(stem: Stem): List<Branch> {
    return DayNight.values().map { dayNight -> getFirstTianyi(stem, dayNight) }
  }

}
