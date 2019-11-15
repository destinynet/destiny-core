/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:20:08
 */
package destiny.fengshui

abstract class AbstractSymbol<T> : ICompass<T> {

  override val initDegree: Double
    get() = 337.5

  override val stepDegree: Double
    get() = 45.0


}
