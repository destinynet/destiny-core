/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology.eclipse

import java.io.Serializable

/** 日食、月食 的最上層 抽象 class  */
abstract class AbstractEclipse(val begin: Double,
                               /** 不論是 全食、偏食、還是環食，都會有「最大值」  */
                               val max: Double, val end: Double) : Serializable
