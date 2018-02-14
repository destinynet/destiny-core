/**
 * @author smallufo
 * Created on 2002/8/9 at 下午 03:37:51
 */
package destiny.core.chinese

/** 取得此元素的陰陽值 : getYinYang()  */
interface IYinYang {

  val booleanValue: Boolean

  companion object {

    val 陽 = { true }

    val 陰 = { false }
  }
}
