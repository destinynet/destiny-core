/**
 * @author smallufo
 * @date 2002/8/19 , @date 2009/6/23 改以 HexagramIF 計算
 * @time 下午 11:04:01
 */
package destiny.iching

import destiny.iching.divine.HexagramDivinationComparator

/**
 * 取得卦的排列順序 , 1 <= int <= 64
 * 只有 [HexagramDefaultComparator] (周易卦序) 以及
 * [HexagramDivinationComparator] (六爻卦序) 會使用到
 */
interface IHexagramSequence {

  /**
   * 傳回卦序
   * @param hexagram 一個卦的資料(Hexagram)
   * @return 卦序 1 <= int <=64
   */
  fun getIndex(hexagram: IHexagram): Int


  /**
   * 從卦序傳回卦
   * @param index : 1<=index <=64
   * @return
   */
  fun getHexagram(index: Int): IHexagram

}
