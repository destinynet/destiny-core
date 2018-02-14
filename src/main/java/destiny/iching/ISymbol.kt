/**
 * @author smallufo
 * @date 2002/8/13
 * @time 下午 05:38:34
 */
package destiny.iching

/**
 * Symbol(八卦) 的介面 , 主要 methods : getYinYang(1<=index<=3)
 */
internal interface ISymbol {

  /**
   * 取得一個卦的第幾爻
   * index 值為 1,2 or 3
   */
  fun getBooleanValue(index: Int): Boolean

}
