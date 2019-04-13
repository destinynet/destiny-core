/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.ITransFour.Value.*
import java.util.*


class TransFourNorthImpl : TransFourAbstractImpl() {

  override val table
    get() = dataTable

  override fun getDescription(locale: Locale): String {
    return "北派、神數派"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    /** 北派 , 神數  */
    private val dataTable = listOf(
      Triple(甲, 祿, StarMain.廉貞)
      , Triple(甲, 權, StarMain.破軍)
      , Triple(甲, 科, StarMain.武曲)
      , Triple(甲, 忌, StarMain.太陽)

      , Triple(乙, 祿, StarMain.天機)
      , Triple(乙, 權, StarMain.天梁)
      , Triple(乙, 科, StarMain.紫微)
      , Triple(乙, 忌, StarMain.太陰)

      , Triple(丙, 祿, StarMain.天同)
      , Triple(丙, 權, StarMain.天機)
      , Triple(丙, 科, StarLucky.文昌)
      , Triple(丙, 忌, StarMain.廉貞)

      , Triple(丁, 祿, StarMain.太陰)
      , Triple(丁, 權, StarMain.天同)
      , Triple(丁, 科, StarMain.天機)
      , Triple(丁, 忌, StarMain.巨門)

      // 戊 有差別
      , Triple(戊, 祿, StarMain.貪狼)
      , Triple(戊, 權, StarMain.太陰)
      , Triple(戊, 科, StarLucky.右弼)
      , Triple(戊, 忌, StarMain.天機)

      , Triple(己, 祿, StarMain.武曲)
      , Triple(己, 權, StarMain.貪狼)
      , Triple(己, 科, StarMain.天梁)
      , Triple(己, 忌, StarLucky.文曲)

      // 庚 有差別
      , Triple(庚, 祿, StarMain.太陽)
      , Triple(庚, 權, StarMain.武曲)
      , Triple(庚, 科, StarMain.天同)
      , Triple(庚, 忌, StarMain.天相)

      , Triple(辛, 祿, StarMain.巨門)
      , Triple(辛, 權, StarMain.太陽)
      , Triple(辛, 科, StarLucky.文曲)
      , Triple(辛, 忌, StarLucky.文昌)

      // 壬 有差別
      , Triple(壬, 祿, StarMain.天梁)
      , Triple(壬, 權, StarMain.紫微)
      , Triple(壬, 科, StarLucky.左輔)
      , Triple(壬, 忌, StarMain.武曲)

      , Triple(癸, 祿, StarMain.破軍)
      , Triple(癸, 權, StarMain.巨門)
      , Triple(癸, 科, StarMain.太陰)
      , Triple(癸, 忌, StarMain.貪狼)

    )
  }
}
