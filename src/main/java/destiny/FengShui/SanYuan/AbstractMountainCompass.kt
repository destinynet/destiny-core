/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.FengShui.SanYuan

import destiny.astrology.Utils

abstract class AbstractMountainCompass : AbstractCompass<Mountain>() {

  /**
   * 取得 "子" 山的起始度數 (地盤正針得傳回 352.5 度)
   * 由繼承此 Class 的子物件去實作
   */
  abstract override val initDegree: Double

  override val stepDegree: Double
    get() = 15.0

  /**
   * 取得某個山的起始度數
   */
  override fun getStartDegree(t: Mountain): Double {
    return Utils.getNormalizeDegree(t.index * stepDegree + initDegree)
  }

  /**
   * 取得某個山的結束度數
   */
  override fun getEndDegree(t: Mountain): Double {
    return Utils.getNormalizeDegree((t.index + 1) * stepDegree + initDegree)
  }
  
  /**
   * 取得目前這個度數位於哪個山當中
   */
  fun getMnt(degree: Double): Mountain {
    var index = ((degree + 360 - initDegree) / stepDegree).toInt()
    if (index >= 24)
      index -= 24
    else if (index < 0)
      index += 24
    return Mountain.values()[index]
  }
  
  /**
   * http://www.neighbor168.com/name543/house11.htm
   * <pre>
   * 寅－－內藏甲、丙、戊，已知甲丙屬陽，故寅為陽。
   * 申－－內藏庚、壬、戊，已知庚壬屬陽，故申屬陽。
   * 巳－－內藏庚、丙、戊，已知庚丙屬陽，故巳屬陽。
   * 亥－－內藏甲、壬、戊，已知甲壬屬陽，故亥屬陽。
   *
   * 辰－－內藏乙、癸、戊，已知乙癸屬陰，故辰屬陰。
   * 戌－－內藏辛、丁、戊，已知辛丁屬陰，故戌屬陰。
   * 丑－－內藏癸、辛、己，已知癸辛屬陰，故丑屬陰。
   * 未－－內藏丁、乙、己，己知丁乙屬陰，故未屬陰。
  </pre> *
   */
  fun getYinYang(m: Mountain): Boolean {
    return when {
      m.mnt is SealedMnt.MntSymbol -> true
      m.mnt is SealedMnt.MntStem -> // 陽干傳回陽 , 陰干傳回陰
        m.mnt.stem.booleanValue
      m.mnt is SealedMnt.MntBranch -> {
        val index = m.mnt.branch.index
        //寅巳申亥
        index == 2 || index == 5 || index == 8 || index == 11
      }
      else -> throw RuntimeException("Cannot find YinYang from " + m)
    }
  }
}