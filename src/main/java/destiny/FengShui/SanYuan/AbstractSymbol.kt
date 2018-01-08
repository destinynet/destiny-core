/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:20:08
 */
package destiny.FengShui.SanYuan

abstract class AbstractSymbol : AbstractCompass() {

  override val initDegree: Double
    get() = 337.5

  override val stepDegree: Double
    get() = 45.0


}
