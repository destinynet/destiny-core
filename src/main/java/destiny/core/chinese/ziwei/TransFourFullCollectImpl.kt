/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.T4Value.*

/**
 * 紫微斗數全集、中州派（陸斌兆）、欽天門
 *
 *
 * 五十五年二月間，神州出版社出版了《十八飛星策天紫微斗數全集》( 下簡稱《全集》) ，由周應祥發行，
 * 著作人是大宋華山希夷陳圖南著；隱逸玉蟾白先生增。
 *
 * 此書在編者的凡例上，竟然提到：世傳之紫微斗數，計分南、北兩派，北派即本書，屬於真傳正統，奇驗無比，
 * 南派即流傳之俗本，是後人所偽託希夷之名(清朝無聊之徒所纂改)，而不應驗，純屬欺人之偽書…
 *
 * 而這本書自稱的北派，其實和集文書局在七十一年出版的《十八飛星策天紫微斗數全集》，其內容是完全相同，
 * 只是木刻的版本不一樣而已，因此李亨利老師認為，紫微斗數只有《全書》和《全集》之分，
 * 根本沒有南派和北派之說，更不要說爾後陸續出現的透派、四化派，甚至是中洲派等的派別。
 */

class TransFourFullCollectImpl : TransFourAbstractImpl(TransFour.FullCollect) {

  override val table
    get() = dataTable

  companion object {
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

      // 庚 有差別 (比較正確)
      , Triple(庚, 祿, StarMain.太陽)
      , Triple(庚, 權, StarMain.武曲)
      , Triple(庚, 科, StarMain.太陰)
      , Triple(庚, 忌, StarMain.天同)

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
      , Triple(癸, 科, StarMain.太陰) // 不正確
      , Triple(癸, 忌, StarMain.貪狼)
    )

    const val VALUE = "collect"
  }

}
